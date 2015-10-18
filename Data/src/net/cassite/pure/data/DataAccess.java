package net.cassite.pure.data;

import java.util.List;
import java.util.Map;

public interface DataAccess {
    <En> En find(Class<En> entityClass, Object pkValue);

    <En> List<En> list(En entity, Where whereClause, QueryParameter parameter);

    List<Map<String, Object>> map(Object entity, Where whereClause, QueryParameterWithFocus parameter);

    void update(Object entity, Where whereClause, UpdateEntry[] toUpdate);

    void remove(Object entity, Where whereClause);

    <En> void save(En[] entity);

    <E> List<E> find(String query, QueryParameter parameter);

    <En> NamedListQuery<En> makeList(String name, En entity, Where whereClause, QueryParameter parameter);

    NamedMapQuery makeMap(String name, Object entity, Where whereClause, QueryParameterWithFocus parameter);

    NamedUpdateQuery makeUpdate(String name, Object entity, Where whereClause, UpdateEntry[] toUpdate);

    NamedUpdateQuery makeDelete(String name, Object entity, Where whereClause);

    <En> List<En> runNamedListQuery(NamedListQuery<En> query) throws IllegalArgumentException;

    List<Map<String, Object>> runNamedMapQuery(NamedMapQuery query) throws IllegalArgumentException;

    void runNamedUpdateQuery(NamedUpdateQuery query) throws IllegalArgumentException;

    void execute(String query);
}
