package steve6472.orbiter.orlang.parser;

import steve6472.core.tokenizer.*;
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
public class FunctionCallParslet implements InfixParslet<AST.Node>
{
    private final TokenParser<AST.Node> parser;

    public FunctionCallParslet(TokenParser<AST.Node> parser)
    {
        this.parser = parser;
    }

    @Override
    public AST.Node parse(Tokenizer tokenizer, AST.Node left)
    {
        if (!(left instanceof AST.Node.Identifier identifier))
            throw new ParserException("Left is not an Identifier");

        if (tokenizer.matchToken(OrlangToken.PARENTHESIS_RIGHT, true))
        {
            return new AST.Node.FunctionCall(identifier, new AST.Node[0]);
        }

        List<AST.Node> args = new ArrayList<>();

        while (true)
        {
            AST.Node arg = parser.parse(OrlangPrecedence.ANYTHING);
            args.add(arg);

            if (tokenizer.matchToken(OrlangToken.PARENTHESIS_RIGHT, true))
                break;

            if (!tokenizer.matchToken(OrlangToken.COMMA, true))
                throw new ParserException("Expected ',' or ')' in argument list, got " + tokenizer.peekToken());
        }

        return new AST.Node.FunctionCall(identifier, args.toArray(new AST.Node[0]));
    }

    @Override
    public Precedence getPrecedence()
    {
        return OrlangPrecedence.FUNC_CALL;
    }
}
