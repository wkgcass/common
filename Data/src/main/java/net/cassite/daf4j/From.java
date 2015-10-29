package net.cassite.daf4j;

import net.cassite.daf4j.stream.QueryStream;

public class From<En> {
        private final En entity;
        private final DataAccess dataAccess;

        From(En entity, DataAccess dataAccess) {
                this.entity = entity;
                this.dataAccess = dataAccess;
        }

        public PreResult<En> where(Where whereClause) {
                return new PreResult<En>(dataAccess, entity, whereClause);
        }

        public QueryStream<En> stream() {
                return new QueryStream<En>(entity, dataAccess);
        }
}
