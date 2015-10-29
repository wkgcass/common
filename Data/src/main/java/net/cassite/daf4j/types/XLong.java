package net.cassite.daf4j.types;

import net.cassite.daf4j.DataComparable;

/**
 * 针对Long类型在实体中的简化
 */
public class XLong extends DataComparable<Long> {
        public XLong(Object entity) {
                super(entity);
        }

        public XLong(Long item, Object entity) {
                super(item, entity);
        }
}
