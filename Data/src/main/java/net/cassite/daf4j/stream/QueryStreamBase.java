package net.cassite.daf4j.stream;

import net.cassite.daf4j.*;

/**
 * Stream式查询基类
 *
 * @param <E> 实体类型
 * @param <T> QueryStream类型
 */
public abstract class QueryStreamBase<E, T extends QueryStreamBase<E, T>> {
        final E entity;
        final DataAccess dataAccess;
        AndOr andOr;
        QueryParameter parameter;

        public QueryStreamBase(E entity, DataAccess dataAccess) {
                this.entity = entity;
                this.dataAccess = dataAccess;
        }

        @SuppressWarnings("unchecked")
        public T filter(AndOr andOr) {
                if (this.andOr == null) {
                        this.andOr = andOr;
                } else {
                        if (andOr instanceof And) {
                                this.andOr.and((And) andOr);
                        } else if (andOr instanceof Or) {
                                this.andOr.and((Or) andOr);
                        } else if (andOr instanceof Condition) {
                                this.andOr.and((Condition) andOr);
                        } else if (andOr instanceof ExpressionBoolean) {
                                this.andOr.and((ExpressionBoolean) andOr);
                        }
                }
                return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T sort(OrderBase... order) {
                if (parameter == null) {
                        parameter = new QueryParameter();
                }
                parameter.orderBy(order);
                return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T limit(int count) {
                if (parameter == null) {
                        parameter = new QueryParameter();
                }
                parameter.top(count);
                return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T limit(int start, int end) {
                if (parameter == null) {
                        parameter = new QueryParameter();
                }
                parameter.limit(start, end);
                return (T) this;
        }

        public QueryProjectionStream<E> mapAll() {
                return map(null);
        }

        public QueryProjectionStream<E> map(Focus focus) {
                QueryParameterWithFocus p = new QueryParameterWithFocus(parameter, focus);
                return new QueryProjectionStream<E>(entity, dataAccess, andOr, p);
        }
}
