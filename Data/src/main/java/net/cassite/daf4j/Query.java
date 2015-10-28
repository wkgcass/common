package net.cassite.daf4j;

import net.cassite.daf4j.stream.QueryStream;

import java.util.List;
import java.util.Map;

/**
 * Pure.Data 查询.<br>
 * 将查询封装为对象,通过方法调用和传参进行增删查改
 */
public class Query {
        final DataAccess dataAccess;

        public Query(DataAccess dataAccess) {
                this.dataAccess = dataAccess;
        }

        public <En> From<En> from(En entity) {
                return new From<En>(entity, dataAccess);
        }

        public <En> QueryStream<En> stream(En entity) {
                return new QueryStream<En>(entity, dataAccess);
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
}
