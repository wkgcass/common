package net.cassite.datafacade;

/**
 * 可赋值的字段
 *
 * @param <T> 字段类型
 */
public interface IDataAssignable<T> extends IData<T> {
    /**
     * 指定更新时将其设置为何值
     *
     * @param o 要设置的值(可以为表达式)
     * @return 更新依据
     */
    UpdateEntry as(Object o);
}
