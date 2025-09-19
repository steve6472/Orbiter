package steve6472.orbiter.audio;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_error;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**********************
 * Created by steve6472
 * On date: 25.10.2020
 * Project: CaveGame
 *
 ***********************/
public class SoundMaster
{
	private final Vector3f UP = new Vector3f(0, 1, 0);
	private final Vector3f AT = new Vector3f(0, 0, 0);
	private final Matrix4f INV_CAM = new Matrix4f();
	private final float[] ORIENTATION_DATA = new float[6];

	private final IntList buffers = new IntArrayList();

	private long device, context;

	public void setup()
	{
		String defaultDeviceName = alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
		device = alcOpenDevice(defaultDeviceName);

		int[] attributes = {0};
		context = alcCreateContext(device, attributes);
		alcMakeContextCurrent(context);

		ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

		// TODO: replace with just disabling sound
		if (!alCapabilities.OpenAL11)
		{
			throw new RuntimeException("OpenAL 11 is not supported");
		}
	}

	public void setListenerPosition(float x, float y, float z)
	{
		AL11.alListener3f(AL_POSITION, x, y, z);
	}

	public void setListenerOrientation(Matrix4f viewMatrix)
	{
		AT.set(0, 0, -1);
		UP.set(0, 1, 0);

		INV_CAM.set(viewMatrix).invert();
		INV_CAM.transformDirection(AT);
		INV_CAM.transformDirection(UP);

		ORIENTATION_DATA[0] = AT.x();
		ORIENTATION_DATA[1] = AT.y();
		ORIENTATION_DATA[2] = AT.z();
		ORIENTATION_DATA[3] = UP.x();
		ORIENTATION_DATA[4] = UP.y();
		ORIENTATION_DATA[5] = UP.z();

		AL11.alListenerfv(AL_ORIENTATION, ORIENTATION_DATA);
	}

	public int loadSound(String path)
	{
		int channels, sampleRate;
		ShortBuffer rawAudioBuffer;

		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer channelsBuffer = stack.mallocInt(1);
			IntBuffer sampleRateBuffer = stack.mallocInt(1);

			rawAudioBuffer = stb_vorbis_decode_filename(path, channelsBuffer, sampleRateBuffer);

			channels = channelsBuffer.get();
			sampleRate = sampleRateBuffer.get();
		}

		if (rawAudioBuffer == null)
			throw new RuntimeException("Failed to load sound " + path);

		//Find the correct OpenAL format
		int format = -1;
		if (channels == 1)
		{
			format = AL_FORMAT_MONO16;
		} else if (channels == 2)
		{
			format = AL_FORMAT_STEREO16;
		}

		//Request space for the buffer
		int bufferPointer = alGenBuffers();

		//Send the data to OpenAL
		alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

		//Free the memory allocated by STB
		free(rawAudioBuffer);

		buffers.add(bufferPointer);

		return bufferPointer;
	}

	public void cleanup()
	{
		for (int buffer : buffers)
		{
			AL11.alDeleteBuffers(buffer);
		}

		alcDestroyContext(context);
		alcCloseDevice(device);
	}
}
