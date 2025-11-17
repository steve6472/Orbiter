package steve6472.orbiter.world.collision.parser;

import steve6472.core.tokenizer.PrefixParselet;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.world.collision.ShapeExp;
import steve6472.orbiter.world.collision.expression.NumberExp;
import steve6472.orbiter.world.collision.expression.StringExp;

/**********************
 * Created by steve6472
 * On date: 11/17/2025
 * Project: Orbiter
 *
 ***********************/
public class StringParslet implements PrefixParselet<ShapeExp>
{
	@Override
	public ShapeExp parse(Tokenizer tokenizer, TokenParser<ShapeExp> tokenParser)
	{
		return new StringExp(tokenizer.getCurrentToken().sval().trim());
	}
}
