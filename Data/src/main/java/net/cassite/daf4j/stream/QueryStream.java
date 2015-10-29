package net.cassite.daf4j.stream;

import net.cassite.daf4j.*;

import java.util.Iterator;
import java.util.List;

/**
 * QueryStream查询.<br>
 * 用于查询实体序列
 *
 * @param <E> 实体类型
 */
public class QueryStream<E> extends QueryStreamBase<E, QueryStream<E>> implements Iterable<E> {
        public QueryStream(E entity, DataAccess dataAccess) {
                super(entity, dataAccess);
        }

        /**
         * 将结果转换为List[实体]
         *
         * @return List类型的结果
         */
        public List<E> list() {
                return dataAccess.list(entity, andOr, parameter);
        }

        /**
         * 执行计数
         *
         * @return long类型的计数结果
         */
        public long count() {
                return DataUtils.executeCount(entity, andOr, parameter, dataAccess);
        }

        /**
         * 映射所有非集合字段
         *
         * @return 映射后的stream
         */
        public QueryProjectionStream<E> mapAll() {
                return map((Focus) null);
        }

        /**
         * 映射指定字段/表达式
         *
         * @param focus 要映射的字段/表达式
         * @return 映射后的stream
         */
        public QueryProjectionStream<E> map(Focus focus) {
                QueryParameterWithFocus p = new QueryParameterWithFocus(parameter, focus);
                return new QueryProjectionStream<E>(entity, dataAccess, andOr, p);
        }

        /**
         * 映射到整型的stream
         *
         * @param data 表示整型的字段
         * @return 映射后的stream
         */
        public QueryIntStream<E> mapToInt(DataComparable<Integer> data) {
                return new QueryIntStream<E>(entity, dataAccess, andOr, parameter, data);
        }

        /**
         * 映射到Long的stream
         *
         * @param data 表示Long的字段
         * @return 映射后的stream
         */
        public QueryLongStream<E> mapToLong(DataComparable<Long> data) {
                return new QueryLongStream<E>(entity, dataAccess, andOr, parameter, data);
        }

        /**
         * 映射到Double的stream
         *
         * @param data 表示Double的字段
         * @return 映射后的stream
         */
        public QueryDoubleStream<E> mapToDouble(DataComparable<Double> data) {
                return new QueryDoubleStream<E>(entity, dataAccess, andOr, parameter, data);
        }

        @Override
        public Iterator<E> iterator() {
                return list().iterator();
        }
}
