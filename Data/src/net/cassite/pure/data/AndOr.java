package net.cassite.pure.data;

public interface AndOr {
        And and(Condition condition);

        And and(And a);

        And and(Or or);

        And and(ExpressionBoolean expBool);

        Or or(Condition condition);

        Or or(And a);

        Or or(Or o);

        Or or(ExpressionBoolean expBool);
}
