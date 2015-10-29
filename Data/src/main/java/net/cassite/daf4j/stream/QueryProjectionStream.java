package net.cassite.daf4j.stream;

import net.cassite.daf4j.AndOr;
import net.cassite.daf4j.DataAccess;
import net.cassite.daf4j.QueryParameterWithFocus;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Stream式查询<br>
 * 用于查询{别名:值}序列
 *
 * @param <E> 实体类型
 */
public class QueryProjectionStream<E> extends QueryStreamBase<E, QueryProjectionStream<E>> implements Iterable<Map<String, Object>> {

        QueryProjectionStream(E entity, DataAccess dataAccess, AndOr andOr, QueryParameterWithFocus parameter) {
                super(entity, dataAccess);
                this.andOr = andOr;
                this.parameter = parameter;
        }

        public List<Map<String, Object>> list() {
                return dataAccess.projection(entity, andOr, (QueryParameterWithFocus) parameter);
        }

        @Override
        public Iterator<Map<String, Object>> iterator() {
                return list().iterator();
        }
}
