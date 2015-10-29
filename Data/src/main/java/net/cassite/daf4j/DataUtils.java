package net.cassite.daf4j;

import net.cassite.daf4j.util.AliasMap;
import net.cassite.daf4j.util.Location;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 工具类
 */
public class DataUtils {
        private DataUtils() {
        }

        /**
         * 将IData转为字符串,首先调用findFieldByIData获取该IData在实体中对应的Field,然后返回(实体简称.Field名称)<br>
         * 用于所有IData子类的toString方法
         *
         * @param data 要转换的IdDta实例
         * @return 实体简称.对应Field名称
         * @see DataUtils#findFieldByIData(IData)
         */
        public static String dataToStringUtil(IData<?> data) {
                Field f = findFieldByIData(data);
                return f.getDeclaringClass().getSimpleName() + "." + f.getName();
        }

        /**
         * 将表达式转换为字符串.返回(表达式类型[参数表...])
         *
         * @param expType    表达式类型
         * @param parameters 参数表
         * @return 表达式类型[参数表...]
         */
        public static String expToStringUtil(ExpressionTypes expType, Object[] parameters) {
                return expType + Arrays.toString(parameters);
        }

        /**
         * 获取IData对应Field的名称<br>
         * 调用findFieldByIData获取Field
         *
         * @param data IData对象
         * @return 对应Field名称
         * @see DataUtils#findFieldByIData(IData)
         */
        public static String findFieldNameByIData(IData<?> data) {
                return findFieldByIData(data).getName();
        }

        /**
         * 获取IData对应的Field
         *
         * @param data IData对象
         * @return 获取到的IData
         * @throws RuntimeException 若没有找到则将抛出异常
         */
        public static Field findFieldByIData(IData<?> data) {
                try {
                        Field[] fields = data.getEntity().getClass().getFields();
                        for (Field f : fields) {
                                if (data == f.get(data.getEntity())) {
                                        return f;
                                }
                        }
                } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                }
                throw new RuntimeException("This object(" + data + ") is not a field of entity " + data.getEntity());
        }

        /**
         * 根据包含实体获取Field<br>
         * 搜索searchIn实体的field,若其Field.get(searchiIn)为IData子类型且.get()==entityToSearch<br>
         * 若其为聚合类型则其中有元素==entityToSearch<br>
         * 则返回这个field
         *
         * @param entityToSearch 要寻找的实体
         * @param searchIn       在此实体中寻找
         * @return 找到的Field
         * @throws RuntimeException 若没有找到则抛出一场
         */
        public static Field findFieldByContainedEntity(Object entityToSearch, Object searchIn) {
                Field[] fields = searchIn.getClass().getFields();
                try {
                        for (Field f : fields) {
                                if (IData.class.isAssignableFrom(f.getType())) {
                                        if (DataIterable.class.isAssignableFrom(f.getType())) {
                                                // iterable
                                                @SuppressWarnings("unchecked")
                                                DataIterable<?, Iterable<?>> datait = (DataIterable<?, Iterable<?>>) f.get(searchIn);

                                                for (Object o : datait.get()) {
                                                        if (o == entityToSearch) {
                                                                return f;
                                                        }
                                                }
                                        } else {
                                                // get
                                                if (entityToSearch == f.get(searchIn)) {
                                                        return f;
                                                }
                                        }
                                }
                        }
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
                throw new RuntimeException("This object(" + entityToSearch + ") is not contained in " + searchIn);
        }

        /**
         * 判断输入的两个值是否相等.用于IExpression子类的equals方法
         *
         * @param a    IExpression对象
         * @param that 要判断是否相等的Object对象
         * @return true则相等, false则不等
         * @see Expression
         * @see ExpressionBoolean
         * @see ExpressionComparable
         */
        public static boolean expressionEquals(IExpression a, Object that) {
                if (that instanceof IExpression) {
                        IExpression b = (IExpression) that;
                        if (a.expType() == b.expType()) {
                                if (a.expArgs().length == b.expArgs().length) {
                                        Object[] arrA = a.expArgs();
                                        Object[] arrB = b.expArgs();
                                        for (int i = 0; i < arrA.length; ++i) {
                                                if (!arrA[i].equals(arrB[i])) {
                                                        return false;
                                                }
                                        }
                                        return true;
                                }
                        }
                }
                return false;
        }

        /**
         * 计算IExpression的hashCode.用于IExpression子类的hashCode方法
         *
         * @param exp IExpression对象
         * @return 计算得到的hashCode
         */
        public static int expressionHashCode(IExpression exp) {
                int r = 0;
                r += exp.expType().hashCode();
                Object[] arr = exp.expArgs();
                if (arr != null) {
                        for (Object o : arr) {
                                r += o.hashCode();
                        }
                }
                return r;
        }

        /**
         * 判断该Expression对应的返回值是否为聚合类型
         *
         * @param exp IExpression对象
         * @return true则为聚合类型, false则不是
         */
        public static boolean expressionIsAggregate(IExpression exp) {
                ExpressionTypes type = exp.expType();
                return (type == ExpressionTypes.avg || ExpressionTypes.count == type || ExpressionTypes.sum == type || ExpressionTypes.max == type || ExpressionTypes.min == type);
        }

        /**
         * 获取Where子句中的聚合类型
         *
         * @param where where子句
         * @return 只包含聚合类型的where子句.若没有找到则返回null
         */
        public static Where getAggregate(Where where) {
                if (where.isAnd()) {
                        And and = (And) where;
                        And toRet = new And();
                        for (Or or : and.getOrList()) {
                                if (getAggregate(or) != null) toRet.and(or);
                        }
                        for (Condition condition : and.getConditionList()) {
                                if (getAggregate(condition) != null) toRet.and(condition);
                        }
                        for (ExpressionBoolean exp : and.getExpBoolList()) {
                                if (getAggregate(exp) != null) toRet.and(exp);
                        }
                        if (toRet.getConditionList().size() + toRet.getExpBoolList().size() + toRet.getOrList().size() != 0) {
                                return toRet;
                        }
                } else if (where.isOr()) {
                        Or or = (Or) where;
                        for (And and : or.getAndList()) {
                                if (getAggregate(and) != null) return where;
                        }
                        for (Condition condition : or.getConditionList()) {
                                if (getAggregate(condition) != null) return where;
                        }
                        for (ExpressionBoolean exp : or.getExpBoolList()) {
                                if (getAggregate(exp) != null) return where;
                        }
                } else if (where.isCondition()) {
                        Condition condition = (Condition) where;
                        if (condition.data instanceof Where && getAggregate((Where) condition.data) != null) return where;
                        for (Object arg : condition.args) {
                                if (arg instanceof Where && getAggregate((Where) arg) != null) return where;
                        }
                } else if (where.isExpression()) {
                        IExpression exp = (IExpression) where;
                        if (expressionIsAggregate(exp)) return where;
                        for (Object o : exp.expArgs()) {
                                if (o instanceof Where) {
                                        if (getAggregate((Where) o) != null) {
                                                return where;
                                        }
                                }
                        }
                }
                return null;
        }

        /**
         * 生成位置并将其写入AliasMap<br>
         * 该方法将对locationList从0到(1--size)取subList,对该subList生成Location并检查aliasMap中是否存在该Location的别名<br>
         * 若存在,则使用该别名代替这一部分List内容.<br>
         * 最终将使用"有别名的最长的subList"对应的别名<br>
         * 后续元素将保持原样
         *
         * @param locationList 位置List
         * @param aliasMap     别名Map
         * @return 生成的Location对象
         */
        public static Location generateLocationAndFillMap(List<String> locationList, AliasMap aliasMap) {
                int count = 0;
                Location aliasLoc = null;
                for (int i = 1; i <= locationList.size(); ++i) {
                        Location l = new Location(locationList.subList(0, i));
                        if (aliasMap.containsKey(l)) {
                                count = i;
                                aliasLoc = l;
                        }
                }
                List<String> toGenerate = aliasLoc == null ? new ArrayList<String>() : aliasLoc.getLocation();
                toGenerate.addAll(locationList.subList(count, locationList.size()));
                Location toAdd = new Location(toGenerate);
                if (!aliasMap.containsKey(toAdd)) {
                        aliasMap.add(toAdd);
                }
                return toAdd;
        }

        /**
         * 设置Data的值<br>
         * 该方法是为了防止直接通过public的字段赋值
         *
         * @param data 要赋值的IData对象
         * @param val  要赋的值
         * @param <T>  存储数据类型
         */
        public static <T> void set(Data<T> data, T val) {
                data.set(val);
        }

        /**
         * 设置Data的值<br>
         * 该方法是为了防止直接通过public的字段赋值
         *
         * @param data 要赋值的IData对象
         * @param val  要赋的值
         * @param <T>  存储数据类型
         */
        public static <T extends Comparable<T>> void set(DataComparable<T> data, T val) {
                data.set(val);
        }

        /**
         * 设置Data的值<br>
         * 该方法是为了防止直接通过public的字段赋值
         *
         * @param data 要赋值的IData对象
         * @param val  要赋的值
         * @param <E>  聚合对象中存储的对象的类型
         * @param <T>  聚合对象的类型
         */
        public static <E, T extends Iterable<E>> void set(DataIterable<E, T> data, T val) {
                data.set(val);
        }

        /**
         * 执行计数<br>
         * 直接将 count(实体) 作为映射依据
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param dataAccess DataAccess实例
         * @return long类型的计数结果
         */
        public static long executeCount(Object entity, Where where, QueryParameter parameter, DataAccess dataAccess) {
                String alias = "countRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.count(entity), alias)));
                return (res == null || res.size() == 0) ? 0L : Long.parseLong(res.get(0).get(alias).toString());
        }

        /**
         * 执行求和
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param data       求和字段
         * @param dataAccess DataAccess实例
         * @return long类型的求和结果
         */
        public static long executeSumLong(Object entity, Where where, QueryParameter parameter, DataComparable<? extends Number> data, DataAccess dataAccess) {
                String alias = "sumRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.sum(data), alias)));
                return (res == null || res.size() == 0) ? 0L : Long.parseLong(res.get(0).get(alias).toString());
        }

        /**
         * 执行求和
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param data       求和字段
         * @param dataAccess DataAccess实例
         * @return double类型的求和结果
         */
        public static double executeSumDouble(Object entity, Where where, QueryParameter parameter, DataComparable<? extends Number> data, DataAccess dataAccess) {
                String alias = "sumRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.sum(data), alias)));
                return (res == null || res.size() == 0) ? 0D : Double.parseDouble(res.get(0).get(alias).toString());
        }

        /**
         * 执行求平均数
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param data       求平均数字段
         * @param dataAccess DataAccess实例
         * @return double类型的平均值结果
         */
        public static double executeAvg(Object entity, Where where, QueryParameter parameter, DataComparable<? extends Number> data, DataAccess dataAccess) {
                String alias = "avgRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.avg(data), alias)));
                return (res == null || res.size() == 0) ? 0D : Double.parseDouble(res.get(0).get(alias).toString());
        }

        /**
         * 执行求最大值
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param data       求最大值字段
         * @param dataAccess DataAccess实例
         * @return int类型的求最大值结果
         */
        public static int executeMaxInt(Object entity, Where where, QueryParameter parameter, DataComparable<? extends Number> data, DataAccess dataAccess) {
                String alias = "maxRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.max(data), alias)));
                return (res == null || res.size() == 0) ? 0 : Integer.parseInt(res.get(0).get(alias).toString());
        }

        /**
         * 执行求最大值
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param data       求最大值字段
         * @param dataAccess DataAccess实例
         * @return long类型的求最大值结果
         */
        public static long executeMaxLong(Object entity, Where where, QueryParameter parameter, DataComparable<? extends Number> data, DataAccess dataAccess) {
                String alias = "maxRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.max(data), alias)));
                return (res == null || res.size() == 0) ? 0L : Long.parseLong(res.get(0).get(alias).toString());
        }

        /**
         * 执行求最大值
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param data       求最大值字段
         * @param dataAccess DataAccess实例
         * @return double类型的求最大值结果
         */
        public static double executeMaxDouble(Object entity, Where where, QueryParameter parameter, DataComparable<? extends Number> data, DataAccess dataAccess) {
                String alias = "maxRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.max(data), alias)));
                return (res == null || res.size() == 0) ? 0D : Double.parseDouble(res.get(0).get(alias).toString());
        }

        /**
         * 执行求最小值
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param data       求最小值字段
         * @param dataAccess DataAccess实例
         * @return int类型的求最小值结果
         */
        public static int executeMinInt(Object entity, Where where, QueryParameter parameter, DataComparable<? extends Number> data, DataAccess dataAccess) {
                String alias = "minRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.min(data), alias)));
                return (res == null || res.size() == 0) ? 0 : Integer.parseInt(res.get(0).get(alias).toString());
        }

        /**
         * 执行求最小值
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param data       求最小值字段
         * @param dataAccess DataAccess实例
         * @return long类型的求最小值结果
         */
        public static long executeMinLong(Object entity, Where where, QueryParameter parameter, DataComparable<? extends Number> data, DataAccess dataAccess) {
                String alias = "minRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.min(data), alias)));
                return (res == null || res.size() == 0) ? 0L : Long.parseLong(res.get(0).get(alias).toString());
        }

        /**
         * 执行求最小值
         *
         * @param entity     查询实体
         * @param where      查询条件
         * @param parameter  查询参数
         * @param data       求最小值字段
         * @param dataAccess DataAccess实例
         * @return double类型的求最小值结果
         */
        public static double executeMinDouble(Object entity, Where where, QueryParameter parameter, DataComparable<? extends Number> data, DataAccess dataAccess) {
                String alias = "minRes";
                List<Map<String, Object>> res = dataAccess.projection(entity, where, new QueryParameterWithFocus(parameter, new Focus().focus(Functions.min(data), alias)));
                return (res == null || res.size() == 0) ? 0D : Double.parseDouble(res.get(0).get(alias).toString());
        }

        /**
         * 返回OrderBase数组<br>
         * 此处直接返回输入的参数<br>
         * 用于快速实现SortedEntity接口
         *
         * @param bases 排序依据
         * @return 输入的OrderBase组成的数组(实际上是直接返回输入的参数)
         */
        public static OrderBase[] sorted(OrderBase... bases) {
                return bases;
        }
}
