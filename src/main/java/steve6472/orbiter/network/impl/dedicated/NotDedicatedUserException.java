package steve6472.orbiter.network.impl.dedicated;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public class NotDedicatedUserException extends RuntimeException
{
    public NotDedicatedUserException()
    {
        super("Passed User is not an instance of DedicatedUser!");
    }
}
