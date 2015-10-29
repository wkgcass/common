package net.cassite.daf4j.types;

import net.cassite.daf4j.Data;

/**
 * 针对String在实体中的简化
 */
public class XString extends Data<String> {
        public XString(Object entity) {
                super(entity);
        }

        public XString(String item, Object entity) {
                super(item, entity);
        }
}
