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
public class QueryIntStream<E> extends QueryStreamBase<E, QueryIntStream<E>> implements Iterable<Integer> {
        private final DataComparable<Integer> intData;

        protected QueryIntStream(E entity, DataAccess dataAccess, AndOr andOr, QueryParameter parameter, DataComparable<Integer> intData) {
                super(entity, dataAccess);
                this.andOr = andOr;
                this.parameter = parameter;
                this.intData = intData;
        }

        public long sum() {
                return DataUtils.executeSumLong(entity, andOr, parameter, intData, dataAccess);
        }

        public double average() {
                return DataUtils.executeAvg(entity, andOr, parameter, intData, dataAccess);
        }

        public int max() {
                return DataUtils.executeMaxInt(entity, andOr, parameter, intData, dataAccess);
        }

        public int min() {
                return DataUtils.executeMinInt(entity, andOr, parameter, intData, dataAccess);
        }

        @Override
        public QueryIntStream<E> sorted() {
                try {
                        return super.sorted();
                } catch (UnsupportedOperationException e) {
                        return sorted(intData.asc());
                }
        }


        @Override
        public Iterator<Integer> iterator() {
                String alias = "resultData";
                List<Map<String, Object>> list = dataAccess.projection(entity, andOr, new QueryParameterWithFocus(parameter, new Focus().focus(intData, alias)));
                return new It<Integer>(list, alias);
        }
}
