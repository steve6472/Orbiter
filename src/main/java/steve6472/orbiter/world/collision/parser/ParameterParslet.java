package steve6472.orbiter.world.collision.parser;

import com.mojang.datafixers.util.Either;
import steve6472.core.tokenizer.*;
import steve6472.orbiter.world.collision.ShapeExp;
import steve6472.orbiter.world.collision.ShapeToken;
import steve6472.orbiter.world.collision.expression.NumberExp;
import steve6472.orbiter.world.collision.expression.ParameterExp;
import steve6472.orbiter.world.collision.expression.StringExp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public class ParameterParslet implements PrefixParselet<ShapeExp>
{
    @Override
    public ShapeExp parse(Tokenizer tokenizer, TokenParser<ShapeExp> tokenParser)
    {
        List<Either<Float, String>> params = new ArrayList<>(4);
        while (true)
        {
            Token nextType = tokenizer.peekToken().type();
            if (!(nextType == MainTokens.NUMBER_DOUBLE || nextType == MainTokens.NUMBER_INT || nextType == MainTokens.STRING))
                break;

            if (nextType == MainTokens.STRING)
            {
                params.add(Either.right(tokenParser.parse(StringExp.class).value()));
            } else
            {
                params.add(Either.left(tokenParser.parse(NumberExp.class).value()));
            }

            if (!tokenizer.matchToken(ShapeToken.SEPARATOR, true))
            {
                break;
            }
        }

        tokenizer.consumeToken(ShapeToken.PARAMETERS_END);

        //noinspection unchecked
        return new ParameterExp(params.toArray(Either[]::new));
    }
}
