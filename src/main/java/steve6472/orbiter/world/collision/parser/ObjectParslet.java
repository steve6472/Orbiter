package steve6472.orbiter.world.collision.parser;

import steve6472.core.tokenizer.PrefixParselet;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.world.collision.ShapeExp;
import steve6472.orbiter.world.collision.ShapeToken;
import steve6472.orbiter.world.collision.expression.ObjectExp;
import steve6472.orbiter.world.collision.expression.ParameterExp;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public class ObjectParslet implements PrefixParselet<ShapeExp>
{
    @Override
    public ShapeExp parse(Tokenizer tokenizer, TokenParser<ShapeExp> tokenParser)
    {
        String name = tokenizer.getCurrentToken().sval();
        float[] params;

        if (tokenizer.matchToken(ShapeToken.PARAMETERS_START, false))
        {
            ParameterExp parse = tokenParser.parse(ParameterExp.class);
            params = parse.parameters();
        } else
        {
            params = new float[0];
        }

        return new ObjectExp(name, params);
    }
}
