package steve6472.orbiter.world;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class EngineReflectionHelper
{
    private static final Field COMPONENT_OPERATION_HANDLER_FIELD;
    private static final Field ENTITY_MANAGER_FIELD;
    private static final Method HAS_OPERATIONS_TO_PROCESS;
    private static final Method PROCESS_OPERATIONS;
    private static final Method HAS_PENDING_OPERATIONS;
    private static final Method PROCESS_PENDING_OPERATIONS;

    static
    {
        try
        {
            Class<?> engineClass = Class.forName("com.badlogic.ashley.core.Engine");
            Class<?> componentOperationHandlerClass = Class.forName("com.badlogic.ashley.core.ComponentOperationHandler");
            Class<?> entityManagerClass = Class.forName("com.badlogic.ashley.core.EntityManager");

            // Access private fields
            COMPONENT_OPERATION_HANDLER_FIELD = engineClass.getDeclaredField("componentOperationHandler");
            COMPONENT_OPERATION_HANDLER_FIELD.setAccessible(true);
            ENTITY_MANAGER_FIELD = engineClass.getDeclaredField("entityManager");
            ENTITY_MANAGER_FIELD.setAccessible(true);

            // Access package-private methods reflectively (no lookup restrictions)
            HAS_OPERATIONS_TO_PROCESS = componentOperationHandlerClass.getDeclaredMethod("hasOperationsToProcess");
            HAS_OPERATIONS_TO_PROCESS.setAccessible(true);

            PROCESS_OPERATIONS = componentOperationHandlerClass.getDeclaredMethod("processOperations");
            PROCESS_OPERATIONS.setAccessible(true);

            HAS_PENDING_OPERATIONS = entityManagerClass.getDeclaredMethod("hasPendingOperations");
            HAS_PENDING_OPERATIONS.setAccessible(true);

            PROCESS_PENDING_OPERATIONS = entityManagerClass.getDeclaredMethod("processPendingOperations");
            PROCESS_PENDING_OPERATIONS.setAccessible(true);

        } catch (Throwable t)
        {
            throw new ExceptionInInitializerError("Failed to initialize EngineReflectionHelper: " + t);
        }
    }

    private EngineReflectionHelper()
    {
    }

    /**
     * Executes:
     * while(componentOperationHandler.hasOperationsToProcess() || entityManager.hasPendingOperations()) {
     * componentOperationHandler.processOperations();
     * entityManager.processPendingOperations();
     * }
     */
    public static void processPendingOperations(Object engineInstance)
    {
        try
        {
            Object componentOperationHandler = COMPONENT_OPERATION_HANDLER_FIELD.get(engineInstance);
            Object entityManager = ENTITY_MANAGER_FIELD.get(engineInstance);

            while ((boolean) HAS_OPERATIONS_TO_PROCESS.invoke(componentOperationHandler) || (boolean) HAS_PENDING_OPERATIONS.invoke(entityManager))
            {

                PROCESS_OPERATIONS.invoke(componentOperationHandler);
                PROCESS_PENDING_OPERATIONS.invoke(entityManager);
            }

        } catch (Throwable t)
        {
            throw new RuntimeException("Failed to process pending operations reflectively", t);
        }
    }
}