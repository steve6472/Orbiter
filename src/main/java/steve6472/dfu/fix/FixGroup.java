package steve6472.dfu.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import steve6472.dfu.ModelDataFix;
import steve6472.dfu.References;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by steve6472
 * Date: 11/18/2025
 * Project: Orbiter <br>
 */
public class FixGroup extends ModelDataFix
{
    public FixGroup(Schema outputSchema, boolean changesType)
    {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule()
    {
        return fixTypeEverywhereTyped("FixGroup",
            getInputSchema().getType(References.MODEL_TYPE),
            typed -> typed.update(DSL.remainderFinder(), dynamic -> fixVersion(fix(dynamic))));
    }

    private Dynamic<?> fix(Dynamic<?> dynamic)
    {
        OptionalDynamic<?> groups = dynamic.get("groups");
        Map<String, Pair<Float[], Float[]>> map = new HashMap<>();

        groups.orElseEmptyList().asStream().forEach(entry ->
        {
            Float[] origin = entry.get("origin").orElseEmptyList().asStream().map(dynValue -> dynValue.asFloat(0)).toArray(Float[]::new);
            Float[] rotation = entry.get("rotation").orElseEmptyList().asStream().map(dynValue -> dynValue.asFloat(0)).toArray(Float[]::new);

            map.put(entry.get("uuid").asString().result().orElseThrow(), Pair.of(origin, rotation));
        });

        return dynamic.update("outliner", outliner -> {
            outliner = outliner.createList(outliner.asStream().map(entry -> fixOutliner(entry, map)));
            return outliner;
        }).remove("groups");
    }

    private Dynamic<?> fixOutliner(Dynamic<?> dynamic, Map<String, Pair<Float[], Float[]>> map)
    {
        DataResult<String> uuid = dynamic.get("uuid").asString();
        if (uuid.isError())
            return dynamic;

        Optional<String> result = uuid.result();
        if (result.isEmpty())
            return dynamic;

        String uuidString = result.get();
        Pair<Float[], Float[]> pair = map.get(uuidString);

        if (pair != null)
        {
            Float[] origin = pair.getFirst();
            Float[] rotation = pair.getSecond();
            Dynamic<?> originList = dynamic.createList(Stream.of(dynamic.createFloat(origin[0]), dynamic.createFloat(origin[1]), dynamic.createFloat(origin[2])));
            Dynamic<?> rotationList = dynamic.createList(Stream.of(dynamic.createFloat(rotation[0]), dynamic.createFloat(rotation[1]), dynamic.createFloat(rotation[2])));
            dynamic = dynamic.set("origin", originList).set("rotation", rotationList);
        }

        return dynamic.update("children", outliner -> {
            outliner = outliner.createList(outliner.asStream().map(entry -> fixOutliner(entry, map)));
            return outliner;
        });
    }
}
