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
        String expression = "math.sin(7 + 2) + 2";
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
