package steve6472.orbiter.rendering.snapshot;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import org.joml.Vector3f;
import steve6472.orbiter.rendering.ParticleMaterial;
import steve6472.orbiter.rendering.snapshot.pairs.FlipbookParticlePair;
import steve6472.orbiter.rendering.snapshot.pairs.RenderPair;
import steve6472.orbiter.rendering.snapshot.pairs.PlaneParticlePair;
import steve6472.orbiter.rendering.snapshot.pairs.PlaneTintedParticlePair;
import steve6472.orbiter.rendering.snapshot.snapshots.ParticleSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class WorldRenderState
{
    public boolean created = false;
    public final WorldSnapshot lastSnapshot, currentSnapshot;

    private final List<PlaneParticlePair> unsortedParticles = new ArrayList<>();
    private final List<PlaneTintedParticlePair> unsortedTintedParticles = new ArrayList<>();
    private final List<FlipbookParticlePair> unsortedFlipbookParticles = new ArrayList<>();

    public Map<ParticleMaterial, List<PlaneParticlePair>> particles = new HashMap<>();
    public Map<ParticleMaterial, List<PlaneTintedParticlePair>> tintedParticles = new HashMap<>();
    public Map<ParticleMaterial, List<FlipbookParticlePair>> flipbookParticles = new HashMap<>();

    public WorldRenderState(WorldSnapshot lastSnapshot, WorldSnapshot currentSnapshot)
    {
        this.lastSnapshot = lastSnapshot;
        this.currentSnapshot = currentSnapshot;
    }

    // Runs once after tick
    public void createRenderPairs()
    {
        if (created)
            return;

        createParticlePairs(lastSnapshot.particleSnapshots.planeParticles, currentSnapshot.particleSnapshots.planeParticles, PlaneParticlePair::new, unsortedParticles);
        createParticlePairs(lastSnapshot.particleSnapshots.planeTintedParticles, currentSnapshot.particleSnapshots.planeTintedParticles, PlaneTintedParticlePair::new, unsortedTintedParticles);
        createParticlePairs(lastSnapshot.particleSnapshots.flipbookParticles, currentSnapshot.particleSnapshots.flipbookParticles, FlipbookParticlePair::new, unsortedFlipbookParticles);

        created = true;
    }

    public void prepareParticles(Vector3f cameraPos, float partialTicks)
    {
        prepareParticles(cameraPos, partialTicks, particles, unsortedParticles);
        prepareParticles(cameraPos, partialTicks, tintedParticles, unsortedTintedParticles);
        prepareParticles(cameraPos, partialTicks, flipbookParticles, unsortedFlipbookParticles);
    }

    private <T extends ParticleSnapshot, P extends RenderPair<T>> void
    prepareParticles(Vector3f cameraPos, float partialTicks, Map<ParticleMaterial, List<P>> sortedResult, List<P> unsortedSource)
    {
        sortedResult.forEach((_, list) -> list.clear());

        // Calculate interpolated position
        for (RenderPair<T> pair : unsortedSource)
        {
            T last = pair.previous();
            T current = pair.current();

            current.rx = lerp(last.x, current.x, partialTicks);
            current.ry = lerp(last.y, current.y, partialTicks);
            current.rz = lerp(last.z, current.z, partialTicks);
        }

        for (P pair : unsortedSource)
        {
            List<P> list = sortedResult.computeIfAbsent(pair.current().material, _ -> new ArrayList<>(16));
            list.add(pair);
        }

        // Sort based on interpolated position
        sortedResult.forEach((material, list) -> {
            // Skip unsorted particles
            if (!material.renderSettings().transparency().sorted)
                return;

            list.sort((e1, e2) -> {

                var p1 = e1.current();
                var p2 = e2.current();

                float d1 = Vector3f.distanceSquared(p1.rx, p1.ry, p1.rz, cameraPos.x, cameraPos.y, cameraPos.z);
                float d2 = Vector3f.distanceSquared(p2.rx, p2.ry, p2.rz, cameraPos.x, cameraPos.y, cameraPos.z);

                return Float.compare(d2, d1);
            });
        });
    }

    protected <T extends ParticleSnapshot, P extends RenderPair<T>> void
    createParticlePairs(Array<? extends ParticleSnapshot> lastSnapshots, Array<? extends ParticleSnapshot> currentSnapshots, BiFunction<T, T, P> pairConstructor, List<P> store)
    {
        // Create map for speed
        Map<Entity, ParticleSnapshot> last = new HashMap<>(lastSnapshots.size);
        for (ParticleSnapshot particle : lastSnapshots)
        {
            last.put(particle.entity, particle);
        }

        // Iterate over current particles
        for (ParticleSnapshot currentSnapshot : currentSnapshots)
        {
            ParticleSnapshot lastSnapshot = last.remove(currentSnapshot.entity);
            if (lastSnapshot == null)
                lastSnapshot = currentSnapshot;

            // Create pair for rendering
            //noinspection unchecked
            store.add(pairConstructor.apply((T) lastSnapshot, (T) currentSnapshot));
        }
    }

    private static float lerp(float start, float end, float value)
    {
        return start + value * (end - start);
    }
}
