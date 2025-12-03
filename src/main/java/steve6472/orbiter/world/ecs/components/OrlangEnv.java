package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import steve6472.orbiter.util.ComponentCodec;
import steve6472.orbiter.util.OrbiterCodecs;
import steve6472.orbiter.world.QueryFunction;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.AST;
import steve6472.orlang.Curve;
import steve6472.orlang.OrlangEnvironment;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.codec.OrCode;

import java.util.Map;

/**
 * Created by steve6472
 * Date: 9/7/2025
 * Project: Orbiter <br>
 */
public class OrlangEnv implements Component, ParticleComponent
{
    public static final Codec<OrlangEnv> CODEC = ComponentCodec.create(instance -> instance.group(
        Codec.unboundedMap(Codec.STRING, OrbiterCodecs.ORLANG_VALUE).optionalFieldOf("variables", Map.of()).forGetter(e -> e.env.variableMap),
        Codec.unboundedMap(Codec.STRING, OrbiterCodecs.ORLANG_VALUE).optionalFieldOf("temp_variables", Map.of()).forGetter(e -> e.env.tempMap),
        Codec.unboundedMap(Codec.STRING, OrCode.CODEC).optionalFieldOf("expressions", Map.of()).forGetter(e -> e.env.expressions),
        Codec.unboundedMap(AST.Node.Identifier.CODEC, Curve.CODEC).optionalFieldOf("curves", Map.of()).forGetter(e -> e.env.curves),
        QueryFunction.CODEC.optionalFieldOf("query_function", QueryFunction.EMPTY).forGetter(e -> e.queryFunction)
    ).apply(instance, (_, _, _, _, _) -> OrlangEnv::new));

    public OrlangEnvironment env = new OrlangEnvironment();
    public QueryFunction queryFunction;

    public OrlangEnv()
    {

    }

    public OrlangEnv(Map<String, OrlangValue> variables, Map<String, OrlangValue> tempVariables, Map<String, OrCode> expressions, Map<AST.Node.Identifier, Curve> curves, QueryFunction function)
    {
        env.variableMap.putAll(variables);
        env.tempMap.putAll(tempVariables);
        env.expressions.putAll(expressions);
        env.curves.putAll(curves);
        queryFunction = function;
    }

    public void setQueryFunctionSet(QueryFunction function, Entity entity)
    {
        queryFunction = function;
        env.queryFunctionSet = function.createFunctionSet(entity);
    }

    public void pushTempFrom(OrlangEnv from)
    {
        // TODO: replace with stack
        env.tempMap.putAll(from.env.tempMap);
    }

    public void clearTemp()
    {
        env.clearTemp();
    }

    @Override
    public void reset()
    {
        env.reset();
    }
}
