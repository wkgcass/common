package net.cassite.slot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.cassite.slot.exceptions.Result;

public class Input<InputType> {
        private final Object target;
        private final Method toInvoke;

        Input(Object target, Method toInvoke, Class<InputType> type) {
                this.target = target;
                this.toInvoke = toInvoke;
        }

        public void send(InputType o) {
                try {
                        toInvoke.invoke(target, new Object[] { o });
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
