package net.cassite.pure.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PreResult用于规定返回结果的形式/执行的功能
 *
 * @param <En> 目标实体的类型
 */
public class PreResult<En> {
    private final DataAccess dataAccess;
    public final En entity;
    public final Where whereClause;

    PreResult(DataAccess dataAccess, En entity, Where whereClause) {
        this.dataAccess = dataAccess;
        this.entity = entity;
        this.whereClause = whereClause;
    }

    /**
     * 执行查询
     *
     * @return 查询结果List[实体]
     * @see DataAccess#list(Object, Where, QueryParameter)
     */
    public List<En> list() {
        return list(null);
    }

    /**
     * 执行查询
     *
     * @param parameters 查询参数
     * @return 查询结果List[实体]
     * @see DataAccess#list(Object, Where, QueryParameter)
     */
    public List<En> list(QueryParameter parameters) {
        return dataAccess.list(entity, whereClause, parameters);
    }

    /**
     * 获取第一个查询结果
     *
     * @return 第一个查询结果
     */
    public En first() {
        List<En> list = dataAccess.list(entity, whereClause, new QueryParameter().top(1));
        if (list == null || list.isEmpty()) return null;
        return list.get(0);
    }

    /**
     * 查询部分字段
     *
     * @return 查询结果List[Map{字段名,值}]
     * @see DataAccess#map(Object, Where, QueryParameterWithFocus)
     */
    public List<Map<String, Object>> map() {
        return map(null);
    }

    /**
     * 查询部分字段
     *
     * @param parameters 查询参数
     * @return 查询结果List[Map{字段名,值}]
     * @see DataAccess#map(Object, Where, QueryParameterWithFocus)
     */
    public List<Map<String, Object>> map(QueryParameterWithFocus parameters) {
        return dataAccess.map(entity, whereClause, parameters);
    }

    /**
     * 将匹配的实体更新为样本
     *
     * @param samples 实体样本
     */
    public void saveAs(En samples) {
        List<UpdateEntry> tmpList = new ArrayList<UpdateEntry>();
        try {
            for (Field f : samples.getClass().getFields()) {
                Object o = f.get(samples);
                if (o instanceof IDataAssignable) {
                    IDataAssignable data = (IDataAssignable) o;
                    tmpList.add(data.as(data.get()));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        dataAccess.update(entity, whereClause, tmpList.toArray(new UpdateEntry[tmpList.size()]));
    }

    /**
     * 执行更新
     *
     * @param entries 更新内容
     * @see DataAccess#update(Object, Where, UpdateEntry[])
     */
    public void set(UpdateEntry... entries) {
        dataAccess.update(entity, whereClause, entries);
    }

    /**
     * 执行删除
     *
     * @see DataAccess#remove(Object, Where)
     */
    public void remove() {
        dataAccess.remove(entity, whereClause);
    }

    /**
     * 生成用于查询的NamedListQuery
     *
     * @param name      定义query的名称
     * @param parameter 查询参数
     * @return 生成的Query
     * @see DataAccess#makeList(String, Object, Where, QueryParameter)
     */
    public NamedListQuery<En> makeList(String name, QueryParameter parameter) {
        return dataAccess.makeList(name, entity, whereClause, parameter);
    }

    /**
     * 生成用于查询的NamedMapQuery
     *
     * @param name      定义query的名称
     * @param parameter 查询参数
     * @return 生成的Query
     * @see DataAccess#makeList(String, Object, Where, QueryParameter)
     */
    public NamedMapQuery makeMap(String name, QueryParameterWithFocus parameter) {
        return dataAccess.makeMap(name, entity, whereClause, parameter);
    }

    /**
     * 生成用于更新的NamedUpdateQuery
     *
     * @param name    定义query的名称
     * @param entires 更新内容
     * @return 生成的Query
     * @see DataAccess#makeUpdate(String, Object, Where, UpdateEntry[])
     */
    public NamedUpdateQuery makeUpdate(String name, UpdateEntry... entires) {
        return dataAccess.makeUpdate(name, entity, whereClause, entires);
    }

    /**
     * 生成用于删除的NamedUpdateQuery
     *
     * @param name 定义query的名称
     * @return 生成的Query
     * @see DataAccess#makeDelete(String, Object, Where)
     */
    public NamedUpdateQuery makeDelete(String name) {
        return dataAccess.makeDelete(name, entity, whereClause);
    }

    @Override
    public String toString() {
        return "from " + entity.getClass().getSimpleName() + " where " + whereClause;
    }
}
