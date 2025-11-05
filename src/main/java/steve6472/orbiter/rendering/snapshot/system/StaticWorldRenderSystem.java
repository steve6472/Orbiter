package steve6472.orbiter.rendering.snapshot.system;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import steve6472.core.registry.Key;
import steve6472.flare.assets.model.Model;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.SBOTransfromArray;
import steve6472.flare.render.StaticModelRenderImpl;
import steve6472.orbiter.Client;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.rendering.snapshot.WorldRenderState;
import steve6472.orbiter.rendering.snapshot.pairs.StaticModelPair;
import steve6472.orbiter.rendering.snapshot.snapshots.StaticModelSnapshot;
import steve6472.orbiter.world.World;

import java.util.List;

import static steve6472.orbiter.rendering.snapshot.system.PlaneParticleRenderSystem.lerp;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class StaticWorldRenderSystem extends StaticModelRenderImpl
{
    private final Client client;

    public StaticWorldRenderSystem(Client client)
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

        WorldRenderState currentRenderState = OrbiterApp.getInstance().currentRenderState;
        if (currentRenderState == null || currentRenderState.unsortedStaticModels.isEmpty())
            return;

        float partial = OrbiterApp.getInstance().partialTicks;

        List<StaticModelPair> staticModels = currentRenderState.staticModels;
        performSort(currentRenderState.unsortedStaticModels, staticModels, sboTransfromArray);

        var lastArea = sboTransfromArray.getAreaByIndex(0);
        Model lastModel = staticModels.getFirst().current().model;
        Matrix4f transform = new Matrix4f();
        Quaternionf rot = new Quaternionf();

        for (StaticModelPair snapshotPair : staticModels)
        {
            StaticModelSnapshot previousSnapshot = snapshotPair.previous();
            StaticModelSnapshot currentSnapshot = snapshotPair.current();

            Model model = currentSnapshot.model;

            if (lastArea == null || lastModel != model)
            {
                lastArea = sboTransfromArray.getAreaByType(model);
                lastModel = model;
            }

            transform.identity();

            transform.translate(
                lerp(previousSnapshot.x, currentSnapshot.x, partial),
                lerp(previousSnapshot.y, currentSnapshot.y, partial),
                lerp(previousSnapshot.z, currentSnapshot.z, partial)
            );
            previousSnapshot.rotation.slerp(currentSnapshot.rotation, partial, rot);
            transform.rotate(rot);

            lastArea.updateTransform(transform);
        }
    }

    private void performSort(List<StaticModelPair> unsorted, List<StaticModelPair> sorted, SBOTransfromArray<Model> sboTransfromArray)
    {
        if (unsorted.isEmpty())
            return;

        if (!sorted.isEmpty())
            return;

        sorted.addAll(unsorted);

        sboTransfromArray.sort(sorted, snappyshot -> sboTransfromArray.addArea(snappyshot.current().model).index());
    }
}
