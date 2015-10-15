package net.cassite.pure.data.util;

import net.cassite.pure.data.IData;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

/**
 * Created by wkgcass on 15/10/13.
 */
public class AliasMap extends HashMap<Field, String> {
    private final String prefix;
    private int aliasCount = 0;

    public AliasMap(String prefix) {
        this.prefix = prefix;
    }

    public AliasMap(String prefix, int count) {
        this.prefix = prefix;
        this.aliasCount = count;
    }

    @Override
    public String get(Object o) {
        if (o instanceof Field) {
            if (containsKey(o)) return super.get(o);
            put((Field) o, ((Class<?>) ((ParameterizedType) ((Field) o).getGenericType()).getActualTypeArguments()[0]).getSimpleName().substring(0, 1).toLowerCase() + "_" + prefix + (++aliasCount));
            return super.get(o);
        } else {
            return null;
        }
    }

    public int getAliasCount() {
        return aliasCount;
    }

    public void setAliasCount(int count) {
        this.aliasCount = count;
    }
}
