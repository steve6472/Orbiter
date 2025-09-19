package steve6472.orbiter.audio;

import java.util.Iterator;
import java.util.List;

/**
 * Created by steve6472
 * Date: 9/19/2025
 * Project: Orbiter <br>
 */
public interface WorldSounds
{
    List<Source> getSoundSources();

    default void addSound(Sound sound, float x, float y, float z, float volume, float pitch)
    {
        Source source = new Source();
        source.setVolume(volume);
        source.setPitch(pitch);
        source.setPosition(x, y, z);
        source.play(sound.id());

        getSoundSources().add(source);
    }

    default void tickSoundClean()
    {
        for (Iterator<Source> iterator = getSoundSources().iterator(); iterator.hasNext(); )
        {
            Source source = iterator.next();
            if (!source.isPlaying())
            {
                source.delete();
                iterator.remove();
            }
        }
    }

    default void clearAllSoundSources()
    {
        for (Source soundSource : getSoundSources())
        {
            soundSource.delete();
        }
        getSoundSources().clear();
    }
}
