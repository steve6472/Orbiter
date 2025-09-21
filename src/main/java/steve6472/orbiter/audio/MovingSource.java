package steve6472.orbiter.audio;

import org.joml.Vector3f;

import java.util.function.Supplier;

/**********************
 * Created by steve6472
 * On date: 25.10.2020
 * Project: CaveGame
 *
 ***********************/
public class MovingSource extends Source
{
	Supplier<Vector3f> position;
	Supplier<Vector3f> velocity;

	public MovingSource(Supplier<Vector3f> position, Supplier<Vector3f> velocity)
	{
		super();
        this.position = position;
        this.velocity = velocity;
		tick();
    }

	public void tick()
	{
		Vector3f pos = position.get();
		setPosition(pos.x, pos.y, pos.z);

		Vector3f vel = velocity.get();
		setVelocity(vel.x, vel.y, vel.z);
	}
}
