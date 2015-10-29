package net.cassite.daf4j.types;

import net.cassite.daf4j.DataIterable;

import java.util.ArrayList;
import java.util.List;

/**
 * 针对List在实体中的简化
 */
public class XList<E> extends DataIterable<E, List<E>> {
        /**
         * 使用ArrayList初始化该数据项
         *
         * @param entity 该数据项所在的实体,使用(this)填入该参数
         */
        public XList(Object entity) {
                this(new ArrayList<E>(), entity);
        }

        /**
         * 将该数据项初始化为指定值
         *
         * @param it     指定的初始化值
         * @param entity 该数据线所在的实体,使用(this)填入该参数
         */
        public XList(List<E> it, Object entity) {
                super(it, entity);
        }
}
