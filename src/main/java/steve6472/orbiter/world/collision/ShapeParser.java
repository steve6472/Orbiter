package steve6472.orbiter.world.collision;

import steve6472.core.tokenizer.MainTokens;
import steve6472.core.tokenizer.TokenParser;
import steve6472.core.tokenizer.TokenStorage;
import steve6472.orbiter.world.collision.expression.CollisionExp;
import steve6472.orbiter.world.collision.parser.*;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public class ShapeParser
{
    private final TokenStorage tokenStorage;
    private final TokenParser<ShapeExp> parser;

    /*
     * collision{{capsule(0.5, 1.6)},{sphere(0.4),offset(0, 0, 0)}}
     */
    public ShapeParser()
    {
        tokenStorage = new TokenStorage();
        fillTokens();

        parser = new TokenParser<>(tokenStorage);
        fillParser();
    }

    private void fillParser()
    {
        parser.prefixParslet(MainTokens.NUMBER_DOUBLE, new NumberParslet());
        parser.prefixParslet(MainTokens.NUMBER_INT, new NumberParslet());
        parser.prefixParslet(MainTokens.STRING, new StringParslet());

        parser.prefixParslet(ShapeToken.COLLISION, new CollisionParslet());

        parser.prefixParslet(ShapeToken.PARAMETERS_START, new ParameterParslet());
        parser.prefixParslet(MainTokens.NAME, new ObjectParslet());
        parser.prefixParslet(ShapeToken.GROUP_START, new GroupParslet());
    }

    private void fillTokens()
    {
        tokenStorage.addTokens(ShapeToken.class);
    }

    public CollisionExp parse(String toParse)
    {
        List<ShapeExp> shapeExps = parser.tokenize(toParse).parseAll();
        if (shapeExps.size() != 1)
            throw new RuntimeException("Parsing returned more than one result");
        ShapeExp first = shapeExps.getFirst();
        if (!(first instanceof CollisionExp collisionExp))
            throw new RuntimeException("Did not return collision expression");
        return collisionExp;
    }
}
