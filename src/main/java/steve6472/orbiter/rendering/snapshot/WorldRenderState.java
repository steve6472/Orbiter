package steve6472.orbiter.rendering.snapshot;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import org.joml.Vector3f;
import steve6472.orbiter.rendering.ParticleMaterial;
import steve6472.orbiter.rendering.snapshot.pairs.ParticlePair;
import steve6472.orbiter.rendering.snapshot.snapshots.ParticleSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class WorldRenderState
{
    public boolean created = false;
    public final WorldSnapshot lastSnapshot, currentSnapshot;

    private final List<ParticlePair> unsortedParticles = new ArrayList<>();
    public Map<ParticleMaterial, List<ParticlePair>> particles = new HashMap<>();

    public WorldRenderState(WorldSnapshot lastSnapshot, WorldSnapshot currentSnapshot)
    {
        this.lastSnapshot = lastSnapshot;
        this.currentSnapshot = currentSnapshot;
    }

    public void createRenderPairs()
    {
        if (created)
            return;

        createParticlePairs(lastSnapshot.planeParticleSnapshot.particles, currentSnapshot.planeParticleSnapshot.particles, false);
        createParticlePairs(lastSnapshot.planeParticleSnapshot.tintedParticles, currentSnapshot.planeParticleSnapshot.tintedParticles, true);

        created = true;
    }

    public void prepareParticles(Vector3f cameraPos, float partialTicks)
    {
        particles.forEach((_, list) -> list.clear());

        // Calculate interpolated position
        for (ParticlePair pair : unsortedParticles)
        {
            ParticleSnapshot last = pair.previous();
            ParticleSnapshot current = pair.current();

            current.rx = lerp(last.x, current.x, partialTicks);
            current.ry = lerp(last.y, current.y, partialTicks);
            current.rz = lerp(last.z, current.z, partialTicks);
        }

        for (ParticlePair pair : unsortedParticles)
        {
            List<ParticlePair> list = particles.computeIfAbsent(pair.current().material, _ -> new ArrayList<>(16));
            list.add(pair);
        }

        // Sort based on interpolated position
        particles.forEach((material, list) -> {
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

    protected void createParticlePairs(Array<? extends ParticleSnapshot> lastSnapshots, Array<? extends ParticleSnapshot> currentSnapshots, boolean tinted)
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
            // Add newly created particles to old snapshot
            ParticleSnapshot lastSnapshot = last.remove(currentSnapshot.entity);
            if (lastSnapshot == null)
                lastSnapshot = currentSnapshot;

            // Create pair for rendering
            unsortedParticles.add(new ParticlePair(lastSnapshot, currentSnapshot, tinted));
        }
    }

    private static float lerp(float start, float end, float value)
    {
        return start + value * (end - start);
    }
}
