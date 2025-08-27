package steve6472.orbiter.orlang.parser;

import steve6472.core.tokenizer.*;
import steve6472.orbiter.orlang.AST;
import steve6472.orbiter.orlang.OrlangPrecedence;
import steve6472.orbiter.orlang.OrlangToken;
import steve6472.orbiter.orlang.ParserException;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class UnaryParslet implements PrefixParselet<AST.Node>
{
    @Override
    public AST.Node parse(Tokenizer tokenizer, TokenParser<AST.Node> parser)
    {
        Token type = tokenizer.getCurrentToken().type();
        if (!(type instanceof OrlangToken token))
            throw new ParserException("Token is not of orlang type");
        if (!token.forUnary)
            throw new ParserException("Token '" + token + "' is not for unary operation");
        return new AST.Node.UnaryOp(token, parser.parse(OrlangPrecedence.PREFIX));
    }
}
