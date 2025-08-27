package steve6472.orbiter.orlang;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class OrlangInterpreter
{
    public OrlangValue interpret(AST.Node nodeExpression, OrlangEnvironment environment)
    {
        return switch (nodeExpression)
        {
            case AST.Node.NumberLiteral exp -> new OrlangValue.Number(exp.value());
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
                    case MUL -> new OrlangValue.Number(checkNum(left, exp.type()) * checkNum(right, exp.type()));
                    case DIV -> new OrlangValue.Number(checkNum(left, exp.type()) / checkNum(right, exp.type()));
                    case ADD -> new OrlangValue.Number(checkNum(left, exp.type()) + checkNum(right, exp.type()));
                    case SUB -> new OrlangValue.Number(checkNum(left, exp.type()) - checkNum(right, exp.type()));
                    default -> throw new IllegalStateException("Unexpected value: " + exp.type());
                };
            }

            case AST.Node.UnaryOp exp ->
            {
                OrlangValue right = interpret(exp.expression(), environment);
                yield switch (exp.type())
                {
                    case NOT -> OrlangValue.bool(!checkBool(right, exp.type()));
                    case SUB -> new OrlangValue.Number(-checkNum(right, exp.type()));
                    default -> throw new IllegalStateException("Unexpected value: " + exp.type());
                };
            }

            case AST.Node.Identifier exp -> environment.getValue(exp);
            case AST.Node.Return exp -> interpret(exp.expression(), environment.nest());

            default -> throw new IllegalStateException("Unexpected value: " + nodeExpression);
        };
    }

    private boolean checkBool(OrlangValue value, OrlangToken operation)
    {
        if (!(value instanceof OrlangValue.Bool bool))
            throw new IllegalStateException("Operation " + operation.getSymbol() + " expected a boolean result");
        return bool.value();
    }

    private double checkNum(OrlangValue value, OrlangToken operation)
    {
        if (!(value instanceof OrlangValue.Number num))
            throw new IllegalStateException("Operation " + operation.getSymbol() + " expected a number result");
        return num.value();
    }
}
