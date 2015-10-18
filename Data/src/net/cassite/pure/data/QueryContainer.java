package net.cassite.pure.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/18.
 */
public class QueryContainer {
    private QueryContainer() {
    }

    private static Map<String, NamedListQuery<?>> namedList = new HashMap<String, NamedListQuery<?>>();
    private static Map<String, NamedMapQuery> namedMap = new HashMap<String, NamedMapQuery>();
    private static Map<String, NamedUpdateQuery> namedUpdate = new HashMap<String, NamedUpdateQuery>();

    static void register(String name, NamedListQuery<?> named) {
        namedList.put(name, named);
    }

    static void register(String name, NamedMapQuery named) {
        namedMap.put(name, named);
    }

    static void register(String name, NamedUpdateQuery named) {
        namedUpdate.put(name, named);
    }

    @SuppressWarnings("unchecked")
    public static <T> NamedListQuery<T> getNamedListQuery(String name) {
        return (NamedListQuery<T>) namedList.get(name);
    }

    public static NamedMapQuery getNamedMapQuery(String name) {
        return namedMap.get(name);
    }

    public static NamedUpdateQuery getNamedUpdateQuery(String name) {
        return namedUpdate.get(name);
    }
}
