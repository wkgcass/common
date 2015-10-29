package net.cassite.daf4j.types;

import net.cassite.daf4j.DataComparable;

import java.util.Date;

/**
 * 针对Date类型在实体中的简化
 */
public class XDate extends DataComparable<Date> {
        public XDate(Object entity) {
                super(entity);
        }

        public XDate(Date item, Object entity) {
                super(item, entity);
        }
}
