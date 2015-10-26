package net.cassite.datafacade.util;

import java.util.LinkedHashMap;

/**
 * 别名映射.自动生成唯一的别名.别名形如"$prefix$aliasCount".其中prefix在构造时指定,aliasCount是一个自增的整型.
 */
public class AliasMap extends LinkedHashMap<Location, String> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6275755012252632698L;
	private final String prefix;
    private int aliasCount;

    /**
     * 创建一个AliasMap并指定前缀,aliasCount被初始化为0
     *
     * @param prefix 用于生成别名的前缀
     */
    public AliasMap(String prefix) {
        this(prefix, 0);
    }

    /**
     * 创建一个AliasMap并指定前缀和aliasCount的初始值
     *
     * @param prefix 用于生成别名的前缀
     * @param count  别名计数初始值
     */
    public AliasMap(String prefix, int count) {
        this.prefix = prefix;
        this.aliasCount = count;
    }

    /**
     * 向该映射中加入位置信息
     *
     * @param location 位置
     * @return 该AliasMap本身
     */
    public AliasMap add(Location location) {
        put(location, prefix + (++aliasCount));
        return this;
    }

    @Override
    public String get(Object o) {
        if (o instanceof Location) {
            if (containsKey(o)) return super.get(o);
            add((Location) o);
            return super.get(o);
        } else {
            return null;
        }
    }

    /**
     * 返回当前别名计数
     *
     * @return 当前别名计数值
     */
    public int getAliasCount() {
        return aliasCount;
    }

    /**
     * 设置别名计数值
     *
     * @param count 要设置的计数值
     */
    public void setAliasCount(int count) {
        this.aliasCount = count;
    }
}
