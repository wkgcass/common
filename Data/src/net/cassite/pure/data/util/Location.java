package net.cassite.pure.data.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 位置信息
 */
public class Location {
    private final List<String> location;

    /**
     * 通过通过List&lt;String&gt;定义一个位置<br>
     * 位置根据list中元素的先后确定<br>
     * 例如,[A,B,C]则定义了一个A(B(C))的位置<br>
     * 传入的List不会直接使用,而是先进行复制
     *
     * @param location List定义的位置,若为null则将初始化为空表
     */
    public Location(List<String> location) {
        if (location == null) this.location = new ArrayList<String>();
        else this.location = new ArrayList<String>(location);
    }

    /**
     * 获取位置定义List
     *
     * @return 位置定义List.该List不会直接返回, 而是先进行拷贝
     */
    public List<String> getLocation() {
        return new ArrayList<String>(location);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Location) {
            if (this.location.size() == ((Location) o).location.size()) {
                for (int i = 0; i < location.size(); ++i) {
                    if (!this.location.get(i).equals(((Location) o).location.get(i))) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int r = 0;
        for (String s : location) {
            r += s.hashCode();
        }
        return r;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String loc : location) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(".");
            }
            sb.append(loc);
        }
        return sb.toString();
    }
}
