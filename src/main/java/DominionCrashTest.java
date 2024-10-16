import dev.dominion.ecs.api.Dominion;

public void main()
{
    System.setProperty("dominion.show-banner", "false");
    int[] seedList = new int[] {-339818046, -228942723};

    class TestA {}
    class TestB {}
    class TestC {}
    class TestD {}
    class TestE {}

    Dominion ecs;
    ecs = Dominion.create();
    //noinspection InfiniteLoopStatement
    for (int tick = 0;; tick++)
    {
        var found = ecs.findEntitiesWith(TestA.class);

        for (var entityComps : found)
        {
            System.out.println("Processing entity: " + entityComps);
            TestA _ = entityComps.comp();
        }

        Random random = new Random(seedList[tick]);

        List<Object> components = new ArrayList<>(List.of(new TestA(), new TestB(), new TestC(), new TestD(), new TestE()));
        Collections.shuffle(components, random);
        Object[] array = components.toArray();
        System.out.println("Creating entity with following data: " + Arrays.toString(array));
        System.out.println("Entity created: " + ecs.createEntity(array));

        System.out.println("\n");
    }
}