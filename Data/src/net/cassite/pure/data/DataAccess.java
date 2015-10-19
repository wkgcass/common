package net.cassite.pure.data;

import java.util.List;
import java.util.Map;

/**
 * Pure.Data操作的实际执行接口.<br>
 * 从以下类的文档中查看调用信息
 *
 * @author wkgcass
 * @see Query
 * @see PreResult
 */
public interface DataAccess {
    /**
     * 根据主键进行查询
     *
     * @param entityClass 实体类
     * @param pkValue     主键值
     * @param <En>        实体类型
     * @return 查询结果, 实体
     */
    <En> En find(Class<En> entityClass, Object pkValue);

    /**
     * 执行查询,注入实体并返回结果List
     *
     * @param entity      要查询的实体
     * @param whereClause 查询条件
     * @param parameter   查询参数(可以为空)
     * @param <En>        实体类型
     * @return 查询的实体结果List
     */
    <En> List<En> list(En entity, Where whereClause, QueryParameter parameter);

    /**
     * 执行查询,生成List&lt;Map&lt;字段名,对象&gt;&gt;形式的结果
     *
     * @param entity      要查询的实体
     * @param whereClause 查询条件
     * @param parameter   查询参数(可以为空,若为空则查询所有非iterable字段)
     * @return 查询的Map结果
     */
    List<Map<String, Object>> map(Object entity, Where whereClause, QueryParameterWithFocus parameter);

    /**
     * 执行更新
     *
     * @param entity      目标实体
     * @param whereClause 更新条件
     * @param entries     更新内容
     */
    void update(Object entity, Where whereClause, UpdateEntry[] entries);

    /**
     * 执行删除
     *
     * @param entity      目标实体
     * @param whereClause 删除条件
     */
    void remove(Object entity, Where whereClause);

    /**
     * 持久化实体
     *
     * @param entities 要持久化的实体
     */
    void save(Object[] entities);

    /**
     * 执行查询,返回List[实体]
     *
     * @param query     查询语句
     * @param parameter 查询参数
     * @param <E>       实体类型
     * @return [ENTITY]
     */
    <E> List<E> find(Object query, QueryParameter parameter);

    /**
     * 执行查询,返回List[Map{字段名:值}]
     *
     * @param query     查询语句
     * @param parameter 查询参数
     * @return [{字段名:值}]
     */
    List<Map<String, Object>> find(Object query, QueryParameterWithFocus parameter);

    /**
     * 执行语句
     *
     * @param query 要执行的语句
     */
    void execute(Object query);

    /**
     * 创建一个用于执行查询的NamedListQuery并返回
     *
     * @param name        query名称
     * @param entity      查询实体
     * @param whereClause 查询条件
     * @param parameter   查询参数
     * @param <En>        实体类型
     * @return 创建的NamedListQuery
     */
    <En> NamedListQuery<En> makeList(String name, En entity, Where whereClause, QueryParameter parameter);

    /**
     * 创建一个用于执行查询的NamedMapQuery并返回
     *
     * @param name        query名称
     * @param entity      查询实体
     * @param whereClause 查询条件
     * @param parameter   查询参数
     * @return 生成的NamedMapQuery
     */
    NamedMapQuery makeMap(String name, Object entity, Where whereClause, QueryParameterWithFocus parameter);

    /**
     * 创建一个用于更新的NamedUpdateQuery并返回
     *
     * @param name        query名称
     * @param entity      更新实体
     * @param whereClause 更新条件
     * @param entries     更新参数
     * @return 生成的NamedUpdateQuery
     */
    NamedUpdateQuery makeUpdate(String name, Object entity, Where whereClause, UpdateEntry[] entries);

    /**
     * 创建一个用于删除的NamedUpdateQuery并返回
     *
     * @param name        query名称
     * @param entity      删除实体
     * @param whereClause 删除条件
     * @return 生成的NamedUpdateQuery
     */
    NamedUpdateQuery makeDelete(String name, Object entity, Where whereClause);

    /**
     * 执行NamedListQuery,返回查询结果
     *
     * @param query 查询
     * @param <En>  实体类型
     * @return 查询结果
     * @throws IllegalArgumentException 当给出query不是本DataAccess可支持的类型时抛出
     */
    <En> List<En> runNamedListQuery(NamedListQuery<En> query) throws IllegalArgumentException;

    /**
     * 执行NamedMapQuery,返回查询结果
     *
     * @param query 查询
     * @return 查询结果
     * @throws IllegalArgumentException 当给出query不是本DataAccess可支持的类型时抛出
     */
    List<Map<String, Object>> runNamedMapQuery(NamedMapQuery query) throws IllegalArgumentException;

    /**
     * 执行NamedUpdateQuery
     *
     * @param query 要执行的query
     * @throws IllegalArgumentException 当给出query不是本DataAccess可支持的类型时抛出
     */
    void runNamedUpdateQuery(NamedUpdateQuery query) throws IllegalArgumentException;
}
