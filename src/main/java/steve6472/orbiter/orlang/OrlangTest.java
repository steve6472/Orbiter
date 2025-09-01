package steve6472.orbiter.orlang;

import steve6472.core.log.Log;
import steve6472.orbiter.orlang.codec.OrCode;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class OrlangTest
{
    private static final Logger LOGGER = Log.getLogger(OrlangTest.class);

    public static void main()
    {
        String expression = "v.rng2 = math.random(0, 1); v.out = (v.rng2 < 0.5 ? -1 : 1) * 10";
        OrlangParser parser = new OrlangParser();
        OrlangInterpreter interpreter = new OrlangInterpreter();

        OrCode parsed = parser.parse(expression);
        LOGGER.finest("Parsed: " + parsed.code());

        OrlangValue lastValue = null;
        OrlangEnvironment environment = new OrlangEnvironment();
        for (AST.Node node : parsed.code())
        {
            LOGGER.finest("Executing: " + node);
            lastValue = interpreter.interpret(node, environment);
        }
        LOGGER.info("Returned value: " + lastValue);
        LOGGER.finer(environment.toString());
    }
}
