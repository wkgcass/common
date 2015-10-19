package net.cassite.pure.data;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Pure.Data 查询.<br>
 * 将查询封装为对象,通过方法调用和传参进行增删查改
 */
public class Query {
    private final DataAccess dataAccess;

    public Query(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @SuppressWarnings("unchecked")
    public <En> From<En> from(En entity) {
        return new From<En>(entity, dataAccess);
    }

    /**
     * 持久化实体
     *
     * @param entities 要持久化的实体
     * @see DataAccess#save(Object[])
     */
    public void save(Object... entities) {
        dataAccess.save(entities);
    }

    /**
     * 查询实体
     *
     * @param query 查询语句
     * @param <E>   实体类型
     * @return List[实体]
     * @see DataAccess#find(Object, QueryParameter)
     */
    public <E> List<E> find(Object query) {
        return find(query, null);
    }

    /**
     * 查询实体
     *
     * @param query     查询语句
     * @param parameter 查询参数
     * @param <E>       实体类型
     * @return List[实体]
     * @see DataAccess#find(Object, QueryParameter)
     */
    public <E> List<E> find(Object query, QueryParameter parameter) {
        return dataAccess.find(query, parameter);
    }

    /**
     * 查询部分字段
     *
     * @param query 查询语句
     * @return List[Map{字段名,值}]
     * @see DataAccess#find(Object, QueryParameterWithFocus)
     */
    public List<Map<String, Object>> findMap(Object query) {
        return findMap(query, null);
    }

    /**
     * 查询部分字段
     *
     * @param query     查询语句
     * @param parameter 查询参数
     * @return List[Map{字段名,值}]
     * @see DataAccess#find(Object, QueryParameterWithFocus)
     */
    public List<Map<String, Object>> findMap(Object query, QueryParameterWithFocus parameter) {
        return dataAccess.find(query, parameter);
    }

    /**
     * 执行语句
     *
     * @param query 语句
     * @see DataAccess#execute(Object)
     */
    public void execute(Object query) {
        dataAccess.execute(query);
    }

    /**
     * 根据主键查询实体
     *
     * @param entityClass 实体类
     * @param pkValue     主键值
     * @param <En>        实体类型
     * @return 查询结果, 实体
     * @see DataAccess#find(Class, Object)
     */
    public <En> En find(Class<En> entityClass, Object pkValue) {
        return dataAccess.find(entityClass, pkValue);
    }

    /**
     * 在此Query的DataAccess上执行NamedListQuery
     *
     * @param query 要执行的NamedListQuery
     * @param <En>  返回实体类型
     * @return List[实体]
     * @see DataAccess#runNamedListQuery(NamedListQuery)
     */
    public <En> List<En> execute(NamedListQuery<En> query) {
        return dataAccess.runNamedListQuery(query);
    }

    /**
     * 在此Query的DataAccess上执行NamedMapQuery
     *
     * @param query 要执行的NamedMapQuery
     * @return List[Map{字段名,值}]
     * @see DataAccess#runNamedMapQuery(NamedMapQuery)
     */
    public List<Map<String, Object>> execute(NamedMapQuery query) {
        return dataAccess.runNamedMapQuery(query);
    }

    /**
     * 在此Query的DataAccess上执行NamedUpdateQuery
     *
     * @param query 要执行的NamedUpdateQuery
     * @see DataAccess#runNamedUpdateQuery(NamedUpdateQuery)
     */
    public void execute(NamedUpdateQuery query) {
        dataAccess.runNamedUpdateQuery(query);
    }

}
