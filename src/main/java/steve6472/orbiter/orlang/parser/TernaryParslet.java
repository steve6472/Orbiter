package steve6472.orbiter.orlang.parser;

import steve6472.core.tokenizer.InfixParslet;
import steve6472.core.tokenizer.Precedence;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.Tokenizer;
import steve6472.orbiter.orlang.AST;
import steve6472.orbiter.orlang.OrlangPrecedence;
import steve6472.orbiter.orlang.OrlangToken;
import steve6472.orbiter.orlang.ParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class TernaryParslet implements InfixParslet<AST.Node>
{
    private final TokenParser<AST.Node> parser;

    public TernaryParslet(TokenParser<AST.Node> parser)
    {
        this.parser = parser;
    }

    @Override
    public AST.Node parse(Tokenizer tokenizer, AST.Node left)
    {
        AST.Node ifTrue = parser.parse(getPrecedence());
        tokenizer.consumeToken(OrlangToken.COLON);
        AST.Node ifFalse = parser.parse(getPrecedence());
        return new AST.Node.Ternary(left, ifTrue, ifFalse);
    }

    @Override
    public Precedence getPrecedence()
    {
        return OrlangPrecedence.CONDITIONAL;
    }
}
