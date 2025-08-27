package steve6472.orbiter.orlang.parser;

import steve6472.core.tokenizer.PrefixParselet;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.orlang.AST;
import steve6472.orbiter.orlang.OrlangPrecedence;
import steve6472.orbiter.orlang.OrlangToken;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class GroupParslet implements PrefixParselet<AST.Node>
{
    @Override
    public AST.Node parse(Tokenizer tokenizer, TokenParser<AST.Node> parser)
    {
        AST.Node expression = parser.parse(OrlangPrecedence.ANYTHING);
        parser.tokenizer.consumeToken(OrlangToken.PARENTHESIS_RIGHT);
        return expression;
    }
}
