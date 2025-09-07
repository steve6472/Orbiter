package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.*;
import steve6472.orlang.codec.OrCode;

import java.util.Map;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleEnvironmentBlueprint(
    Optional<Map<AST.Node.Identifier, AST.Node.Identifier>> passVariables,
    Optional<OrCode> init,
    Optional<OrCode> tick,
    Optional<OrCode> frame,
    Optional<Map<AST.Node.Identifier, Curve>> curves) implements PCBlueprint<ParticleEnvironmentBlueprint>
{
    public static final Key KEY = Constants.key("environment");
    public static final Codec<ParticleEnvironmentBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(AST.Node.Identifier.CODEC, AST.Node.Identifier.CODEC).optionalFieldOf("pass_variables").forGetter(ParticleEnvironmentBlueprint::passVariables),
        OrCode.CODEC.optionalFieldOf("init").forGetter(ParticleEnvironmentBlueprint::init),
        OrCode.CODEC.optionalFieldOf("tick").forGetter(ParticleEnvironmentBlueprint::tick),
        OrCode.CODEC.optionalFieldOf("frame").forGetter(ParticleEnvironmentBlueprint::frame),
        Codec.unboundedMap(AST.Node.Identifier.CODEC, Curve.CODEC).optionalFieldOf("curves").forGetter(ParticleEnvironmentBlueprint::curves)
    ).apply(instance, ParticleEnvironmentBlueprint::new));

    public static final ParticleEnvironmentBlueprint EMPTY = new ParticleEnvironmentBlueprint(
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        OrlangEnv component = particleEngine.createComponent(OrlangEnv.class);
        passVariables().ifPresent(map -> {
            map.forEach((fromEmitter, toParticle) -> {
                OrlangValue value = environment.getValue(fromEmitter);
                if (value != null)
                    component.env.setValue(toParticle, value);
                else
                    throw new NullPointerException("Variable " + fromEmitter + " not found in emitter environment");
            });
        });

        init.ifPresent(init -> Orlang.interpreter.interpret(init, component.env));
        tick.ifPresent(tick -> component.env.expressions.put("tick", tick));
        frame.ifPresent(frame -> component.env.expressions.put("frame", frame));
        curves.ifPresent(curves -> component.env.curves.putAll(curves));
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleEnvironmentBlueprint> codec()
    {
        return CODEC;
    }
}
