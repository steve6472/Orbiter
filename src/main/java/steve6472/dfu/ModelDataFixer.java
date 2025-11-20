package steve6472.dfu;

import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import steve6472.dfu.fix.FixGroup;

/**
 * Created by steve6472
 * Date: 11/17/2025
 * Project: Orbiter <br>
 */
public class ModelDataFixer
{
    public static final int CURRENT_MODEL_VERSION = ModelVersionMap.getVersionFromModelString("4.10");
    public static final String NEWEST_BB = "5.0";

    public static JsonObject apply(JsonObject oldModel, int newVersion)
    {
        int oldVersion = ModelVersionMap.getVersionFromModelString(VersionGetter
            .getString(oldModel, "meta.format_version").orElse(NEWEST_BB));
        return build().update(References.MODEL_TYPE, new Dynamic<>(JsonOps.INSTANCE, oldModel), oldVersion, newVersion).getValue().getAsJsonObject();
    }

    public static boolean needsUpdate(JsonObject oldModel)
    {
        int oldVersion = ModelVersionMap.getVersionFromModelString(VersionGetter
            .getString(oldModel, "meta.format_version").orElse(NEWEST_BB));
        return oldVersion < CURRENT_MODEL_VERSION;
    }

    public static JsonObject apply(JsonObject oldModel)
    {
        return apply(oldModel, CURRENT_MODEL_VERSION);
    }

    private static DataFixer build()
    {
        DataFixerBuilder builder = new DataFixerBuilder(CURRENT_MODEL_VERSION);

        builder.addSchema(1, ModelSchema::new);
        Schema schema = builder.addSchema(2, Schema::new);
        builder.addFixer(new FixGroup(schema, true));

        return builder.build().fixer();
    }
}
