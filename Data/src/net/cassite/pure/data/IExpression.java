package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/13.
 */
public interface IExpression {
    ExpressionType expType();

    Object[] expArgs();

    boolean equals(Object o);

    int hashCode();
}
