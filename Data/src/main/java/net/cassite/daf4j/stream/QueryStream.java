package net.cassite.daf4j.stream;

import net.cassite.daf4j.*;

import java.util.List;

/**
 * QueryStream查询.<br>
 * 用于查询实体序列
 *
 * @param <E> 实体类型
 */
public class QueryStream<E> extends QueryStreamBase<E, QueryStream<E>> {
        public QueryStream(E entity, DataAccess dataAccess) {
                super(entity, dataAccess);
        }

        public List<E> list() {
                return dataAccess.list(entity, andOr, parameter);
        }
}
