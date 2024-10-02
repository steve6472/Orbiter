package steve6472.orbiter.world.ecs.components;

import steve6472.volkaniums.assets.model.Model;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class IndexModel
{
    private final Model model;
    private int modelIndex = -1;

    public IndexModel(Model model)
    {
        this.model = model;
    }

    public Model model()
    {
        return model;
    }

    public int modelIndex()
    {
        return modelIndex;
    }

    public void setModelIndex(int newIndex)
    {
        if (modelIndex != -1)
            throw new RuntimeException("Tried to change model index!");
        this.modelIndex = newIndex;
    }
}
