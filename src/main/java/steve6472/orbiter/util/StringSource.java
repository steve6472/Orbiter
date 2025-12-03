package steve6472.orbiter.util;

import com.mojang.serialization.Codec;
import steve6472.orlang.Orlang;
import steve6472.orlang.OrlangEnvironment;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.codec.OrCode;

/**
 * Created by steve6472
 * Date: 11/25/2025
 * Project: Orbiter <br>
 */
public interface StringSource
{
    Codec<StringSource> CODEC = Codec.STRING.xmap(StringSource::fromRaw, StringSource::getRaw);

    String get(OrlangEnvironment env);
    String getRaw();
    boolean isLiteral();

    default boolean isScript()
    {
        return !isLiteral();
    }

    static StringSource literal(String value)
    {
        return new Literal(value);
    }

    static StringSource script(OrCode code)
    {
        return new Script(code);
    }

    static StringSource fromRaw(String raw)
    {
        if (raw.startsWith("$"))
        {
            return script(Orlang.parser.parse(raw.substring(1)));
        }
        return literal(raw);
    }

    final class Literal implements StringSource
    {
        private final String value;

        Literal(String value)
        {
            this.value = value;
        }

        @Override
        public String get(OrlangEnvironment env)
        {
            return value;
        }

        @Override
        public String getRaw()
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

    final class Script implements StringSource
    {
        private final OrCode code;

        Script(OrCode code)
        {
            this.code = code;
        }

        @Override
        public String get(OrlangEnvironment env)
        {
            OrlangValue interpret = Orlang.interpreter.interpret(code, env);
            if (interpret instanceof OrlangValue.StringVal val)
                return val.value();
            throw new IllegalArgumentException("Orlang expression return value was not string. Code: '%s'".formatted(code.codeStr()));
        }

        @Override
        public String getRaw()
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
