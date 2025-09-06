package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mojang.datafixers.util.Pair;
import org.joml.Matrix4f;
import steve6472.core.registry.Key;
import steve6472.flare.Camera;
import steve6472.flare.assets.model.Model;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.SBOTransfromArray;
import steve6472.flare.render.StaticModelRenderImpl;
import steve6472.orbiter.Client;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.particle.components.*;
import steve6472.orbiter.world.particle.ParticleComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class StaticParticleModelRender extends StaticModelRenderImpl
{
    private final Client client;
    private final RenderPipeline.Enum particlePipeline;

    public StaticParticleModelRender(Client client, RenderPipeline.Enum particlePipeline)
    {
        this.client = client;
        this.particlePipeline = particlePipeline;
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

        ImmutableArray<Entity> particle = world.particleEngine().getEntitiesFor(PARTICLE_FAMILY);
        if (particle.size() == 0)
            return;

        List<Entity> list = new ArrayList<>(particle.size());
        for (Entity entity : particle)
        {
            RenderPipeline renderPipeline = ParticleComponents.RENDER_PIPELINE.get(entity);
            // Model is default
            if ((renderPipeline != null && renderPipeline.value == this.particlePipeline) || (renderPipeline == null && this.particlePipeline == RenderPipeline.Enum.MODEL))
                list.add(entity);
        }

        if (list.isEmpty())
            return;

        sboTransfromArray.sort(list, entity -> sboTransfromArray.addArea(ParticleComponents.MODEL.get(entity).model).index());

        var lastArea = sboTransfromArray.getAreaByIndex(0);
        Model lastModel = ParticleComponents.MODEL.get(list.getFirst()).model;
        for (Entity entity : list)
        {
            Pair<Model, SBOTransfromArray<Model>.Area> modelAreaPair = processEntity(entity, lastModel, lastArea, sboTransfromArray, frameInfo.camera());
            lastModel = modelAreaPair.getFirst();
            lastArea = modelAreaPair.getSecond();
        }
    }

    private Pair<Model, SBOTransfromArray<Model>.Area> processEntity(Entity entity, Model lastModel, SBOTransfromArray<Model>.Area lastArea, SBOTransfromArray<Model> sboTransfromArray, Camera camera)
    {
        ParticleModel model = ParticleComponents.MODEL.get(entity);
        OrlangEnvironment env = ParticleRenderCommon.updateEnvironment(entity);

        if (lastArea == null || lastModel != model.model)
        {
            lastArea = sboTransfromArray.getAreaByType(model.model);
            lastModel = model.model;
        }

        Matrix4f primitiveTransform = new Matrix4f();

        ParticleRenderCommon.updateTransformMat(primitiveTransform, entity, camera, env);

        lastArea.updateTransform(primitiveTransform);

        return Pair.of(lastModel, lastArea);
    }
}
