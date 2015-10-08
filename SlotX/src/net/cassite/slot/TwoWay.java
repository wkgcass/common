package net.cassite.slot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.cassite.slot.exceptions.Result;

public class TwoWay<TwoWayType, ResultType> {
        private final Object target;
        private final Method toInvoke;

        TwoWay(Object target, Method toInvoke, Class<TwoWayType> type, Class<ResultType> resType) {
                this.target = target;
                this.toInvoke = toInvoke;
        }

        @SuppressWarnings("unchecked")
        public ResultType send(TwoWayType o) {
                try {
                        if (toInvoke.getParameterTypes()[1].isPrimitive()) {
                                if (toInvoke.getParameterTypes()[1] == boolean.class) {
                                        return (ResultType) toInvoke.invoke(target, new Object[] { o, false });
                                } else {
                                        return (ResultType) toInvoke.invoke(target, new Object[] { o, 0 });
                                }
                        } else {
                                return (ResultType) toInvoke.invoke(target, new Object[] { o, null });
                        }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        if (e instanceof InvocationTargetException) {
                                if (((InvocationTargetException) e).getTargetException() instanceof Result) {
                                        throw (Result) ((InvocationTargetException) e).getTargetException();
                                }
                        }
                        throw new RuntimeException(e);
                }
        }
}
