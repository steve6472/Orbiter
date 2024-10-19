package steve6472.orbiter.world.collision.parser;

import steve6472.core.tokenizer.PrefixParselet;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.world.collision.ShapeExp;
import steve6472.orbiter.world.collision.ShapeToken;
import steve6472.orbiter.world.collision.expression.CollisionExp;
import steve6472.orbiter.world.collision.expression.GroupExp;
import steve6472.orbiter.world.collision.expression.ObjectExp;
import steve6472.orbiter.world.collision.expression.ParameterExp;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public class CollisionParslet implements PrefixParselet<ShapeExp>
{
    @Override
    public ShapeExp parse(Tokenizer tokenizer, TokenParser<ShapeExp> tokenParser)
    {
        return new CollisionExp(tokenParser.parse(GroupExp.class));
    }
}
