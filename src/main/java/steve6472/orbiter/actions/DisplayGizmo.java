package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import org.joml.Vector3f;
import steve6472.core.registry.StringValue;
import steve6472.core.util.ExtraCodecs;
import steve6472.orbiter.Constants;
import steve6472.orbiter.rendering.gizmo.GizmoRenderSettings;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.util.OrbiterCodecs;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.codec.OrCode;

import java.util.Locale;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record DisplayGizmo(OrCode condition, EntitySelection entitySelection, Optional<Long> stayFor, boolean fadeOut, Unit unit, boolean alwaysOnTop, GizmoSettings gizmoSettings) implements Action
{
    public static final Codec<DisplayGizmo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Codec.LONG.optionalFieldOf("duration").forGetter(DisplayGizmo::stayFor),
        Codec.BOOL.optionalFieldOf("fade_out", false).forGetter(DisplayGizmo::fadeOut),
        Unit.CODEC.optionalFieldOf("unit", Unit.MILLI).forGetter(DisplayGizmo::unit),
        Codec.BOOL.optionalFieldOf("always_on_top", false).forGetter(DisplayGizmo::fadeOut),
        GizmoSettings.CODEC.fieldOf("gizmo").forGetter(DisplayGizmo::gizmoSettings)
    ).apply(instance, DisplayGizmo::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        Vector3f pos = new Vector3f();
        Components.POSITION.ifPresent(entity, position -> pos.set(position.toVec3f()));
        if (gizmoSettings.offsetLocal)
        {
            Components.ROTATION.ifPresent(entity, rotation -> {
                Vector3f offset = new Vector3f(gizmoSettings.offset).rotate(rotation.toQuat());
                pos.add(offset);
            });
        } else
        {
            pos.add(gizmoSettings.offset);
        }
        gizmoSettings.createGizmo(pos, stayFor, unit, fadeOut, alwaysOnTop);
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.DISPLAY_GIZMO;
    }

    public enum Unit implements StringValue
    {
        MILLI(l -> l),
        SECOND(l -> l * 1000),
        TICK(l -> (long) (l * 1000 / Constants.TICKS_IN_SECOND)),
        MINUTE(l -> l * 60000);

        private final Long2LongFunction toTicks;

        Unit(Long2LongFunction toTicks)
        {
            this.toTicks = toTicks;
        }

        public long toMilli(long value)
        {
            return toTicks.get(value);
        }

        public static final Codec<Unit> CODEC = StringValue.fromValues(Unit::values);

        @Override
        public String stringValue()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public record GizmoSettings(String type, int fillColor, int lineColor, Vector3f halfSizes, Vector3f offset, boolean offsetLocal)
    {
        public static final Codec<GizmoSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(GizmoSettings::type),
            OrbiterCodecs.HEX.fieldOf("fill_color").forGetter(GizmoSettings::fillColor),
            OrbiterCodecs.HEX.optionalFieldOf("line_color", 0xff000000).forGetter(GizmoSettings::lineColor),
            ExtraCodecs.VEC_3F.optionalFieldOf("half_sizes", new Vector3f(0.5f)).forGetter(GizmoSettings::halfSizes),
            ExtraCodecs.VEC_3F.optionalFieldOf("offset", new Vector3f()).forGetter(GizmoSettings::offset),
            Codec.BOOL.fieldOf("offset_local").forGetter(GizmoSettings::offsetLocal)
        ).apply(instance, GizmoSettings::new));

        public void createGizmo(Vector3f pos, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Long> stayFor, Unit unit, boolean fadeOut, boolean alwaysOnTop)
        {
            if (type.equals("cuboid"))
            {
                GizmoRenderSettings gizmoRenderSettings = Gizmos.filledLineCuboid(pos, halfSizes.x, halfSizes.y, halfSizes.z, fillColor, lineColor, 1);
                applySettings(gizmoRenderSettings, stayFor, unit, fadeOut, alwaysOnTop);
            } else if (type.equals("point"))
            {
                GizmoRenderSettings gizmoRenderSettings = Gizmos.point(pos, fillColor);
                applySettings(gizmoRenderSettings, stayFor, unit, fadeOut, alwaysOnTop);
            }
        }

        private void applySettings(
            GizmoRenderSettings gizmoRenderSettings,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Long> stayFor,
            Unit unit,
            boolean fadeOut,
            boolean alwaysOnTop)
        {
            if (fadeOut) gizmoRenderSettings.fadeOut();
            if (alwaysOnTop) gizmoRenderSettings.alwaysOnTop();
            stayFor.ifPresent(val -> gizmoRenderSettings.stayForMs(unit.toMilli(val)));
        }
    }
}
