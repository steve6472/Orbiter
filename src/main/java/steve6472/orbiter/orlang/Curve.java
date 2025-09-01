package steve6472.orbiter.orlang;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

			// expected in 0-1
			double v = evaluateChain(nodes, input);

			environment.setValue(name, OrlangValue.num(v));
		} else
		{
			List<OrNumValue> nodes = this.nodes.left().orElseThrow(() -> new IllegalArgumentException("Incorrect chain type"));
			double input = this.input.evaluateAndGet(environment);
			double range = this.horizontalRange.evaluateAndGet(environment);
			double T = Math.clamp(input / range, 0, 1);

			int index = Math.clamp((int) (T * (nodes.size() - 1)), 0, nodes.size() - 1);
			int next = Math.clamp(index + 1, 0, nodes.size() - 1);

			double v = switch (type)
			{
				case LINEAR -> (float) MathUtil.lerp(nodes.get(index).evaluateAndGet(environment), nodes.get(next).evaluateAndGet(environment), T);
				case BEZIER -> {
					if (nodes.size() != 4)
						throw new IllegalArgumentException("Bezier curve requires 4 nodes!");

                    double P0 = nodes.get(0).evaluateAndGet(environment);
					double P1 = nodes.get(1).evaluateAndGet(environment);
					double P2 = nodes.get(2).evaluateAndGet(environment);
					double P3 = nodes.get(3).evaluateAndGet(environment);
                    yield Math.pow((1 - T), 3) * P0 + 3 * T * Math.pow((1 - T), 2) * P1 + 3 * Math.pow(T, 2) * (1 - T) * P2 + Math.pow(T, 3) * P3;
				}
				case CATMULL_ROM -> {

					int past = Math.clamp((int) (T * (nodes.size() - 3)), 0, nodes.size() - 1);
					index = Math.clamp(past + 1, 0, nodes.size() - 2);
					next = Math.clamp(index + 1, 0, nodes.size() - 2);
					int future = Math.clamp(next + 1, 0, nodes.size() - 1);

					double start = past / ((double) nodes.size() - 1);
					double end = future / ((double) nodes.size() - 1);

					double t = MathUtil.time(start, end, T);

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
		LINEAR, CATMULL_ROM, BEZIER, BEZIER_CHAIN;

		public static final Codec<CurveType> CODEC = StringValue.fromValues(CurveType::values);

		@Override
		public String stringValue()
		{
			return name().toLowerCase(Locale.ROOT);
		}
	}

	private record ChainEntry(double t, ChainNode node) {}

	private record ChainNode(double leftValue, double rightValue, double leftSlope, double rightSlope)
	{
		private static final MapCodec<Pair<Double, Double>> LR_VALUE = Codec.mapPair(Codec.DOUBLE.fieldOf("left_value"), Codec.DOUBLE.fieldOf("right_value"));
		private static final MapCodec<Either<Pair<Double, Double>, Double>> VALUE = Codec.mapEither(LR_VALUE, Codec.DOUBLE.fieldOf("value"));

		private static final MapCodec<Pair<Double, Double>> LR_SLOPE = Codec.mapPair(Codec.DOUBLE.fieldOf("left_slope"), Codec.DOUBLE.fieldOf("right_slope"));
		private static final MapCodec<Either<Pair<Double, Double>, Double>> SLOPE = Codec.mapEither(LR_SLOPE, Codec.DOUBLE.fieldOf("slope"));

		private ChainNode(Either<Pair<Double, Double>, Double> value, Either<Pair<Double, Double>, Double> slope)
		{
			this(left(value), right(value), left(slope), right(slope));
		}

		private static double left(Either<Pair<Double, Double>, Double> v)
		{
			Optional<Pair<Double, Double>> left = v.left();
			Optional<Double> right = v.right();
			if (left.isPresent())
			{
				return left.get().getFirst();
			}
			else if (right.isPresent())
			{
				return right.get();
			} else
			{
				throw new IllegalArgumentException("Left nor Right are filled!");
			}
		}

		private static double right(Either<Pair<Double, Double>, Double> v)
		{
			Optional<Pair<Double, Double>> left = v.left();
			Optional<Double> right = v.right();
			if (left.isPresent())
			{
				return left.get().getSecond();
			}
			else if (right.isPresent())
			{
				return right.get();
			} else
			{
				throw new IllegalArgumentException("Left nor Right are filled!");
			}
		}

		public static final Codec<ChainNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			VALUE.forGetter(null),
			SLOPE.forGetter(null)
		).apply(instance, ChainNode::new));
	}

	private static double evaluateChain(List<ChainEntry> entries, double t)
	{
		if (entries == null || entries.size() < 2)
		{
			throw new IllegalArgumentException("At least 2 entries are required");
		}

		// Clamp
		t = Math.clamp(t, 0, 1);

		// Find correct segment
		int segmentIndex = -1;
		for (int i = 0; i < entries.size() - 1; i++)
		{
			if (t >= entries.get(i).t() && t <= entries.get(i + 1).t())
			{
				segmentIndex = i;
				break;
			}
		}

		if (segmentIndex == -1)
		{
			if (t < entries.getFirst().t())
			{
				return entries.getFirst().node.leftValue;
			} else
			{
				return entries.getLast().node.rightValue;
			}
		}

		ChainEntry e0 = entries.get(segmentIndex);
		ChainEntry e1 = entries.get(segmentIndex + 1);

		double localT = (t - e0.t()) / (e1.t() - e0.t());

		return calculateSegment(e0.node, e1.node, Math.clamp(localT, 0, 1));
	}

	private static double calculateSegment(ChainNode left, ChainNode right, double T)
	{
		double P0 = left.rightValue;
		double P1 = left.rightSlope * (1d / 3d) + left.rightValue;
		double P2 = -right.leftSlope * (1d / 3d) + right.leftValue;
		double P3 = right.leftValue;
		return Math.pow((1 - T), 3) * P0 + 3 * T * Math.pow((1 - T), 2) * P1 + 3 * Math.pow(T, 2) * (1 - T) * P2 + Math.pow(T, 3) * P3;
	}
}
