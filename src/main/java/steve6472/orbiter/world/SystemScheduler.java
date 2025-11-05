package steve6472.orbiter.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import steve6472.orbiter.world.ecs.core.RunSystem;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SystemScheduler
{
    private final Map<Integer, EntitySystem> systems = new HashMap<>();
    private final Map<Integer, Set<Integer>> dependencies = new HashMap<>();

    private final ExecutorService executor;
    private final Engine engine;
    private boolean finalized = false;
    private OperationProcessor operationProcessor;

    public SystemScheduler(int threadCount, Engine engine)
    {
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.engine = engine;
    }

    public void setOperationProcessor(OperationProcessor operationProcessor)
    {
        this.operationProcessor = operationProcessor;
    }

    // Register a system with an ID
    public void addSystem(int id, EntitySystem system)
    {
        if (finalized)
            throw new IllegalStateException("Cannot add systems after finalize()");
        systems.put(id, system);
        dependencies.putIfAbsent(id, new HashSet<>());
        engine.addSystem(system);
    }

    public void addSystem(int id, Consumer<Float> run)
    {
        addSystem(id, new RunSystem(run));
    }

    // Define dependency: systemId depends on dependencyId
    public void addDependency(int systemId, int dependencyId)
    {
        if (finalized)
            throw new IllegalStateException("Cannot add dependencies after finalize()");
        dependencies.get(systemId).add(dependencyId);
    }

    // Prepare dependency graph
    public void finalizeGraph()
    {
        topologicalSort();
        this.finalized = true;
    }

    public void runAll(float deltaTime)
    {
        if (!finalized)
            throw new IllegalStateException("Call finalizeGraph() before runAll()");

        Map<Integer, AtomicInteger> remainingDeps = new ConcurrentHashMap<>();
        for (int id : dependencies.keySet())
        {
            remainingDeps.put(id, new AtomicInteger(dependencies.get(id).size()));
        }

        BlockingQueue<Integer> readyQueue = new LinkedBlockingQueue<>();
        remainingDeps.forEach((id, depCount) ->
        {
            if (depCount.get() == 0)
                readyQueue.offer(id);
        });

        CountDownLatch latch = new CountDownLatch(systems.size());

        while (!readyQueue.isEmpty())
        {
            int id = readyQueue.poll();
            EntitySystem system = systems.get(id);

            executor.submit(() ->
            {
                try
                {
                    if (system.checkProcessing())
                    {
                        system.update(deltaTime);
                    }
                } finally
                {
                    // Whether it ran or not, mark dependents as ready
                    for (int dependent : dependencies.keySet())
                    {
                        if (dependencies.get(dependent).contains(id))
                        {
                            int count = remainingDeps.get(dependent).decrementAndGet();
                            if (count == 0)
                                readyQueue.offer(dependent);
                        }
                    }
                    latch.countDown();
                }
            });
        }

        try
        {
            latch.await();
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        if (operationProcessor != null)
            operationProcessor.process();
    }

    public void shutdown()
    {
        executor.shutdown();
    }

    // ---- Internal utility: topological sort ----
    /// Check for circular dependency
    public List<Integer> topologicalSort()
    {
        Map<Integer, Integer> inDegree = new HashMap<>();
        for (int id : dependencies.keySet())
        {
            inDegree.put(id, dependencies.get(id).size());
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (Map.Entry<Integer, Integer> e : inDegree.entrySet())
        {
            if (e.getValue() == 0)
                queue.offer(e.getKey());
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty())
        {
            int id = queue.poll();
            order.add(id);
            for (int dependent : dependencies.keySet())
            {
                if (dependencies.get(dependent).contains(id))
                {
                    int newCount = inDegree.compute(dependent, (k, v) -> v - 1);
                    if (newCount == 0)
                        queue.offer(dependent);
                }
            }
        }

        if (order.size() != systems.size())
        {
            throw new IllegalStateException("Circular dependency detected!");
        }

        return order;
    }

    public void runSequential(float deltaTime)
    {
        if (!finalized)
            throw new IllegalStateException("Call finalizeGraph() before runSequential()");

        List<Integer> sortedOrder = topologicalSort();

        for (int id : sortedOrder)
        {
            EntitySystem system = systems.get(id);
            if (system.checkProcessing())
            {
                try
                {
                    system.update(deltaTime);
                } catch (Exception ex)
                {
                    System.err.println(system.getClass().getCanonicalName());
                    throw ex;
                }
            }

            if (operationProcessor != null)
                operationProcessor.process();
        }
    }

    @FunctionalInterface
    public interface OperationProcessor
    {
        void process();
    }
}