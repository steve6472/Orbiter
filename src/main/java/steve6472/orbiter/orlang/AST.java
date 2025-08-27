package steve6472.orbiter.orlang;

import java.util.Arrays;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public final class AST
{
    public interface Node
    {
        record Identifier(String name, String[] path) implements Node {
            @Override
            public String toString()
            {
                return "Identifier{" + "name='" + name + '\'' + ", path=" + Arrays.toString(path) + '}';
            }
        }

        record NumberLiteral(double value) implements Node {}
        record BoolLiteral(boolean value) implements Node {}

        record Assign(Identifier identifier, Node expression) implements Node {}
        record BinOp(OrlangToken type, Node left, Node right) implements Node {}
        record UnaryOp(OrlangToken type, Node expression) implements Node {}
        record Return(Node expression) implements Node {}
    }
}
