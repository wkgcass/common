package net.cassite.pure.data;

import java.util.List;
import java.util.Map;

public interface DataAccess {
    <En> List<En> list(Class<En> entityClass, Where whereClause, QueryParameter parameter);

    List<Map<String, Object>> map(Class<?> entityClass, Where whereClause, QueryParameterWithFocus parameter);

    <En> void update(Class<En> entityClass, Where whereClause, UpdateEntry[] toUpdate);

    void remove(Class<?> entityClass, Where whereClause);

    void save(Object[] entity);

    <E, T extends Iterable<E>> T find(Class<E> cls, String query);

    void execute(String query);
}
