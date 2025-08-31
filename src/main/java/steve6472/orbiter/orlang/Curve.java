package steve6472.orbiter.orlang;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.StringValue;
import steve6472.core.util.MathUtil;
import steve6472.orbiter.orlang.codec.OrNumValue;

import java.util.*;

/**********************
 * Created by steve6472
 * On date: 11/4/2021
 * Project: VoxWorld<br>
 *
 ***********************/
public record Curve(CurveType type, OrNumValue input, OrNumValue horizontalRange, Either<List<OrNumValue>, List<ChainEntry>> nodes)
{
	private static final Codec<Either<List<OrNumValue>, List<ChainEntry>>> EITHER_NODES =
		Codec.either(
			OrNumValue.CODEC.listOf(),
			Codec.unboundedMap(
				Codec.STRING,
				ChainNode.CODEC).xmap(map -> {
					List<ChainEntry> entries = new ArrayList<>(map.size());
					map.forEach((t, n) -> entries.add(new ChainEntry(Double.parseDouble(t), n)));
					entries.sort(Comparator.comparingDouble(ChainEntry::t));
					return entries;
			}, entries -> {
				Map<String, ChainNode> map = new HashMap<>(entries.size());
				for (ChainEntry entry : entries)
				{
					map.put(Double.toString(entry.t), entry.node);
				}
				return map;
			})
		);

	public static final Codec<Curve> CODEC = RecordCodecBuilder.create(instance -> instance.group(
	    CurveType.CODEC.fieldOf("type").forGetter(Curve::type),
		OrNumValue.CODEC.fieldOf("input").forGetter(Curve::input),
		OrNumValue.CODEC.optionalFieldOf("horizontal_range", new OrNumValue(1)).forGetter(Curve::horizontalRange),
		EITHER_NODES.fieldOf("nodes").forGetter(Curve::nodes)
	).apply(instance, Curve::new));

	public void calculate(AST.Node.Identifier name, OrlangEnvironment environment)
	{
		if (type == CurveType.BEZIER_CHAIN)
		{
			List<ChainEntry> nodes = this.nodes.right().orElseThrow(() -> new IllegalArgumentException("Incorrect chain type"));
			double input = this.input.evaluateAndGet(environment);
			double v = evaluateChain(nodes, input);
			environment.setValue(name, OrlangValue.num(v));
		} else
		{
			List<OrNumValue> nodes = this.nodes.left().orElseThrow(() -> new IllegalArgumentException("Incorrect chain type"));
			double input = this.input.evaluateAndGet(environment);
			double range = this.horizontalRange.evaluateAndGet(environment);
			double indexSelector = Math.clamp(input / range, 0, 1);

			int index = Math.clamp((int) (indexSelector * (nodes.size() - 1)), 0, nodes.size() - 1);
			int next = Math.clamp(index + 1, 0, nodes.size() - 1);

			double start = index / ((double) nodes.size() - 1);
			double end = next / ((double) nodes.size() - 1);

			double t = MathUtil.time(start, end, indexSelector);

			double v = switch (type)
			{
				case LINEAR -> (float) MathUtil.lerp(nodes.get(index).evaluateAndGet(environment), nodes.get(next).evaluateAndGet(environment), t);
				case CATMULL_ROM -> {

					int past = Math.clamp((int) (indexSelector * (nodes.size() - 3)), 0, nodes.size() - 1);
					index = Math.clamp(past + 1, 0, nodes.size() - 2);
					next = Math.clamp(index + 1, 0, nodes.size() - 2);
					int future = Math.clamp(next + 1, 0, nodes.size() - 1);

					start = past / ((double) nodes.size() - 1);
					end = future / ((double) nodes.size() - 1);

					t = MathUtil.time(start, end, indexSelector);

					double p0 = nodes.get(past).evaluateAndGet(environment);
					double p1 = nodes.get(index).evaluateAndGet(environment);
					double p2 = nodes.get(next).evaluateAndGet(environment);
					double p3 = nodes.get(future).evaluateAndGet(environment);

	                yield MathUtil.catmullLerp(p0, p1, p2, p3, t);
				}
				case BEZIER_CHAIN -> throw new IllegalStateException();
			};
			environment.setValue(name, OrlangValue.num(v));
		}
	}

	public enum CurveType implements StringValue
	{
		LINEAR, CATMULL_ROM, BEZIER_CHAIN;

		public static final Codec<CurveType> CODEC = StringValue.fromValues(CurveType::values);

		@Override
		public String stringValue()
		{
			return name().toLowerCase(Locale.ROOT);
		}
	}

	private record ChainEntry(double t, ChainNode node) {}

	private record ChainNode(double value, double leftValue, double rightValue, double slope, double leftSlope, double rightSlope)
	{
		public static final Codec<ChainNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.fieldOf("value").forGetter(ChainNode::value),
			Codec.DOUBLE.optionalFieldOf("left_value", 0d).forGetter(ChainNode::leftValue),
			Codec.DOUBLE.optionalFieldOf("right_value", 0d).forGetter(ChainNode::rightValue),
			Codec.DOUBLE.fieldOf("slope").forGetter(ChainNode::slope),
			Codec.DOUBLE.optionalFieldOf("left_slope", 0d).forGetter(ChainNode::leftSlope),
			Codec.DOUBLE.optionalFieldOf("right_slope", 0d).forGetter(ChainNode::rightSlope)
		).apply(instance, ChainNode::new));
	}

	private static double evaluateChain(List<ChainEntry> entries, double t)
	{
		if (entries == null || entries.size() < 2) {
			throw new IllegalArgumentException("At least 2 entries are required");
		}

		// Clamp
		t = Math.max(0, Math.min(1, t));

		// Find correct segment
		int segmentIndex = -1;
		for (int i = 0; i < entries.size() - 1; i++) {
			if (t >= entries.get(i).t() && t <= entries.get(i + 1).t()) {
				segmentIndex = i;
				break;
			}
		}
		if (segmentIndex == -1) {
			segmentIndex = entries.size() - 2; // t=1 case
		}

		ChainEntry e0 = entries.get(segmentIndex);
		ChainEntry e1 = entries.get(segmentIndex + 1);

		double localT = (t - e0.t()) / (e1.t() - e0.t());

		return evaluateSegment(e0, e1, localT);
	}

	// Evaluate cubic Bezier for one segment
	private static double evaluateSegment(ChainEntry  e0, ChainEntry  e1, double localT)
	{
		double t0 = e0.t();
		double t1 = e1.t();
		double dt = t1 - t0;

		double y0 = e0.node().value();
		double y1 = e1.node().value();

		double m0 = e0.node().slope(); // derivative at start
		double m1 = e1.node().slope(); // derivative at end

		// Control points in (x,y)
		double x0 = t0, y_0 = y0;
		double x3 = t1, y_3 = y1;

		double x1 = x0 + dt / 3.0;
		double y1c = y0 + m0 * dt / 3.0;

		double x2 = x3 - dt / 3.0;
		double y2c = y1 - m1 * dt / 3.0;

		// Cubic Bezier interpolation in y (x is redundant since we know localT)
		double u = 1 - localT;
		return u*u*u * y_0 +
			3*u*u*localT * y1c +
			3*u*localT*localT * y2c +
			localT*localT*localT * y_3;
	}
}
