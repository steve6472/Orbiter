package steve6472.orbiter.orlang;

import steve6472.core.tokenizer.MainTokens;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.TokenStorage;
import steve6472.orbiter.orlang.parser.*;

import java.util.List;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class OrlangParser
{
    private final TokenStorage tokenStorage;
    private final TokenParser<AST.Node> parser;

    public OrlangParser()
    {
        tokenStorage = new TokenStorage();
        fillTokens();

        parser = new TokenParser<>(tokenStorage);
        fillParser();
        parser.setIteratingCondition(() -> parser.tokenizer.matchToken(OrlangToken.SEMICOLON, true));
    }

    private void fillParser()
    {
        parser.prefixParslet(OrlangToken.TRUE, new BoolParslet());
        parser.prefixParslet(OrlangToken.FALSE, new BoolParslet());

        UnaryParslet unaryParslet = new UnaryParslet();
        for (OrlangToken value : OrlangToken.values())
        {
            if (value.forUnary)
            {
                parser.prefixParslet(value, unaryParslet);
            }
        }
        parser.prefixParslet(MainTokens.NAME, new IdentifierParslet());
        parser.prefixParslet(MainTokens.NAME, new IdentifierParslet());
        parser.prefixParslet(MainTokens.NUMBER_DOUBLE, new NumberParslet());
        parser.prefixParslet(MainTokens.NUMBER_INT, new NumberParslet());
        parser.prefixParslet(OrlangToken.RETURN, new ReturnParslet());
        parser.prefixParslet(OrlangToken.PARENTHESIS_LEFT, new GroupParslet());

        parser.infixParslet(OrlangToken.ASSIGN, new AssignParslet(parser));

        for (OrlangToken value : OrlangToken.values())
        {
            if (value.binaryPrecedence != null)
            {
                parser.infixParslet(value, new BinaryParslet(value.binaryPrecedence, parser));
            }
        }
    }

    private void fillTokens()
    {
        tokenStorage.addTokens(OrlangToken.class);
    }

    public List<AST.Node> parse(String toParse)
    {
        TokenParser<AST.Node> tokenize = parser.tokenize(toParse);
//        System.out.println(tokenize.tokenizer.getTokens());
        return tokenize.parseAll();
    }
}
