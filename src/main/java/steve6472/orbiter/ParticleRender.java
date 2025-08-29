package steve6472.orbiter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mojang.datafixers.util.Pair;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import steve6472.core.registry.Key;
import steve6472.flare.assets.model.Model;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.SBOTransfromArray;
import steve6472.flare.render.StaticModelRenderImpl;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.particle.components.LocalSpace;
import steve6472.orbiter.world.particle.components.ParticleFollowerId;
import steve6472.orbiter.world.particle.components.ParticleModel;
import steve6472.orbiter.world.particle.components.Position;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.Scale;
import steve6472.orbiter.world.ecs.components.physics.Rotation;

import java.util.ArrayList;
import java.util.List;

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

    private static final Family PARTICLE_FAMILY = Family.all(ParticleModel.class, Position.class, OrlangEnvironment.class).get();

    @Override
    public void updateTransformArray(SBOTransfromArray<Model> sboTransfromArray, FrameInfo frameInfo)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        ImmutableArray<Entity> physicsModels = world.particleEngine().getEntitiesFor(PARTICLE_FAMILY);
        if (physicsModels.size() == 0)
            return;

        List<Entity> list = new ArrayList<>(physicsModels.size());
        for (Entity entity : physicsModels)
        {
            list.add(entity);
        }

        if (list.isEmpty())
            return;

        sboTransfromArray.sort(list, entity -> sboTransfromArray.addArea(ParticleComponents.MODEL.get(entity).model).index());

        var lastArea = sboTransfromArray.getAreaByIndex(0);
        Model lastModel = ParticleComponents.MODEL.get(list.getFirst()).model;
        for (Entity entity : list)
        {
            Pair<Model, SBOTransfromArray<Model>.Area> modelAreaPair = processEntity(entity, lastModel, lastArea, sboTransfromArray);
            lastModel = modelAreaPair.getFirst();
            lastArea = modelAreaPair.getSecond();
        }
    }

    private Pair<Model, SBOTransfromArray<Model>.Area> processEntity(Entity entity, Model lastModel, SBOTransfromArray<Model>.Area lastArea, SBOTransfromArray<Model> sboTransfromArray)
    {
        ParticleModel model = ParticleComponents.MODEL.get(entity);
        OrlangEnvironment env = ParticleComponents.PARTICLE_ENVIRONMENT.get(entity);

        if (lastArea == null || lastModel != model.model)
        {
            lastArea = sboTransfromArray.getAreaByType(model.model);
            lastModel = model.model;
        }

        LocalSpace localSpace = ParticleComponents.LOCAL_SPACE.get(entity);

        Matrix4f primitiveTransform = new Matrix4f();
        if (ParticleComponents.POSITION.has(entity))
        {
            Position position = ParticleComponents.POSITION.get(entity);
            if (localSpace != null && localSpace.position)
            {
                ParticleFollowerId follower = ParticleComponents.PARTICLE_FOLLOWER.get(entity);
                var holderPosition = Components.POSITION.get(follower.entity);
                if (holderPosition != null)
                {
                    primitiveTransform.translate(holderPosition.x(), holderPosition.y(), holderPosition.z());
                }
            }
            primitiveTransform.translate(position.x, position.y, position.z);
        }

        if (localSpace != null && localSpace.rotation)
        {
            ParticleFollowerId follower = ParticleComponents.PARTICLE_FOLLOWER.get(entity);
            Rotation holderRotation = Components.ROTATION.get(follower.entity);
            if (holderRotation != null)
            {
                primitiveTransform.rotate(new Quaternionf(holderRotation.x(), holderRotation.y(), holderRotation.z(), holderRotation.w()));
            }
        }

        if (ParticleComponents.SCALE.has(entity))
        {
            Scale scale = ParticleComponents.SCALE.get(entity);
            scale.scale.evaluate(env);
            primitiveTransform.scale(scale.scale.fx(), scale.scale.fy(), scale.scale.fz());
        }
        lastArea.updateTransform(primitiveTransform);

        return Pair.of(lastModel, lastArea);
    }
}
