package net.cassite.daf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/26.
 */
public class DataAccessEmptyImpl implements DataAccess {
        @Override
        public <En> En find(Class<En> entityClass, Object pkValue) {
                return null;
        }

        @Override
        public <En> List<En> list(En entity, Where whereClause, QueryParameter parameter) {
                return null;
        }

        @Override
        public List<Map<String, Object>> projection(Object entity, Where whereClause, QueryParameterWithFocus parameter) {
                return null;
        }

        @Override
        public void update(Object entity, Where whereClause, UpdateEntry[] entries) {

        }

        @Override
        public void remove(Object entity, Where whereClause) {

        }

        @Override
        public void save(Object[] entities) {

        }
}
