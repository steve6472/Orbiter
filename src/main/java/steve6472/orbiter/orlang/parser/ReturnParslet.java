package steve6472.orbiter.orlang.parser;

import steve6472.core.tokenizer.PrefixParselet;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.orlang.AST;
import steve6472.orbiter.orlang.OrlangPrecedence;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class ReturnParslet implements PrefixParselet<AST.Node>
{
    @Override
    public AST.Node parse(Tokenizer tokenizer, TokenParser<AST.Node> parser)
    {
        return new AST.Node.Return(parser.parse(OrlangPrecedence.ANYTHING));
    }
}
