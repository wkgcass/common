package net.cassite.pure.data;

import java.util.List;
import java.util.Map;

public interface DataAccess {
    <En> List<En> list(Class<En> entityClass, Where whereClause, QueryParameter parameter);

    Map<String, Object> map(Class<?> entityClass, Where whereClause, QueryParameter parameter);

    <En> void update(Class<En> entityClass, Where whereClause, En samples);

    void remove(Class<?> entityClass, Where whereClause);

    void save(Object[] entity);

    <E, T extends Iterable<E>> T find(Class<E> cls, String query);

    void execute(String query);
}
