package steve6472.orbiter.orlang.parser;

import steve6472.core.tokenizer.PrefixParselet;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.orlang.AST;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class NumberParslet implements PrefixParselet<AST.Node>
{
    @Override
    public AST.Node parse(Tokenizer tokenizer, TokenParser<AST.Node> parser)
    {
        return new AST.Node.NumberLiteral(Double.parseDouble(tokenizer.getCurrentToken().sval()));
    }
}
