package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;

/**
 * Created by steve6472
 * Date: 10/11/2024
 * Project: Orbiter <br>
 */
public class ModifyState
{
    private static final ModifyState TRUE = new ModifyState(null, BooleanType.TRUE);
    private static final ModifyState FALSE = new ModifyState(null, BooleanType.FALSE);

    private final Component newComponent;
    private final BooleanType booleanType;

    private ModifyState(Component newComponent, BooleanType booleanType)
    {
        this.newComponent = newComponent;
        this.booleanType = booleanType;
    }

    public static ModifyState component(Component newComponent)
    {
        return new ModifyState(newComponent, BooleanType.COMPONENT);
    }

    public static ModifyState modifiedComponent()
    {
        return TRUE;
    }

    public static ModifyState noModification()
    {
        return FALSE;
    }

    public boolean hasModifiedComponent()
    {
        return booleanType == BooleanType.TRUE && newComponent == null;
    }

    public boolean hasNoModification()
    {
        return booleanType == BooleanType.FALSE && newComponent == null;
    }

    public boolean hasNewComponent()
    {
        return booleanType == BooleanType.COMPONENT && newComponent != null;
    }

    public Component getComponent()
    {
        return newComponent;
    }

    private enum BooleanType
    {
        TRUE, FALSE, COMPONENT
    }
}
