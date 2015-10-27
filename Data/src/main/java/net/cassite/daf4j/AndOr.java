package net.cassite.daf4j;

/**
 * 表示能够执行"与","或"逻辑操作的对象
 */
public interface AndOr {
    /**
     * 和一个Condition对象进行与操作
     *
     * @param condition 条件对象
     * @return 表示"与"关系的And对象
     */
    And and(Condition condition);

    /**
     * 和一个And对象进行与操作
     *
     * @param a "与"对象
     * @return 表示"与"关系的And对象
     */
    And and(And a);

    /**
     * 和一个Or对象进行与操作
     *
     * @param or "或"对象
     * @return 表示"与"关系的And对象
     */
    And and(Or or);

    /**
     * 和一个返回布尔值的表达式进行与操作
     *
     * @param expBool 返回布尔值的表达式
     * @return 表示"与"关系的And对象
     */
    And and(ExpressionBoolean expBool);

    /**
     * 和一个Condition对象进行或操作
     *
     * @param condition 条件对象
     * @return 表示"或"关系的Or对象
     */
    Or or(Condition condition);

    /**
     * 和一个And对象进行或操作
     *
     * @param a "与"对象
     * @return 表示"或"关系的Or对象
     */
    Or or(And a);

    /**
     * 和一个Or对象进行或操作
     *
     * @param o "或"对象
     * @return 表示"或"关系的Or对象
     */
    Or or(Or o);

    /**
     * 和一个返回布尔值的表达式进行或操作
     *
     * @param expBool 返回布尔值的表达式
     * @return 表示"或"关系的Or对象
     */
    Or or(ExpressionBoolean expBool);
}
