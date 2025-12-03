package steve6472.orbiter.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import steve6472.core.registry.Key;
import steve6472.core.registry.Type;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public final class ActionType<T extends Action> extends Type<T>
{
    public static final ActionType<Empty> EMPTY = register("empty", Empty.CODEC);
    public static final ActionType<Sequence> SEQUENCE = register("sequence", Sequence.CODEC);
    public static final ActionType<AddComponentGroups> ADD_COMPONENT_GROUPS = register("add_component_groups", AddComponentGroups.CODEC);
    public static final ActionType<RemoveComponentGroups> REMOVE_COMPONENT_GROUPS = register("remove_component_groups", RemoveComponentGroups.CODEC);
    public static final ActionType<ApplyProperties> APPLY_PROPERTIES = register("apply_properties", ApplyProperties.CODEC);
    public static final ActionType<RunOrlang> RUN_ORLANG = register("orlang", RunOrlang.CODEC);
    public static final ActionType<Schedule> SCHEDULE = register("schedule", Schedule.CODEC);
    public static final ActionType<ForEach> FOR_EACH = register("for_each", ForEach.CODEC);
    public static final ActionType<CallEvent> CALL_EVENT = register("call_event", CallEvent.CODEC);
    public static final ActionType<CreateEntity> CREATE_ENTITY = register("create_entity", CreateEntity.CODEC);
    public static final ActionType<IfElse> IF_ELSE = register("if_else", IfElse.CODEC);
    public static final ActionType<AnimationForceTransition> ANIMATION_FORCE_TRANSITION = register("animation_force_transition", AnimationForceTransition.CODEC);
    public static final ActionType<RemoveEntity> REMOVE_ENTITY = register("remove_entity", RemoveEntity.CODEC);

    public static final ActionType<DebugLog> DEBUG_LOG = register("debug_log", DebugLog.CODEC);
    public static final ActionType<DisplayGizmo> DISPLAY_GIZMO = register("gizmo", DisplayGizmo.CODEC);

    public ActionType(Key key, MapCodec<T> codec)
    {
        super(key, codec);
    }

    private static <T extends Action> ActionType<T> register(String id, Codec<T> codec)
    {
        var obj = new ActionType<>(Constants.key(id), MapCodec.assumeMapUnsafe(codec));
        Registries.ACTION.register(obj);
        return obj;
    }

    public static void bootstrap()
    {
    }
}
