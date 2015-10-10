package net.cassite.pure.data;

import java.util.List;

public class PreResult<En> {
        private final DataAccess dataAccess;
        private final Class<En> entityClass;
        private final Where whereClause;

        PreResult(DataAccess dataAccess, Class<En> entityClass, Where whereClause) {
                this.dataAccess = dataAccess;
                this.entityClass = entityClass;
                this.whereClause = whereClause;
        }

        public List<En> list() {
                return dataAccess.list(entityClass, whereClause);
        }

        public List<En> list(QueryParameter parameters) {
                return dataAccess.list(entityClass, whereClause, parameters);
        }
}
