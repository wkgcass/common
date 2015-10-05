package net.cassite.binder;

import java.util.List;
import java.util.Map;

public interface Binding {
        void set(Map<String, Object> $set);

        void unset(Map<String, Integer> $unset);

        void pushAll(Map<String, List<Object>> $pushAll);

        void pullAll(Map<String, List<Object>> $pullAll);
}
