package steve6472.orbiter.audio;

import steve6472.core.module.FullModulePart;
import steve6472.core.module.ResourceCrawl;
import steve6472.core.registry.Key;
import steve6472.flare.core.Flare;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.OrbiterParts;
import steve6472.orbiter.Registries;

/**
 * Created by steve6472
 * Date: 9/19/2025
 * Project: Orbiter <br>
 */
public class SoundLoader
{
    public static void init()
    {
        SoundMaster soundMaster = OrbiterApp.getInstance().getClient().getSoundMaster();

        Flare.getModuleManager().iterateWithNamespaces((module, namespace) -> {
            FullModulePart part = module.createPart(OrbiterParts.SOUND, namespace);
            ResourceCrawl.crawl(part.path(), true, (file, relPath) -> {
                Key key = Key.withNamespace(part.namespace(), relPath);
                int i = soundMaster.loadSound(file.getAbsolutePath());
                Sound sound = new Sound(key, i);
                Registries.SOUND.register(sound);
            });
        });
    }
}
