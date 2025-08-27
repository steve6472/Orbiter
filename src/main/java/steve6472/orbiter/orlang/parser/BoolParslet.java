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
public class BoolParslet implements PrefixParselet<AST.Node>
{
    @Override
    public AST.Node parse(Tokenizer tokenizer, TokenParser<AST.Node> parser)
    {
        String value = tokenizer.getCurrentToken().sval();

        return new AST.Node.BoolLiteral("true".equals(value));
    }
}
