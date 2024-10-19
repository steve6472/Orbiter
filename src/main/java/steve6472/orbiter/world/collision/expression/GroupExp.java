package steve6472.orbiter.world.collision.expression;

import steve6472.orbiter.world.collision.ShapeExp;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public class GroupExp implements ShapeExp
{
    private final ShapeExp[] objects;

    public GroupExp(ShapeExp[] objects)
    {
        this.objects = objects;
        ensureUnique();
    }

    private void ensureUnique()
    {
        ObjectExp[] objs = getObjects();
        long count = Arrays.stream(objs).map(ObjectExp::type).distinct().count();
        if (count != objs.length)
            throw new RuntimeException("Duplicite objects! " + this);
    }

    public GroupExp[] getGroups()
    {
        return Arrays.stream(objects)
            .filter(exp -> exp instanceof GroupExp)
            .map(exp -> (GroupExp) exp)
            .toArray(GroupExp[]::new);
    }

    public boolean hasGroups()
    {
        return getGroups().length > 0;
    }

    public ObjectExp[] getObjects()
    {
        return Arrays.stream(objects)
            .filter(exp -> exp instanceof ObjectExp)
            .map(exp -> (ObjectExp) exp)
            .toArray(ObjectExp[]::new);
    }

    public Collection<ObjectExp> getAll(Collection<String> name)
    {
        return Arrays.stream(getObjects())
            .filter(exp -> name.contains(exp.type()))
            .toList();
    }

    public boolean isEmpty()
    {
        return objects.length == 0;
    }

    @Override
    public String toString()
    {
        return "GroupExp{" + "objects=" + Arrays.toString(objects) + '}';
    }
}
