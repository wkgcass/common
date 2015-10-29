package net.cassite.daf4j.types;

import net.cassite.daf4j.DataComparable;

/**
 * 针对整型在实体中的简化
 */
public class XInt extends DataComparable<Integer> {
        public XInt(Object entity) {
                super(entity);
        }

        public XInt(Integer item, Object entity) {
                super(item, entity);
        }
}
