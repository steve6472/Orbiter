package steve6472.orbiter.network.api;

import java.util.Objects;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public final class ConnectedUser
{
    private final User user;
    private long lastPacket;

    public ConnectedUser(User user)
    {
        this.user = user;
        lastPacket = System.currentTimeMillis();
    }

    public User user()
    {
        return user;
    }

    public void updatePacketTime()
    {
        lastPacket = System.currentTimeMillis();
    }

    public boolean hasTimedOut()
    {
        return lastPacket + Connections.TIMEOUT_MS < System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ConnectedUser that = (ConnectedUser) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(user);
    }

    @Override
    public String toString()
    {
        return "ConnectedUser{" + "user=" + user + ", lastPacket=" + lastPacket + '}';
    }
}