package net.cassite.pure.data;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class Query {
    private final DataAccess dataAccess;

    public Query(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @SuppressWarnings("unchecked")
    public <En> From<En> from(En entity) {
        return new From<En>(entity, dataAccess);
    }

    public void save(Object... entities) {
        dataAccess.save(entities);
    }

    public <E> List<E> find(String query) {
        return dataAccess.find(query, null);
    }

    public <E> List<E> find(String query, QueryParameter parameter) {
        return dataAccess.find(query, parameter);
    }

    public void execute(String query) {
        dataAccess.execute(query);
    }

    public <En> En find(Class<En> entityClass, Object pkValue) {
        return dataAccess.find(entityClass, pkValue);
    }
}
