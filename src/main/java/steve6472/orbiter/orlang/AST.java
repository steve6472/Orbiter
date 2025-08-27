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
        record Identifier(VarContext context, String name, String[] path) implements Node {

            public Identifier(VarContext context, String name)
            {
                this(context, name, new String[0]);
            }

            @Override
            public String toString()
            {
                return "Identifier{" + "context=" + context + ", name='" + name + '\'' + ", path=" + Arrays.toString(path) + '}';
            }
        }

        record NumberLiteral(double value) implements Node {}
        record BoolLiteral(boolean value) implements Node {}

        record Assign(Identifier identifier, Node expression) implements Node {}
        record FunctionCall(Identifier identifier, Node[] arguments) implements Node {
            @Override
            public String toString()
            {
                return "FunctionCall{" + "identifier=" + identifier + ", arguments=" + Arrays.toString(arguments) + '}';
            }
        }
        record BinOp(OrlangToken type, Node left, Node right) implements Node {}
        record UnaryOp(OrlangToken type, Node expression) implements Node {}
        record Return(Node expression) implements Node {}
    }
}
