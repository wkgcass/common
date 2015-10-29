package net.cassite.daf4j.stream;

import net.cassite.daf4j.OrderBase;

/**
 * 本身带有排序依据的实体<br>
 * 在stream.sorted()时直接引用该接口的sorted方法
 */
public interface SortedEntity {
        /**
         * 规定排序依据,在stream.sorted()方法调用时将以此方法结果为依据
         *
         * @return 排序依据
         */
        OrderBase[] sorted();
}
