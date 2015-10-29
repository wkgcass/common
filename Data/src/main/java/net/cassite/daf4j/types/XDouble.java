package net.cassite.daf4j.types;

import net.cassite.daf4j.DataComparable;

/**
 * 针对Double类型在实体中的简化
 */
public class XDouble extends DataComparable<Double> {
        public XDouble(Object entity) {
                super(entity);
        }

        public XDouble(Double item, Object entity) {
                super(item, entity);
        }
}
