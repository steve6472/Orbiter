package steve6472.orbiter;

import dev.dominion.ecs.api.Results;
import org.joml.Matrix4f;
import steve6472.core.registry.Key;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.PhysicsReference;
import steve6472.volkaniums.assets.model.Model;
import steve6472.volkaniums.core.FrameInfo;
import steve6472.volkaniums.registry.VolkaniumsRegistries;
import steve6472.volkaniums.render.SBOTransfromArray;
import steve6472.volkaniums.render.StaticModelRenderImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class StaticWorldRender extends StaticModelRenderImpl
{
    private final World world;

    public StaticWorldRender(World world)
    {
        this.world = world;
    }

    @Override
    protected void init(SBOTransfromArray<Model> sboTransfromArray)
    {
        sboTransfromArray.addArea(VolkaniumsRegistries.STATIC_MODEL.get(Key.defaultNamespace("blockbench/static/player_capsule")));
    }

    @Override
    public void updateTransformArray(SBOTransfromArray<Model> sboTransfromArray, FrameInfo frameInfo)
    {
        var physicsModels = world.ecs().findEntitiesWith(IndexModel.class, UUID.class);
        List<Results.With2<IndexModel, UUID>> list = new ArrayList<>(physicsModels.stream().toList());
        sboTransfromArray.sort(list, obj -> sboTransfromArray.addArea(obj.comp1().model()).index());

        var lastArea = sboTransfromArray.getAreaByIndex(0);
        Model lastModel = list.getFirst().comp1().model();
        for (Results.With2<IndexModel, UUID> entity : list)
        {
            if (lastArea == null || lastModel != entity.comp1().model())
            {
                lastArea = sboTransfromArray.getAreaByType(entity.comp1().model());
                lastModel = entity.comp1().model();
            }

            Matrix4f jomlMat = Convert.physGetTransformToJoml(world.bodyMap.get(entity.comp2()), new Matrix4f());
            lastArea.updateTransform(jomlMat);
        }
    }
}
