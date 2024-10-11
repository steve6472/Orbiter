package steve6472.orbiter.world.ecs.components.physics;

/**
 * Created by steve6472
 * Date: 10/11/2024
 * Project: Orbiter <br>
 */
public class ModifyState
{
    private static final ModifyState TRUE = new ModifyState(null, BooleanType.TRUE);
    private static final ModifyState FALSE = new ModifyState(null, BooleanType.FALSE);

    private final Object newComponent;
    private final BooleanType booleanType;

    private ModifyState(Object newComponent, BooleanType booleanType)
    {
        this.newComponent = newComponent;
        this.booleanType = booleanType;
    }

    public static ModifyState component(Object newComponent)
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

    public Object getComponent()
    {
        return newComponent;
    }

    private enum BooleanType
    {
        TRUE, FALSE, COMPONENT
    }
}
