package net.cassite.pure.data.util;

import net.cassite.pure.data.ExpressionType;
import net.cassite.pure.data.IData;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by wkgcass on 15/10/11.
 */
public class DataUtils {
    private DataUtils() {
    }

    public static String dataToStringUtil(IData<?> data) {
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

    public static String expToStringUtil(ExpressionType expType, Object[] parameters) {
        return expType + Arrays.toString(parameters);
    }

    public static String findFieldNameByIData(IData<?> data) {
        try {
            Field[] fields = data.getEntity().getClass().getFields();
            for (Field f : fields) {
                if (data == f.get(data.getEntity())) {
                    return f.getName();
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("This object(" + data + ") is not a field of entity " + data.getEntity());
    }
}
