package net.cassite.pure.data;

import java.util.List;
import java.util.Map;

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
        return dataAccess.list(entityClass, whereClause, null);
    }

    public List<En> list(QueryParameter parameters) {
        return dataAccess.list(entityClass, whereClause, parameters);
    }

    public Map<String, Object> map() {
        return dataAccess.map(entityClass, whereClause, null);
    }

    public Map<String, Object> map(QueryParameter parameters) {
        return dataAccess.map(entityClass, whereClause, parameters);
    }

    public void saveAs(En samples) {
        dataAccess.update(entityClass, whereClause, samples);
    }

    public void remove() {
        dataAccess.remove(entityClass, whereClause);
    }

    public Where getWhereClause() {
        return whereClause;
    }

    @Override
    public String toString() {
        return "from " + entityClass.getSimpleName() + " where " + whereClause;
    }
}
