package steve6472.orbiter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jme3.bullet.objects.PhysicsRigidBody;
import org.joml.Matrix4f;
import steve6472.core.registry.Key;
import steve6472.flare.assets.model.Model;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.SBOTransfromArray;
import steve6472.flare.render.StaticModelRenderImpl;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.UUIDComp;
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

    private static final Family MODEL_FAMILY = Family.all(IndexModel.class, UUIDComp.class).get();

    @Override
    public void updateTransformArray(SBOTransfromArray<Model> sboTransfromArray, FrameInfo frameInfo)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        ImmutableArray<Entity> physicsModels = world.ecsEngine().getEntitiesFor(MODEL_FAMILY);
        if (physicsModels.size() == 0)
            return;

        List<Entity> list = new ArrayList<>();
        for (Entity entity : physicsModels)
        {
            list.add(entity);
        }

        sboTransfromArray.sort(list, entity -> sboTransfromArray.addArea(Components.MODEL.get(entity).model()).index());

        var lastArea = sboTransfromArray.getAreaByIndex(0);
        Model lastModel = Components.MODEL.get(list.getFirst()).model();
        for (Entity entity : list)
        {
            IndexModel model = Components.MODEL.get(entity);
            UUID uuid = Components.UUID.get(entity).uuid();

            if (lastArea == null || lastModel != model.model())
            {
                lastArea = sboTransfromArray.getAreaByType(model.model());
                lastModel = model.model();
            }

            PhysicsRigidBody body = world.bodyMap().get(uuid);
            if (body != null)
            {
                Matrix4f jomlMat = Convert.physGetTransformToJoml(body, new Matrix4f());
                lastArea.updateTransform(jomlMat);
            } else
            {
                Matrix4f primitiveTransform = new Matrix4f();
                if (Components.POSITION.has(entity))
                {
                    Position position = Components.POSITION.get(entity);
                    primitiveTransform.translate(position.toVec3f());
                }
                lastArea.updateTransform(primitiveTransform);
            }
        }
    }
}
