package steve6472.orbiter.util;

import com.mojang.serialization.Codec;
import steve6472.core.log.Log;
import steve6472.orlang.Orlang;
import steve6472.orlang.OrlangEnvironment;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.codec.OrCode;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/25/2025
 * Project: Orbiter <br>
 */
public interface ValueSource
{
    Logger SOURCE_LOGGER = Log.getLogger(ValueSource.class);

    Codec<ValueSource> CODEC = Codec.STRING.xmap(ValueSource::fromRaw, Objects::toString);

    Object get(OrlangEnvironment env);
    Object getRaw();
    boolean isLiteral();

    default boolean isScript()
    {
        return !isLiteral();
    }

    static ValueSource literal(Object value)
    {
        return new Literal(value);
    }

    static ValueSource script(OrCode code)
    {
        return new Script(code);
    }

    static ValueSource fromRaw(String raw)
    {
        if (raw.startsWith("$"))
        {
            return script(Orlang.parser.parse(raw.substring(1)));
        }
        return literal(raw);
    }

    final class Literal implements ValueSource
    {
        private final Object value;

        Literal(Object value)
        {
            this.value = value;
        }

        @Override
        public Object get(OrlangEnvironment env)
        {
            return value;
        }

        @Override
        public Object getRaw()
        {
            return value;
        }

        @Override
        public boolean isLiteral()
        {
            return true;
        }

        @Override
        public String toString()
        {
            return "Literal{" + "value='" + value + '\'' + '}';
        }
    }

    final class Script implements ValueSource
    {
        private final OrCode code;

        Script(OrCode code)
        {
            this.code = code;
        }

        @Override
        public Object get(OrlangEnvironment env)
        {
            try
            {
                OrlangValue interpret = Orlang.interpreter.interpret(code, env);
                return OrlangValue.dumbCast(interpret);
            } catch (Exception exception)
            {
                SOURCE_LOGGER.severe("Error getting from script '%s'".formatted(code.codeStr()));
                throw exception;
            }
        }

        @Override
        public Object getRaw()
        {
            return code.codeStr();
        }

        @Override
        public boolean isLiteral()
        {
            return false;
        }

        @Override
        public String toString()
        {
            return "Script{" + "code=" + code + '}';
        }
    }
}
