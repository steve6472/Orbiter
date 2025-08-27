package steve6472.orbiter.orlang;

import steve6472.core.log.Log;

import java.util.List;
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
        String expression = "temp.foo = 1.23; temp.bar = 2; return temp.bar + temp.foo;";
        OrlangParser parser = new OrlangParser();
        OrlangInterpreter interpreter = new OrlangInterpreter();

        List<AST.Node> parsed = parser.parse(expression);

        OrlangValue lastValue = null;
        OrlangEnvironment environment = new OrlangEnvironment();
        for (AST.Node node : parsed)
        {
            LOGGER.finest("Executing: " + node);
            lastValue = interpreter.interpret(node, environment);
        }
        LOGGER.info("Returned value: " + lastValue);
        LOGGER.finer(environment.toString());
    }
}
