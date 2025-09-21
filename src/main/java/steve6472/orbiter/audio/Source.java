package steve6472.orbiter.audio;

import org.joml.Vector3f;
import steve6472.orbiter.settings.Settings;

import static org.lwjgl.openal.AL11.*;

/**********************
 * Created by steve6472
 * On date: 25.10.2020
 * Project: CaveGame
 *
 ***********************/
public class Source
{
	private final int sourceId;

	public Source()
	{
		sourceId = alGenSources();
		alSourcef(sourceId, AL_GAIN, 1);
		alSourcef(sourceId, AL_PITCH, 1);
		alSource3f(sourceId, AL_POSITION, 0, 0, 0);
	}

	public void play(int buffer)
	{
		stop();
		alSourcei(sourceId, AL_BUFFER, buffer);
		unpause();
	}

	public void pause()
	{
		alSourcePause(sourceId);
	}

	public void unpause()
	{
		alSourcePlay(sourceId);
	}

	public void stop()
	{
		alSourceStop(sourceId);
	}

	public boolean isPlaying()
	{
		return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
	}

	public void setVelocity(float x, float y, float z)
	{
		alSource3f(sourceId, AL_VELOCITY, x, y, z);
	}

	public void setLooping(boolean loop)
	{
		alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
	}

	public void setVolume(float volume)
	{
		alSourcef(sourceId, AL_GAIN, volume * Settings.MASTER_VOLUME.get());
	}

	public void setPitch(float pitch)
	{
		alSourcef(sourceId, AL_PITCH, pitch);
	}

	public void setPosition(float x, float y, float z)
	{
		alSource3f(sourceId, AL_POSITION, x, y, z);
	}

	public Vector3f getPosition()
	{
		float[] x = new float[1];
		float[] y = new float[1];
		float[] z = new float[1];
		alGetSource3f(sourceId, AL_POSITION, x, y, z);
		return new Vector3f(x[0], y[0], z[0]);
	}

	public Vector3f getVelocity()
	{
		float[] x = new float[1];
		float[] y = new float[1];
		float[] z = new float[1];
		alGetSource3f(sourceId, AL_VELOCITY, x, y, z);
		return new Vector3f(x[0], y[0], z[0]);
	}

	public void delete()
	{
		stop();
		alDeleteSources(sourceId);
	}
}
