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
public class QueryDoubleStream<E> extends QueryStreamBase<E, QueryDoubleStream<E>> implements Iterable<Double> {
        private final DataComparable<Double> doubleData;

        protected QueryDoubleStream(E entity, DataAccess dataAccess, AndOr andOr, QueryParameter parameter, DataComparable<Double> doubleData) {
                super(entity, dataAccess);
                this.andOr = andOr;
                this.parameter = parameter;
                this.doubleData = doubleData;
        }

        public double sum() {
                return DataUtils.executeSumDouble(entity, andOr, parameter, doubleData, dataAccess);
        }

        public double average() {
                return DataUtils.executeAvg(entity, andOr, parameter, doubleData, dataAccess);
        }

        public double max() {
                return DataUtils.executeMaxDouble(entity, andOr, parameter, doubleData, dataAccess);
        }

        public double min() {
                return DataUtils.executeMinDouble(entity, andOr, parameter, doubleData, dataAccess);
        }

        @Override
        public QueryDoubleStream<E> sorted() {
                try {
                        return super.sorted();
                } catch (UnsupportedOperationException e) {
                        return sorted(doubleData.asc());
                }
        }


        @Override
        public Iterator<Double> iterator() {
                String alias = "resultData";
                List<Map<String, Object>> list = dataAccess.projection(entity, andOr, new QueryParameterWithFocus(parameter, new Focus().focus(doubleData, alias)));
                return new It<Double>(list, alias);
        }
}
