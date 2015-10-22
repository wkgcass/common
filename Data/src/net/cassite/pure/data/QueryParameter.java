package net.cassite.pure.data;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询参数
 */
public class QueryParameter {
    /**
     * 参数列表
     */
    public Map<QueryParameterTypes, Object[]> parameters = new HashMap<QueryParameterTypes, Object[]>();

    public QueryParameter() {
    }

    /**
     * 分页,取前i条记录
     *
     * @param i 前i条记录
     * @return 链式, 对象本身
     */
    public QueryParameter top(int i) {
        parameters.put(QueryParameterTypes.top, new Object[]{i});
        return this;
    }

    /**
     * 分页,取start到end的记录
     *
     * @param start 起始
     * @param end   结束
     * @return 链式, 对象本身
     */
    public QueryParameter limit(int start, int end) {
        parameters.put(QueryParameterTypes.limit, new Object[]{start, end});
        return this;
    }

    /**
     * 排序依据
     *
     * @param dataArr 排序依据
     * @return 链式, 对象本身
     */
    public QueryParameter orderBy(OrderBase... dataArr) {
        parameters.put(QueryParameterTypes.orderBy, dataArr);
        return this;
    }
}
