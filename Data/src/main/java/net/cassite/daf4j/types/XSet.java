package net.cassite.daf4j.types;

import net.cassite.daf4j.DataIterable;

import java.util.*;

/**
 * 针对Set在实体中的简化
 */
public class XSet<E> extends DataIterable<E, Set<E>> {
        /**
         * 使用LinkedHashSet初始化该数据项
         *
         * @param entity 该数据项所在的实体,使用(this)填入该参数
         */
        public XSet(Object entity) {
                this(new LinkedHashSet<E>(), entity);
        }

        /**
         * 将该数据项初始化为指定值
         *
         * @param it     指定的初始化值
         * @param entity 该数据项所在的实体,使用(this)填入该参数
         */
        public XSet(Set<E> it, Object entity) {
                super(it, entity);
        }
}
