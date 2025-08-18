package steve6472.orbiter;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.dominion.ecs.api.Results;
import org.joml.Matrix4f;
import steve6472.core.registry.Key;
import steve6472.flare.assets.model.Model;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.SBOTransfromArray;
import steve6472.flare.render.StaticModelRenderImpl;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.physics.Position;

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
    private final Client client;

    public StaticWorldRender(Client client)
    {
        this.client = client;
    }

    @Override
    protected void init(SBOTransfromArray<Model> sboTransfromArray)
    {
        for (Key key : FlareRegistries.STATIC_MODEL.keys())
        {
            sboTransfromArray.addArea(FlareRegistries.STATIC_MODEL.get(key));
        }
    }

    @Override
    public void updateTransformArray(SBOTransfromArray<Model> sboTransfromArray, FrameInfo frameInfo)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        var physicsModels = world.ecs().findEntitiesWith(IndexModel.class, UUID.class);
        List<Results.With2<IndexModel, UUID>> list = new ArrayList<>(physicsModels.stream().toList());
        if (list.isEmpty())
            return;

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

            PhysicsRigidBody body = world.bodyMap().get(entity.comp2());
            if (body != null)
            {
                Matrix4f jomlMat = Convert.physGetTransformToJoml(body, new Matrix4f());
                lastArea.updateTransform(jomlMat);
            } else
            {
                Matrix4f primitiveTransform = new Matrix4f();
                if (entity.entity().has(Position.class))
                {
                    Position position = entity.entity().get(Position.class);
                    primitiveTransform.translate(position.toVec3f());
                }
                lastArea.updateTransform(primitiveTransform);
            }
        }
    }
}
