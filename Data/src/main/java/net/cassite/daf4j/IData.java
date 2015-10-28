package net.cassite.daf4j;

import net.cassite.daf4j.util.Selectable;

/**
 * 表示实体中的字段
 *
 * @param <T> 字段类型
 */
public interface IData<T> extends Selectable {
        /**
         * 获取该字段存储的值
         *
         * @return 存储的值
         */
        T get();

        /**
         * 获取该字段所在实体
         *
         * @return 该字段所在实体
         */
        Object getEntity();

        /**
         * 进行排序操作时使用降序
         *
         * @return 排序依据
         */
        OrderBase desc();

        /**
         * 进行排序操作时使用升序
         *
         * @return 排序依据
         */
        OrderBase asc();
}
