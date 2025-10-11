package steve6472.jolt;

import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.enumerate.EActivation;
import com.github.stephengold.joltjni.enumerate.EMotionType;
import com.github.stephengold.joltjni.enumerate.EPhysicsUpdateError;
import com.github.stephengold.joltjni.readonly.ConstPlane;
import com.github.stephengold.joltjni.readonly.ConstShape;
import com.github.stephengold.joltjni.readonly.Vec3Arg;
import org.joml.Vector3f;
import steve6472.core.util.Profiler;
import steve6472.core.util.RandomUtil;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.world.JoltBodies;

import javax.swing.*;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/8/2025
 * Project: Orbiter <br>
 */
class Phys
{
    public final PhysicsSystem physics;
    public final TempAllocator tempAllocator;
    public final JobSystem jobSystem;
    public final JoltBodies joltBodies;
    public final Profiler physicsProfiler;

    public Phys()
    {
        physics = createPhysicsSystem();

        tempAllocator = new TempAllocatorMalloc();
        int numWorkerThreads = Runtime.getRuntime().availableProcessors();
        jobSystem = new JobSystemThreadPool(Jolt.cMaxPhysicsJobs, Jolt.cMaxPhysicsBarriers, numWorkerThreads);
        joltBodies = new JoltBodies();
        physicsProfiler = new Profiler(60);
        addPlane(new Vector3f(0, 1f, 0), 0);
    }

    private PhysicsSystem createPhysicsSystem()
    {
        // For simplicity, use a single broadphase layer:
        int numBpLayers = 1;

        //noinspection ExtractMethodRecommender
        ObjectLayerPairFilterTable ovoFilter = new ObjectLayerPairFilterTable(Constants.Physics.NUM_OBJ_LAYERS);
        // Enable collisions between 2 moving bodies:
        ovoFilter.enableCollision(Constants.Physics.OBJ_LAYER_MOVING, Constants.Physics.OBJ_LAYER_MOVING);
        // Enable collisions between a moving body and a non-moving one:
        ovoFilter.enableCollision(Constants.Physics.OBJ_LAYER_MOVING, Constants.Physics.OBJ_LAYER_NON_MOVING);
        // Disable collisions between 2 non-moving bodies:
        ovoFilter.disableCollision(Constants.Physics.OBJ_LAYER_NON_MOVING, Constants.Physics.OBJ_LAYER_NON_MOVING);

        // Map both object layers to broadphase layer 0:
        BroadPhaseLayerInterfaceTable layerMap = new BroadPhaseLayerInterfaceTable(Constants.Physics.NUM_OBJ_LAYERS, numBpLayers);
        layerMap.mapObjectToBroadPhaseLayer(Constants.Physics.OBJ_LAYER_MOVING, 0);
        layerMap.mapObjectToBroadPhaseLayer(Constants.Physics.OBJ_LAYER_NON_MOVING, 0);

        /*
         * Pre-compute the rules for colliding object layers
         * with broadphase layers:
         */
        ObjectVsBroadPhaseLayerFilter ovbFilter = new ObjectVsBroadPhaseLayerFilterTable(layerMap, numBpLayers, ovoFilter, Constants.Physics.NUM_OBJ_LAYERS);

        PhysicsSystem result = new PhysicsSystem();

        // Set high limits, even though this sample app uses only 2 bodies:
        int maxBodies = 5_000;
        int numBodyMutexes = 0; // 0 means "use the default number"
        int maxBodyPairs = 65_536;
        int maxContacts = 20_480;
        result.init(maxBodies, numBodyMutexes, maxBodyPairs, maxContacts, layerMap, ovbFilter, ovoFilter);

        return result;
    }

    public void tick()
    {
        float timePerStep = 1f / Constants.TICKS_IN_SECOND; // in seconds
        int collisionSteps = 1;

        physicsProfiler.start();
        int errors = physics.update(timePerStep, collisionSteps, tempAllocator, jobSystem);
        assert errors == EPhysicsUpdateError.None : errors;
        physicsProfiler.end();
    }

    private void addPlane(Vector3f normal, float constant)
    {
        BodyInterface bi = physics.getBodyInterface();

        Vec3Arg norm = Convert.jomlToPhys(normal);
        ConstPlane plane = new Plane(norm, constant);
        ConstShape floorShape = new PlaneShape(plane);

        BodyCreationSettings bcs = new BodyCreationSettings();
        bcs.setMotionType(EMotionType.Static);
        bcs.setObjectLayer(Constants.Physics.OBJ_LAYER_NON_MOVING);
        bcs.setShape(floorShape);
        Body floor = bi.createBody(bcs);
        floor.setUserData(~Constants.PhysicsFlags.NEVER_DEBUG_RENDER);
        bi.addBody(floor, EActivation.DontActivate);

        //        BodyCreationSettings bcs = new BodyCreationSettings();
        //        bcs.setMotionType(EMotionType.Static);
        //        bcs.setObjectLayer(Constants.Physics.OBJ_LAYER_NON_MOVING);
        //        bcs.setShape(new BoxShape(new Vec3(100.0f, 1.0f, 100.0f)));
        //        bcs.setPosition(new RVec3(0, -1, 0));
        //        Body floor = physics.getBodyInterface().createBody(bcs);
        //        physics.getBodyInterface().addBody(floor, EActivation.DontActivate);
    }

    public void testBox(Vector3f center)
    {
        BodyInterface bi = physics.getBodyInterface();
        BoxShape shape = new BoxShape(0.125f);

        BodyCreationSettings bcs = new BodyCreationSettings();
        bcs.setMotionType(EMotionType.Dynamic);
        bcs.setObjectLayer(Constants.Physics.OBJ_LAYER_MOVING);
        bcs.setShape(shape);
        bcs.setPosition(RandomUtil.randomDouble(-1, 1) + center.x, 2, RandomUtil.randomDouble(-1, 1) + center.z);
        Body box = bi.createBody(bcs);
        bi.addBody(box, EActivation.Activate);

        joltBodies.addBody(UUID.randomUUID(), box);
    }

    public void testBoxPos(Vector3f position)
    {
        BodyInterface bi = physics.getBodyInterface();
        BoxShape shape = new BoxShape(0.125f);

        BodyCreationSettings bcs = new BodyCreationSettings();
        bcs.setMotionType(EMotionType.Dynamic);
        bcs.setObjectLayer(Constants.Physics.OBJ_LAYER_MOVING);
        bcs.setShape(shape);
        bcs.setPosition(position.x, position.y, position.z);
        Body box = bi.createBody(bcs);
        bi.addBody(box, EActivation.Activate);

        joltBodies.addBody(UUID.randomUUID(), box);
    }
}
