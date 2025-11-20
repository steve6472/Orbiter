package steve6472.dfu;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

/**
 * Created by steve6472
 * Date: 11/18/2025
 * Project: Orbiter <br>
 */
public abstract class ModelDataFix extends DataFix
{
    public ModelDataFix(Schema outputSchema, boolean changesType)
    {
        super(outputSchema, changesType);
    }

    protected <T> Dynamic<T> fixVersion(Dynamic<T> dynamic)
    {
        return dynamic.update("meta", meta -> meta.set("format_version", meta.createString(ModelVersionMap.modelStringFromVersion(getVersionKey()))));
    }
}
