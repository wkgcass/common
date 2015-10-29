package net.cassite.daf4j.stream;

import net.cassite.daf4j.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 包含整型的结果
 *
 * @param <E>
 */
public class QueryLongStream<E> extends QueryStreamBase<E, QueryLongStream<E>> implements Iterable<Long> {
        private final DataComparable<Long> longData;

        protected QueryLongStream(E entity, DataAccess dataAccess, AndOr andOr, QueryParameter parameter, DataComparable<Long> longData) {
                super(entity, dataAccess);
                this.andOr = andOr;
                this.parameter = parameter;
                this.longData = longData;
        }

        public long sum() {
                return DataUtils.executeSumLong(entity, andOr, parameter, longData, dataAccess);
        }

        public double average() {
                return DataUtils.executeAvg(entity, andOr, parameter, longData, dataAccess);
        }

        public long max() {
                return DataUtils.executeMaxLong(entity, andOr, parameter, longData, dataAccess);
        }

        public long min() {
                return DataUtils.executeMinLong(entity, andOr, parameter, longData, dataAccess);
        }

        @Override
        public QueryLongStream<E> sorted() {
                try {
                        return super.sorted();
                } catch (UnsupportedOperationException e) {
                        return sorted(longData.asc());
                }
        }

        @Override
        public Iterator<Long> iterator() {
                String alias = "resultData";
                List<Map<String, Object>> list = dataAccess.projection(entity, andOr, new QueryParameterWithFocus(parameter, new Focus().focus(longData, alias)));
                return new It<Long>(list, alias);
        }
}
