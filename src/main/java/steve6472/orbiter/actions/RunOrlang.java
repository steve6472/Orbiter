package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.log.Log;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.Orlang;
import steve6472.orlang.codec.OrCode;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record RunOrlang(OrCode condition, EntitySelection entitySelection, OrCode code) implements Action
{
    private static final Logger LOGGER = Log.getLogger(RunOrlang.class);

    public static final Codec<RunOrlang> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        OrCode.CODEC.fieldOf("code").forGetter(RunOrlang::code)
    ).apply(instance, RunOrlang::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        try
        {
            Orlang.interpreter.interpret(code, environment.env);
        } catch (Exception ex)
        {
            LOGGER.severe("Error while interpreting orlang: '%s'".formatted(code.codeStr()));
            throw ex;
        }
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.RUN_ORLANG;
    }
}
