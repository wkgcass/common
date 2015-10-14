package net.cassite.pure.data;

import java.util.List;
import java.util.Map;

public interface DataAccess {
    <En> List<En> list(En entityClass, Where whereClause, QueryParameter parameter);

    <En> List<Map<String, Object>> map(En entity, Where whereClause, QueryParameterWithFocus parameter);

    <En> void update(En entity, Where whereClause, UpdateEntry[] toUpdate);

    <En> void remove(En entity, Where whereClause);

    <En> void save(En[] entity);

    <E, T extends Iterable<E>> T find(Class<E> cls, String query, QueryParameter parameter);

    void execute(String query);
}
