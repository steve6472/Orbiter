package steve6472.orbiter.util;

import java.util.Objects;

/**
 * Created by steve6472
 * Date: 11/26/2025
 * Project: Orbiter <br>
 */
public class SettableObject<T>
{
    private T value;

    private SettableObject(T value)
    {
        this.value = value;
    }

    /// Create already set object
    public static <T> SettableObject<T> of(T value)
    {
        return new SettableObject<>(value);
    }

    public static <T> SettableObject<T> create()
    {
        return new SettableObject<>(null);
    }

    public void set(T value)
    {
        Objects.requireNonNull(value);
        if (this.value == null)
            this.value = value;
    }

    public T get()
    {
        if (value == null)
            throw new IllegalStateException("Tried to get without setting a value");
        return value;
    }

    @Override
    public String toString()
    {
        return "SettableObject{" + "value=" + value + '}';
    }
}