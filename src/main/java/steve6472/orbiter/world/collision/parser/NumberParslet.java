package steve6472.orbiter.world.collision.parser;

import steve6472.core.tokenizer.PrefixParselet;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.world.collision.ShapeExp;
import steve6472.orbiter.world.collision.expression.NumberExp;

/**********************
 * Created by steve6472
 * On date: 12/19/2021
 * Project: ScriptIt
 *
 ***********************/
public class NumberParslet implements PrefixParselet<ShapeExp>
{
	@Override
	public ShapeExp parse(Tokenizer tokenizer, TokenParser<ShapeExp> tokenParser)
	{
		return new NumberExp(Float.parseFloat(tokenizer.getCurrentToken().sval()));
	}
}
