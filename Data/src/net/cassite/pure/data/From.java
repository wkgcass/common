package net.cassite.pure.data;

public class From<En> {
        private final Class<En> entityClass;
        private final DataAccess dataAccess;

        From(Class<En> cls, DataAccess dataAccess) {
                this.entityClass = cls;
                this.dataAccess = dataAccess;
        }

        public PreResult<En> where(Where whereClause) {
                return new PreResult<En>(dataAccess, entityClass, whereClause);
        }
}
