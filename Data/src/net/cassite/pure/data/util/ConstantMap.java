package net.cassite.pure.data.util;

import java.util.LinkedHashMap;

/**
 * 常量映射,将自动生成(自增的整型)到(常量值)的映射
 */
public class ConstantMap extends LinkedHashMap<Integer, Object> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -160616718543989584L;
	private int count = 0;

    /**
     * 向映射中增加常量值
     *
     * @param o 要加入的常量值
     * @return 该常量值对应的整型
     */
    public int add(Object o) {
        put(++count, o);
        return count;
    }
}
