package net.cassite.pure.data;

import java.lang.reflect.Field;

/**
 * Created by wkgcass on 15/10/11.
 */
public class DataUtils {
    private DataUtils() {
    }

    public static String toStringUtil(IData<?> data) {
        try {
            Field[] fields = data.getEntity().getClass().getFields();
            for (Field f : fields) {
                if (data == f.get(data.getEntity())) {
                    return f.getDeclaringClass().getSimpleName() + "." + f.getName();
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("This object(" + data + ") is not a field of entity " + data.getEntity());
    }
}
