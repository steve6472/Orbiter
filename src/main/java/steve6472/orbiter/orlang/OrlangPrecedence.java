package steve6472.orbiter.orlang;

import steve6472.core.tokenizer.Precedence;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public enum OrlangPrecedence implements Precedence
{
    ANYTHING,
    SCOPE,

    ASSIGNMENT,
    CONDITIONAL,
    ARRAY_ACCESS,

    COALESCE,

    AND,
    OR,

    COMPARE,

    SUM,
    PRODUCT,
    PREFIX,
    FUNC_CALL
}
