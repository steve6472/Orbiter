package steve6472.orbiter.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import steve6472.flare.MasterRenderer;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.ui.GlobalProperties;
import steve6472.orbiter.world.ecs.systems.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 8/24/2025
 * Project: Orbiter <br>
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class WorldSystems
{
    private final World world;
    private final Engine engine;
    private final List<EntitySystem> renderSystems = new ArrayList<>();

    public WorldSystems(World world, Engine engine)
    {
        this.world = world;
        this.engine = engine;
    }

    /*
     * Systems
     */

    public UpdateClientData updateClientData;
    public HoldSystem holdSystem;
    private EntitySystem updateECS;
    private EntitySystem clickECS;
    private EntitySystem updatePhysics;
    private EntitySystem braodcastClientPosition;
    private EntitySystem networkSync;
    private EntitySystem primitiveEmitter;
    private EntitySystem removeEventComponents;

    /*
     *
     */

    public void init(MasterRenderer renderer)
    {
        initSystems();

//        addRenderSystem(new RenderNametag(renderer)); // "Render Nametag"
//        addRenderSystem(new RenderNetworkData(renderer));
    }

    private void initSystems()
    {
        // First
        engine.addSystem(updateECS = new UpdateECS(world)); // "Update ECS Positions", "Updates ECS Positions with data from last tick of Physics Simulation"
        engine.addSystem(clickECS = new ClickECS());
        /*systems.registerSystem(new ComponentSystem()
        {
            @Override
            public void tick(Dominion dominion, World world)
            {
//                if (!steam.isHost())
//                    return;

                dominion.findEntitiesWith(Tag.FireflyAI.class, Position.class).forEach(e ->
                {
                    Position position = e.comp2();
                    modifyComponent(e.entity(), position, p -> p.add(RandomUtil.randomFloat(-0.01f, 0.01f), RandomUtil.randomFloat(-0.01f, 0.01f), RandomUtil.randomFloat(-0.01f, 0.01f)));
                });
            }
        }, "Firefly AI", "Test firefly entity");*/
        // Needs to be before network sync and physics update
        engine.addSystem(updateClientData = new UpdateClientData());

        engine.addSystem(holdSystem = new HoldSystem());
        engine.addSystem(new AttractTestSystem());
        engine.addSystem(braodcastClientPosition = new BroadcastClientPosition());
        engine.addSystem(primitiveEmitter = new ParticleEmitterSystem(world));
        engine.addSystem(new ClickConsumerTest());

        // Last
        engine.addSystem(networkSync = new NetworkSync(OrbiterApp.getInstance().getNetwork())); //"Network Sync", ""
//        engine.addSystem(updatePhysics = new UpdatePhysics(world)); // "Update Physics Positions", "Updates Physics Positions with data from last tick ECS Systems"
        engine.addSystem(removeEventComponents = new RemoveEventComponents());
    }

    public void updateStates()
    {
        boolean isLobbyOpen = GlobalProperties.LOBBY_OPEN.get();
        boolean isHost = GlobalProperties.IS_LOBBY_HOST.get();

        networkSync.setProcessing(isLobbyOpen && isHost);
        updateClientData.setProcessing(isLobbyOpen && isHost);
    }

    public void runTickSystems(float frameTime)
    {
        engine.update(frameTime);
    }

    public void runRenderSystems(float frameTime)
    {
        for (EntitySystem renderSystem : renderSystems)
        {
            renderSystem.update(frameTime);
        }
    }

    private void addRenderSystem(EntitySystem system)
    {
        this.renderSystems.add(system);
        system.addedToEngine(engine);
    }
}
