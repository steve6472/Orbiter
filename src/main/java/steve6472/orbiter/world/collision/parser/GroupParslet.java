package steve6472.orbiter.world.collision.parser;

import steve6472.core.tokenizer.MainTokens;
import steve6472.core.tokenizer.PrefixParselet;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.world.collision.ShapeExp;
import steve6472.orbiter.world.collision.ShapeToken;
import steve6472.orbiter.world.collision.expression.GroupExp;
import steve6472.orbiter.world.collision.expression.NumberExp;
import steve6472.orbiter.world.collision.expression.ObjectExp;
import steve6472.orbiter.world.collision.expression.ParameterExp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public class GroupParslet implements PrefixParselet<ShapeExp>
{
    @Override
    public ShapeExp parse(Tokenizer tokenizer, TokenParser<ShapeExp> tokenParser)
    {
        List<ShapeExp> objects = new ArrayList<>(4);
        while (tokenizer.peekToken().type() == MainTokens.NAME || tokenizer.peekToken().type() == ShapeToken.GROUP_START)
        {
            if (tokenizer.peekToken().type() == MainTokens.NAME)
            {
                objects.add(tokenParser.parse(ObjectExp.class));
            } else
            {
                objects.add(tokenParser.parse(GroupExp.class));
            }

            if (!tokenizer.matchToken(ShapeToken.SEPARATOR, true))
            {
                break;
            }
        }

        tokenizer.consumeToken(ShapeToken.GROUP_END);

        return new GroupExp(objects.toArray(ShapeExp[]::new));
    }
}
