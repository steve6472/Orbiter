package steve6472.orbiter.orlang;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;
import java.util.Locale;

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

            public static final Codec<Identifier> CODEC = Codec.STRING.comapFlatMap(s -> {
                if (!s.contains("."))
                    return DataResult.error(() -> "Missing context");
                String[] split = s.split("\\.");
                if (split.length != 2)
                    return DataResult.error(() -> "Contains more than one '.' (or zero)");
                split[0] = split[0].toLowerCase(Locale.ROOT);
                VarContext context = VarContext.getContext(split[0]);
                if (context == null)
                    return DataResult.error(() -> "Context '" + split[0] + "' is invalid");
                return DataResult.success(new Identifier(context, split[1]));
            }, id -> id.context().contextName() + "." + id.name());
        }

        record NumberLiteral(double value) implements Node {}
        record BoolLiteral(boolean value) implements Node {}
        record StringLiteral(String value) implements Node {}

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
