package steve6472.orbiter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jme3.bullet.objects.PhysicsRigidBody;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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
import steve6472.orbiter.world.ecs.components.particle.LocalSpace;
import steve6472.orbiter.world.ecs.components.particle.ParticleFollowerId;
import steve6472.orbiter.world.ecs.components.particle.Scale;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ParticleRender extends StaticModelRenderImpl
{
    private final Client client;

    public ParticleRender(Client client)
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

    private static final Family MODEL_FAMILY = Family.all(IndexModel.class, Position.class).get();

    @Override
    public void updateTransformArray(SBOTransfromArray<Model> sboTransfromArray, FrameInfo frameInfo)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        ImmutableArray<Entity> physicsModels = world.particleEngine().getEntitiesFor(MODEL_FAMILY);
        if (physicsModels.size() == 0)
            return;

        List<Entity> list = new ArrayList<>(physicsModels.size());
        for (Entity entity : physicsModels)
        {
            list.add(entity);
        }

        if (list.isEmpty())
            return;

        sboTransfromArray.sort(list, entity -> sboTransfromArray.addArea(Components.MODEL.get(entity).model()).index());

        var lastArea = sboTransfromArray.getAreaByIndex(0);
        Model lastModel = Components.MODEL.get(list.getFirst()).model();
        for (Entity entity : list)
        {
            IndexModel model = Components.MODEL.get(entity);

            if (lastArea == null || lastModel != model.model())
            {
                lastArea = sboTransfromArray.getAreaByType(model.model());
                lastModel = model.model();
            }

            LocalSpace localSpace = Components.LOCAL_SPACE.get(entity);

            Matrix4f primitiveTransform = new Matrix4f();
            if (Components.POSITION.has(entity))
            {
                Position position = Components.POSITION.get(entity);
                if (localSpace != null && localSpace.position)
                {
                    ParticleFollowerId follower = Components.PARTICLE_FOLLOWER.get(entity);
                    Position holderPosition = Components.POSITION.get(follower.entity);
                    if (holderPosition != null)
                    {
                        primitiveTransform.translate(holderPosition.x(), holderPosition.y(), holderPosition.z());
                    }
                }
                primitiveTransform.translate(position.x(), position.y(), position.z());
            }

            if (localSpace != null && localSpace.rotation)
            {
                ParticleFollowerId follower = Components.PARTICLE_FOLLOWER.get(entity);
                Rotation holderRotation = Components.ROTATION.get(follower.entity);
                if (holderRotation != null)
                {
                    primitiveTransform.rotate(new Quaternionf(holderRotation.x(), holderRotation.y(), holderRotation.z(), holderRotation.w()));
                }
            }

            if (Components.SCALE.has(entity))
            {
                Scale scale = Components.SCALE.get(entity);
                primitiveTransform.scale(scale.scale());
            }
            lastArea.updateTransform(primitiveTransform);
        }
    }
}
