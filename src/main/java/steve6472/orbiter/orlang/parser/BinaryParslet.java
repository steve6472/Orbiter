package steve6472.orbiter.orlang.parser;

import steve6472.core.tokenizer.*;
import steve6472.orbiter.orlang.AST;
import steve6472.orbiter.orlang.OrlangToken;
import steve6472.orbiter.orlang.ParserException;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class BinaryParslet implements InfixParslet<AST.Node>
{
    private final Precedence precedence;
    private final TokenParser<AST.Node> parser;

    public BinaryParslet(Precedence precedence, TokenParser<AST.Node> parser)
    {
        this.precedence = precedence;
        this.parser = parser;
    }

    @Override
    public AST.Node parse(Tokenizer tokenizer, AST.Node left)
    {
        Token type = tokenizer.getCurrentToken().type();
        if (!(type instanceof OrlangToken token))
            throw new ParserException("Token is not of orlang type");
        if (token.binaryPrecedence == null)
            throw new ParserException("Token '" + token + "' is not for binary operation");
        AST.Node parsed = parser.parse(getPrecedence());
        if (parsed == null)
            throw new ParserException("null returned for right expression. Current precedence: " + precedence);
        return new AST.Node.BinOp(token, left, parsed);
    }

    @Override
    public Precedence getPrecedence()
    {
        return precedence;
    }
}
