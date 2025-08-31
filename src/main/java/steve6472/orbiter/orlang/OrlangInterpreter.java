package steve6472.orbiter.orlang;

import steve6472.orbiter.orlang.codec.OrCode;

import static steve6472.orbiter.orlang.Orlang.checkBool;
import static steve6472.orbiter.orlang.Orlang.checkNum;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class OrlangInterpreter
{
    public OrlangValue interpret(OrCode code, OrlangEnvironment environment)
    {
        OrlangValue lastValue = null;
        for (AST.Node node : code.code())
        {
            lastValue = interpret(node, environment);
        }
        return lastValue;
    }

    public OrlangValue interpret(AST.Node nodeExpression, OrlangEnvironment environment)
    {
        return switch (nodeExpression)
        {
            case AST.Node.NumberLiteral exp -> OrlangValue.num(exp.value());
            case AST.Node.BoolLiteral exp -> OrlangValue.bool(exp.value());

            case AST.Node.Assign exp -> {
                var result = interpret(exp.expression(), environment.nest());
                environment.setValue(exp.identifier(), result);
                yield result;
            }

            case AST.Node.BinOp exp ->
            {
                OrlangValue left = interpret(exp.left(), environment);
                OrlangValue right = interpret(exp.right(), environment);
                yield switch (exp.type())
                {
                    case OR -> OrlangValue.bool(checkBool(left, exp.type()) || checkBool(right, exp.type()));
                    case AND -> OrlangValue.bool(checkBool(left, exp.type()) && checkBool(right, exp.type()));
                    case LESS -> OrlangValue.bool(checkNum(left, exp.type()) < checkNum(right, exp.type()));
                    case LESS_EQ -> OrlangValue.bool(checkNum(left, exp.type()) <= checkNum(right, exp.type()));
                    case GREATER -> OrlangValue.bool(checkNum(left, exp.type()) > checkNum(right, exp.type()));
                    case GREATER_EQ -> OrlangValue.bool(checkNum(left, exp.type()) >= checkNum(right, exp.type()));
                    case EQUAL ->
                    {
                        if (left instanceof OrlangValue.Bool leftB)
                        {
                            if (right instanceof OrlangValue.Bool rightB)
                            {
                                yield OrlangValue.bool(leftB.value() == rightB.value());
                            } else
                            {
                                throw new IllegalArgumentException("Can not mix bool == num");
                            }
                        } else
                        {
                            var leftN = ((OrlangValue.Number) left);
                            if (right instanceof OrlangValue.Number rightN)
                            {
                                yield OrlangValue.bool(leftN.value() == rightN.value());
                            } else
                            {
                                throw new IllegalArgumentException("Can not mix num == bool");
                            }
                        }
                    }
                    case NOT_EQUAL ->
                    {
                        if (left instanceof OrlangValue.Bool leftB)
                        {
                            if (right instanceof OrlangValue.Bool rightB)
                            {
                                yield OrlangValue.bool(leftB.value() != rightB.value());
                            } else
                            {
                                throw new IllegalArgumentException("Can not mix bool == num");
                            }
                        } else
                        {
                            var leftN = ((OrlangValue.Number) left);
                            if (right instanceof OrlangValue.Number rightN)
                            {
                                yield OrlangValue.bool(leftN.value() != rightN.value());
                            } else
                            {
                                throw new IllegalArgumentException("Can not mix num == bool");
                            }
                        }
                    }
                    case MUL -> OrlangValue.num(checkNum(left, exp.type()) * checkNum(right, exp.type()));
                    case DIV -> OrlangValue.num(checkNum(left, exp.type()) / checkNum(right, exp.type()));
                    case ADD -> OrlangValue.num(checkNum(left, exp.type()) + checkNum(right, exp.type()));
                    case SUB -> OrlangValue.num(checkNum(left, exp.type()) - checkNum(right, exp.type()));
                    default -> throw new IllegalStateException("Unexpected value: " + exp.type());
                };
            }

            case AST.Node.UnaryOp exp ->
            {
                OrlangValue right = interpret(exp.expression(), environment);
                yield switch (exp.type())
                {
                    case NOT -> OrlangValue.bool(!checkBool(right, exp.type()));
                    case SUB -> OrlangValue.num(-checkNum(right, exp.type()));
                    default -> throw new IllegalStateException("Unexpected value: " + exp.type());
                };
            }

            case AST.Node.FunctionCall exp ->
            {
                if (exp.identifier().context() == VarContext.MATH)
                {
                    OrlangValue.Func func = Orlang.MATH_FUNCTIONS.get(exp.identifier().name());
                    if (func == null)
                        throw new RuntimeException("Function " + "'math." + exp.identifier().name() + "' not found");
                    AST.Node[] arguments = exp.arguments();
                    OrlangValue[] values = new OrlangValue[arguments.length];
                    for (int i = 0; i < arguments.length; i++)
                    {
                        values[i] = interpret(arguments[i], environment);
                    }
                    yield func.eval(values);
                }
                throw new IllegalStateException("Unexpected context " + exp.identifier().context());
            }

            case AST.Node.Identifier exp -> environment.getValue(exp);
            case AST.Node.Return exp -> interpret(exp.expression(), environment.nest());

            default -> throw new IllegalStateException("Unexpected value: " + nodeExpression);
        };
    }
}
