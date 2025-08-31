package steve6472.orbiter.orlang;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.StringValue;
import steve6472.core.util.MathUtil;
import steve6472.orbiter.orlang.codec.OrNumValue;

import java.util.List;
import java.util.Locale;

/**********************
 * Created by steve6472
 * On date: 11/4/2021
 * Project: VoxWorld<br>
 *
 ***********************/
public record Curve(CurveType type, OrNumValue input, OrNumValue horizontalRange, List<OrNumValue> nodes)
{
	public static final Codec<Curve> CODEC = RecordCodecBuilder.create(instance -> instance.group(
	    CurveType.CODEC.fieldOf("type").forGetter(Curve::type),
		OrNumValue.CODEC.fieldOf("input").forGetter(Curve::input),
		OrNumValue.CODEC.fieldOf("horizontal_range").forGetter(Curve::horizontalRange),
		OrNumValue.CODEC.listOf().fieldOf("nodes").forGetter(Curve::nodes)
	).apply(instance, Curve::new));

	public void calculate(AST.Node.Identifier name, OrlangEnvironment environment)
	{
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
			};

		environment.setValue(name, OrlangValue.num(v));
	}

	public enum CurveType implements StringValue
	{
		LINEAR, CATMULL_ROM;

		public static final Codec<CurveType> CODEC = StringValue.fromValues(CurveType::values);

		@Override
		public String stringValue()
		{
			return name().toLowerCase(Locale.ROOT);
		}
	}
}
