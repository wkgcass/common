package net.cassite.daf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PreResult用于规定返回结果的形式/执行的功能
 *
 * @param <En> 目标实体的类型
 */
public class PreResult<En> {
        final DataAccess dataAccess;
        public final En entity;
        public final Where whereClause;
        QueryParameter parameter = null;

        PreResult(DataAccess dataAccess, En entity, Where whereClause) {
                this.dataAccess = dataAccess;
                this.entity = entity;
                this.whereClause = whereClause;
        }

        /**
         * 设置查询参数
         *
         * @param parameter 查询参数
         * @return 该PreResult对象本身
         */
        public PreResult<En> param(QueryParameter parameter) {
                this.parameter = parameter;
                return this;
        }

        /**
         * 执行查询.
         *
         * @return 查询结果List[实体]
         * @see DataAccess#list(Object, Where, QueryParameter)
         */
        public List<En> list() {
                return dataAccess.list(entity, whereClause, parameter);
        }

        /**
         * 获取第一个查询结果
         *
         * @return 第一个查询结果
         */
        public En first() {
                if (parameter == null) {
                        param(new QueryParameter());
                }
                parameter.top(1);
                List<En> list = dataAccess.list(entity, whereClause, parameter);
                if (list == null || list.isEmpty()) return null;
                return list.get(0);
        }

        /**
         * 查询所有非聚合字段,并进行映射
         *
         * @return 查询结果List[Map{字段名,值}]
         * @see DataAccess#projection(Object, Where, QueryParameterWithFocus)
         */
        public List<Map<String, Object>> selectAll() {
                return select(null);
        }

        /**
         * 查询部分字段.若没有设置查询参数 或 没有指定字段 ,则视为查询所有字段
         *
         * @param focus 需要映射的(非聚合)字段
         * @return 查询结果List[Map{字段名,值}]
         * @see DataAccess#projection(Object, Where, QueryParameterWithFocus)
         */
        public List<Map<String, Object>> select(Focus focus) {
                return dataAccess.projection(entity, whereClause, new QueryParameterWithFocus(parameter, focus));
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
                                        IDataAssignable<?> data = (IDataAssignable<?>) o;
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
         * 执行计数
         *
         * @return 计数结果(long)
         */
        public long count() {
                String alias = "count";
                List<Map<String, Object>> res = dataAccess.projection(entity, whereClause, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.count(entity), alias)));
                return (res == null || res.size() == 0) ? 0L : Long.parseLong(res.get(0).get(alias).toString());
        }

        @Override
        public String toString() {
                return "from " + entity.getClass().getSimpleName() + " where " + whereClause + " with param " + parameter;
        }
}
