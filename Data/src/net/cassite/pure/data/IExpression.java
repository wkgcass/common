package net.cassite.pure.data;

/**
 * 表达式
 */
public interface IExpression {
    /**
     * 获取表达式类型
     *
     * @return 表达式类型
     */
    ExpressionType expType();

    /**
     * 获取表达式接收的参数
     *
     * @return 表达式的参数
     */
    Object[] expArgs();

    /**
     * 使用DataUtils#expressionEquals(IExpression, Object) 完成该方法
     *
     * @param o 要比较的对象
     * @return true则相同, false则不同
     * @see DataUtils#expressionEquals(IExpression, Object)
     * @see Object#equals(Object)
     */
    boolean equals(Object o);

    /**
     * 使用DataUtils#expressionHashCode(IExpression) 完成该方法
     *
     * @return hashCode
     * @see DataUtils#expressionHashCode(IExpression)
     * @see Object#hashCode()
     */
    int hashCode();
}
