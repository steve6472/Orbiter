package steve6472.orbiter.world.collision.parser;

import steve6472.core.tokenizer.MainTokens;
import steve6472.core.tokenizer.PrefixParselet;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.world.collision.ShapeExp;
import steve6472.orbiter.world.collision.ShapeToken;
import steve6472.orbiter.world.collision.expression.NumberExp;
import steve6472.orbiter.world.collision.expression.ParameterExp;

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
        List<Float> params = new ArrayList<>(4);
        while (tokenizer.peekToken().type() == MainTokens.NUMBER_DOUBLE || tokenizer.peekToken().type() == MainTokens.NUMBER_INT)
        {
            params.add(tokenParser.parse(NumberExp.class).value());

            if (!tokenizer.matchToken(ShapeToken.SEPARATOR, true))
            {
                break;
            }
        }

        tokenizer.consumeToken(ShapeToken.PARAMETERS_END);

        float[] floats = new float[params.size()];
        for (int i = 0; i < params.size(); i++)
        {
            floats[i] = params.get(i);
        }

        return new ParameterExp(floats);
    }
}
