package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.core.Blueprint;
import steve6472.orlang.*;
import steve6472.orlang.codec.OrCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record EnvironmentBlueprint(
    Optional<OrCode> init,
    Optional<OrCode> tick,
    Optional<OrCode> frame,
    Optional<Map<AST.Node.Identifier, Curve>> curves) implements Blueprint<EnvironmentBlueprint>
{
    public static final Key KEY = Constants.key("environment");
    public static final Codec<EnvironmentBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrCode.CODEC.optionalFieldOf("init").forGetter(EnvironmentBlueprint::init),
        OrCode.CODEC.optionalFieldOf("tick").forGetter(EnvironmentBlueprint::tick),
        OrCode.CODEC.optionalFieldOf("frame").forGetter(EnvironmentBlueprint::frame),
        Codec.unboundedMap(AST.Node.Identifier.CODEC, Curve.CODEC).optionalFieldOf("curves").forGetter(EnvironmentBlueprint::curves)
    ).apply(instance, EnvironmentBlueprint::new));

    @Override
    public List<Component> createComponents()
    {
        OrlangEnv component = new OrlangEnv();
        init.ifPresent(init -> Orlang.interpreter.interpret(init, component.env));
        tick.ifPresent(tick -> component.env.expressions.put("tick", tick));
        frame.ifPresent(frame -> component.env.expressions.put("frame", frame));
        curves.ifPresent(curves -> component.env.curves.putAll(curves));
        return List.of(component);
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<EnvironmentBlueprint> codec()
    {
        return CODEC;
    }
}
