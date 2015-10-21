package net.cassite.pure.data.jpa;

import net.cassite.pure.data.*;
import net.cassite.pure.data.util.AliasMap;
import net.cassite.pure.data.util.ConstantMap;
import net.cassite.pure.data.DataUtils;
import net.cassite.pure.data.util.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * JPA的DataAccess实现.<br>
 * 将Pure.Data Query转化为JPQL(或者直接传入)并通过JPQL进行增删改查.<br>
 * 构造时传入EntityManagerFactory
 */
public class JPQLDataAccess implements DataAccess {

    private static final String aliasPrefix = "var";

    private Logger logger = LoggerFactory.getLogger(DataAccess.class);

    private static class Args {
        public StringBuilder sb;
        public Where whereClause;
        public QueryParameter queryParameter;

        public Object entity;
        public Class<?> entityClass;
        public AliasMap aliasMap;
        public ConstantMap constantMap;
        public Map<Location, String> toJoin;

        public IExpression expression;
        public Object obj;
        public Condition condition;

        public String aggregateFunctions;
        public List<String> selectNonAggregationAliases;

        public Args doClone() {
            Args args = new Args();
            args.sb = this.sb;
            args.whereClause = this.whereClause;
            args.queryParameter = queryParameter;
            args.entity = entity;
            args.entityClass = this.entityClass;
            args.aliasMap = this.aliasMap;
            args.constantMap = this.constantMap;
            args.toJoin = this.toJoin;
            args.aggregateFunctions = this.aggregateFunctions;
            args.selectNonAggregationAliases = this.selectNonAggregationAliases;
            return args;
        }

        public Args fillObj(Object obj) {
            Args a = doClone();
            a.obj = obj;
            return a;
        }

        public Args fillExp(IExpression exp) {
            Args a = doClone();
            a.expression = exp;
            return a;
        }

        public Args fillWhere(Where where) {
            Args a = doClone();
            a.whereClause = where;
            return a;
        }

        public Args fillCondition(Condition condition) {
            Args a = doClone();
            a.condition = condition;
            return a;
        }

        public Args prepareForSubQuery(PreResult<?> query) {
            Args a = this.doClone();
            a.aliasMap = new AliasMap(aliasPrefix, this.aliasMap.getAliasCount());
            a.toJoin = new LinkedHashMap<Location, String>();
            a.sb = new StringBuilder();
            a.entity = query.entity;
            a.entityClass = query.entity.getClass();
            a.whereClause = query.whereClause;
            a.queryParameter = null;
            a.aggregateFunctions = null;
            a.selectNonAggregationAliases = new ArrayList<String>();
            return a;
        }
    }

    private final EntityManager entityManager;

    public JPQLDataAccess(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * 生成表达式<br>
     * 调用objToString,对于exists也调用generateSelect
     *
     * @param args Args对象
     * @return 生成的表达式语句
     * @see JPQLDataAccess#objToString(Args)
     * @see JPQLDataAccess#generateSelect(Args)
     */
    private String generateExpression(Args args) {
        if (args.expression.expType() == ExpressionType.add) {
            return objToString(args.fillObj(args.expression.expArgs()[0]));
        } else if (args.expression.expType() == ExpressionType.avg) {
            return "AVG(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.concat) {
            return "CONCAT(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ", " + objToString(args.fillObj(args.expression.expArgs()[1])) + ")";
        } else if (args.expression.expType() == ExpressionType.count) {
            return "Count(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.divide) {
            return objToString(args.fillObj(args.expression.expArgs()[0])) + " / " + objToString(args.fillObj(args.expression.expArgs()[1]));
        } else if (args.expression.expType() == ExpressionType.exists) {
            PreResult<?> query = (PreResult<?>) args.expression.expArgs()[0];
            Args a = args.prepareForSubQuery(query);
            String toReturn = "EXISTS(" + generateSelect(a) + ")";
            args.aliasMap.setAliasCount(a.aliasMap.getAliasCount());
            return toReturn;
        } else if (args.expression.expType() == ExpressionType.length) {
            return "LENGTH(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.locate) {
            return "LOCATE(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ", " + objToString(args.fillObj(args.expression.expArgs()[1])) + ")";
        } else if (args.expression.expType() == ExpressionType.lower) {
            return "LOWER(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.max) {
            return "MAX(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.min) {
            return "MIN(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.minus) {
            return objToString(args.fillObj(args.expression.expArgs()[0])) + " - " + objToString(args.fillObj(args.expression.expArgs()[1]));
        } else if (args.expression.expType() == ExpressionType.mod) {
            return objToString(args.fillObj(args.expression.expArgs()[0])) + " % " + objToString(args.fillObj(args.expression.expArgs()[1]));
        } else if (args.expression.expType() == ExpressionType.multi) {
            return objToString(args.fillObj(args.expression.expArgs()[0])) + " * " + objToString(args.fillObj(args.expression.expArgs()[1]));
        } else if (args.expression.expType() == ExpressionType.notExists) {
            PreResult<?> query = (PreResult<?>) args.expression.expArgs()[0];
            Args a = args.prepareForSubQuery(query);
            String toReturn = "NOT EXISTS(" + generateSelect(a) + ")";
            args.aliasMap.setAliasCount(a.aliasMap.getAliasCount());
            return toReturn;
        } else if (args.expression.expType() == ExpressionType.substring) {
            return "SUBSTRING(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ", " + objToString(args.fillObj(args.expression.expArgs()[1])) + ", " + objToString(args.fillObj(args.expression.expArgs()[2])) + ")";
        } else if (args.expression.expType() == ExpressionType.sum) {
            return "SUM(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.trim) {
            return "TRIM(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.unary_negative) {
            return "- " + objToString(args.fillObj(args.expression.expArgs()[0]));
        } else if (args.expression.expType() == ExpressionType.upper) {
            return "UPPER(" + objToString(args.fillObj(args.expression.expArgs()[0])) + ")";
        } else {
            throw new UnsupportedOperationException(args.expression.expType() + " not supported");
        }
    }

    /**
     * 转换obj为String.对于pure.data下的对象转换为字符串,对于java自带对象转换为数字的占位符(从1开始)<br>
     * 对于IData,执行dataToString
     * 对于IExpression,执行generateExpression
     *
     * @param args Args对象
     * @return 生成的字符串
     * @see JPQLDataAccess#dataToString(IData, Args)
     * @see JPQLDataAccess#generateExpression(Args)
     */
    private String objToString(Args args) {
        if (args.obj.getClass().isArray()) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            boolean isFirst = true;
            for (Object o : (Object[]) args.obj) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(", ");
                }
                sb.append(objToString(args.fillObj(o)));
            }
            return sb.append(")").toString();
        } else if (args.obj instanceof Iterable) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            boolean isFirst = true;
            for (Object o : (Iterable<?>) args.obj) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(", ");
                }
                sb.append(objToString(args.fillObj(o)));
            }
            return sb.append(")").toString();
        } else {
            if (args.obj.getClass().getName().startsWith("java.") || args.obj.getClass().getName().startsWith("javax.")) {
                return "?" + args.constantMap.add(args.obj);
            } else {
                if (args.obj instanceof IData) {
                    return dataToString((IData<?>) args.obj, args);
                } else if (args.obj instanceof IExpression) {
                    return generateExpression(args.fillExp((IExpression) args.obj));
                } else {
                    throw new UnsupportedOperationException(args.obj.getClass() + " not supported");
                }
            }
        }
    }

    /**
     * 获取实体的调用位置<br>
     * 使用广度优先搜索
     *
     * @param entity          从该实体开始寻找
     * @param toFind          要寻找的实体
     * @param list            已经经过的路径
     * @param args            Args对象
     * @param alreadySearched 已经寻找过的实体
     * @return 找到的实体位置(Location对象), 没找到则返回null
     */
    private Location findEntity(Object entity, Object toFind, List<String> list, Args args, Set<Object> alreadySearched) {
        Map<Object, String> objectStringMap = new LinkedHashMap<Object, String>();
        try {
            for (Field f : entity.getClass().getFields()) {
                // is IData
                if (IData.class.isAssignableFrom(f.getType())) {
                    // get 1st generic type
                    Class<?> cls = ((Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]);
                    // is not java. / javax.
                    if (!cls.getName().startsWith("java.") && !cls.getName().startsWith("javax.")) {
                        IData<?> data = (IData<?>) f.get(entity);
                        Object obj = data.get();
                        if (obj == null) continue;

                        if (obj instanceof Iterable) {
                            // is aggregate
                            Iterator<?> it = ((Iterable<?>) obj).iterator();
                            if (it.hasNext()) {
                                Object o = it.next();
                                if (o == toFind) {
                                    list.add(f.getName());
                                    Location location = DataUtils.generateLocationAndFillMap(list, args.aliasMap);
                                    if (!args.toJoin.containsKey(location) && toFind != args.entity) {
                                        args.toJoin.put(location, args.aliasMap.get(location));
                                    }
                                    return location;
                                } else {
                                    if (!alreadySearched.contains(o)) {
                                        alreadySearched.add(o);
                                        objectStringMap.put(o, f.getName());
                                    }
                                }
                            }
                        } else {
                            // is plain object
                            if (obj == toFind) {
                                list.add(f.getName());
                                Location location = DataUtils.generateLocationAndFillMap(list, args.aliasMap);
                                if (!args.toJoin.containsKey(location) && toFind != args.entity) {
                                    args.toJoin.put(location, args.aliasMap.get(location));
                                }
                                return location;
                            } else {
                                if (!alreadySearched.contains(obj)) {
                                    alreadySearched.add(obj);
                                    objectStringMap.put(obj, f.getName());
                                }
                            }
                        }
                    }
                }
            }
            // not found
            for (Object obj : objectStringMap.keySet()) {
                List<String> nextList = new ArrayList<String>(list);
                nextList.add(objectStringMap.get(obj));
                Location location = findEntity(obj, toFind, nextList, args, alreadySearched);
                if (null != location) {
                    // found
                    return location;
                }
            }
            return null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取Entity位置,若没有找到则抛出异常
     *
     * @param toFind 要寻找的实体
     * @param args   Args对象
     * @return 实体的位置(Location对象)
     */
    private Location findEntity(Object toFind, Args args) {
        Location l = findEntity(args.entity, toFind, new ArrayList<String>(), args, new HashSet<Object>());
        if (l == null) {
            throw new IllegalArgumentException("Cannot find location of " + toFind);
        }
        return l;
    }

    /**
     * 将IData转化为字符串<br>
     * 调用DataUtils.findFieldNameByIData
     *
     * @param data 要转换的IData对象
     * @param args Args对象
     * @return 生成的字符串
     * @see DataUtils#findFieldNameByIData(IData)
     */
    private String dataToString(IData<?> data, Args args) {
        Object entity = data.getEntity();
        String alias = args.aliasMap.get(new Location(new ArrayList<String>()));
        if (entity != args.entity) {
            Location location = findEntity(entity, args);
            alias = args.aliasMap.get(location);
        }
        return alias + "." + DataUtils.findFieldNameByIData(data);
    }

    /**
     * 生成condition类型对应的语句,例如 u.age > 18<br>
     * 生成过程中调用objToString
     *
     * @param args Args对象
     * @return 生成的条件语句
     * @see ConditionTypes
     * @see JPQLDataAccess#objToString(Args)
     */
    private String generateCondition(Args args) {
        if (args.condition.type == ConditionTypes.between) {
            return objToString(args.fillObj(args.condition.data)) + " BETWEEN " + objToString(args.fillObj(args.condition.args.get(0))) + " AND " + objToString(args.fillObj(args.condition.args.get(1)));
        } else if (args.condition.type == ConditionTypes.eq) {
            return objToString(args.fillObj(args.condition.data)) + " = " + objToString(args.fillObj(args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.ge) {
            return objToString(args.fillObj(args.condition.data)) + " >= " + objToString(args.fillObj(args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.gt) {
            return objToString(args.fillObj(args.condition.data)) + " > " + objToString(args.fillObj(args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.in) {
            Args a = args.prepareForSubQuery((PreResult<?>) args.condition.args.get(0));
            String subQuery = generateSelect(a);
            args.aliasMap.setAliasCount(a.aliasMap.getAliasCount());
            return objToString(args.fillObj(args.condition.data)) + " IN (" + subQuery + ")";
        } else if (args.condition.type == ConditionTypes.isNotNull) {
            return objToString(args.fillObj(args.condition.data)) + " IS NOT NULL";
        } else if (args.condition.type == ConditionTypes.isNull) {
            return objToString(args.fillObj(args.condition.data)) + " IS NULL";
        } else if (args.condition.type == ConditionTypes.le) {
            return objToString(args.fillObj(args.condition.data)) + " <= " + objToString(args.fillObj(args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.like) {
            return objToString(args.fillObj(args.condition.data)) + " LIKE " + objToString(args.fillObj(args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.lt) {
            return objToString(args.fillObj(args.condition.data)) + " < " + objToString(args.fillObj(args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.member) {
            return objToString(args.fillObj(args.condition.data)) + " MEMBER " + objToString(args.fillObj(args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.ne) {
            return objToString(args.fillObj(args.condition.data)) + " <> " + objToString(args.fillObj(args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.notIn) {
            Args a = args.prepareForSubQuery((PreResult<?>) args.condition.args.get(0));
            String subQuery = generateSelect(a);
            args.aliasMap.setAliasCount(a.aliasMap.getAliasCount());
            return objToString(args.fillObj(args.condition.data)) + " NOT IN (" + subQuery + ")";
        } else if (args.condition.type == ConditionTypes.notMember) {
            return objToString(args.fillObj(args.condition.data)) + " NOT MEMBER " + objToString(args.fillObj(args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.reverseMember) {
            return objToString(args.fillObj(args.condition.args.get(0))) + " MEMBER " + objToString(args.fillObj(args.condition.data));
        } else if (args.condition.type == ConditionTypes.reverseNotMember) {
            return objToString(args.fillObj(args.condition.args.get(0))) + " NOT MEMBER " + objToString(args.fillObj(args.condition.data));
        } else {
            throw new UnsupportedOperationException(args.condition.type + " not supported");
        }
    }

    /**
     * 生成where子句<br>
     * 若给定where为 And 类型,对每一个分支递归执行本方法,并用and连接
     * 若给定where为 Or 类型,对每一个分支递归执行本方法,并用or连接
     * 若给定where为 Condition 类型,则调用generateCondition
     * 若给定where为 IExpression 类型,则调用generateExpression
     *
     * @param args   Args对象
     * @param having 向args中添加having项
     * @return where子句内容(不包括where这个词)
     */
    private String generateWhere(Args args, boolean having) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        if (args.whereClause.isAnd()) {
            if (having) {
                Where Optionalhaving = DataUtils.getAggregate(args.whereClause);
                if (Optionalhaving != null) args.aggregateFunctions = generateWhere(args.fillWhere(Optionalhaving), false);
                if (Optionalhaving == null) having = false;
            }
            for (Or or : ((And) args.whereClause).getOrList()) {
                if (having && DataUtils.getAggregate(or) != null) continue;
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" AND ");
                }
                sb.append("(").append(generateWhere(args.fillWhere(or), false)).append(")");
            }
            for (Condition condition : ((And) args.whereClause).getConditionList()) {
                if (having && DataUtils.getAggregate(condition) != null) continue;
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" AND ");
                }
                sb.append(generateWhere(args.fillWhere(condition), false));
            }
            for (ExpressionBoolean expBool : ((And) args.whereClause).getExpBoolList()) {
                if (having && DataUtils.getAggregate(expBool) != null) continue;
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" AND ");
                }
                sb.append(generateWhere(args.fillWhere(expBool), false));
            }
        } else if (args.whereClause.isOr()) {
            for (And and : ((Or) args.whereClause).getAndList()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" OR ");
                }
                sb.append(generateWhere(args.fillWhere(and), false));
            }
            for (Condition condition : ((Or) args.whereClause).getConditionList()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" OR ");
                }
                sb.append(generateWhere(args.fillWhere(condition), false));
            }
            for (ExpressionBoolean expBool : ((Or) args.whereClause).getExpBoolList()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" OR ");
                }
                sb.append(generateWhere(args.fillWhere(expBool), false));
            }
        } else if (args.whereClause.isCondition()) {
            sb.append(generateCondition(args.fillCondition((Condition) args.whereClause)));
        } else if (args.whereClause.isExpression()) {
            sb.append(generateExpression(args.fillExp((IExpression) args.whereClause)));
        }
        return sb.toString();
    }

    /**
     * 生成join子句<br>
     * 根据args.toJoin生成
     *
     * @param args Args对象
     * @return join子句内容
     */
    private String generateJoin(Args args) {
        StringBuilder sb = new StringBuilder();
        for (Location joinLocation : args.toJoin.keySet()) {
            sb.append(" JOIN ").append(args.aliasMap.get(new Location(null))).append(".").append(joinLocation.toString()).append(" ").append(args.toJoin.get(joinLocation));
        }
        return sb.toString();
    }

    /**
     * 生成join和where子句<br>
     * 单个Expression或者And的第二个语句可能是having子句
     * 调用generateWhere和generateJoin
     *
     * @param args Args对象
     */
    private void generateJoinWhere(Args args) {
        if (args.whereClause != null) {
            if (args.whereClause instanceof IExpression && DataUtils.expressionIsAggregate((IExpression) args.whereClause)) {
                args.aggregateFunctions = generateWhere(args, false);
                args.sb.append(generateJoin(args));
            } else if (args.whereClause instanceof Condition && null != DataUtils.getAggregate(args.whereClause)) {
                args.aggregateFunctions = generateWhere(args, false);
                args.sb.append(generateJoin(args));
            } else {
                String where = " WHERE " + generateWhere(args, true);
                args.sb.append(generateJoin(args)).append(where);
            }
        }
    }

    /**
     * 生成group by和having子句
     *
     * @param args Args对象
     */
    private void generateGroupByHaving(Args args) {
        if (args.aggregateFunctions != null) {
            args.sb.append(" GROUP BY ");
            boolean isFirst = true;
            for (String s : args.selectNonAggregationAliases) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    args.sb.append(", ");
                }
                args.sb.append(s);
            }
            args.sb.append(" HAVING ").append(args.aggregateFunctions);
        }
    }

    /**
     * 生成select语句<br>
     * <ul>
     * <li>根据传入的queryParameter确定是否查询部分字段,调用DataUtils.findFieldNameByIData</li>
     * <li>根据entity类型生成from子句</li>
     * <li>调用generateJoinWhere生成join和where子句</li>
     * <li>调用generateGroupByHaving生成groupby和having子句</li>
     * <li>根据传入的queryParameter生成末尾的语句,例如order by</li>
     * </ul>
     *
     * @param args Args对象
     * @return 生成的语句
     */
    private String generateSelect(Args args) {
        String entityAlias = args.aliasMap.get(new Location(new ArrayList<String>(0)));
        args.sb.append("SELECT ");
        if (args.queryParameter == null || !(args.queryParameter instanceof QueryParameterWithFocus) || ((QueryParameterWithFocus) args.queryParameter).focusMap.size() == 0) {
            args.sb.append(entityAlias);
            args.selectNonAggregationAliases.add(entityAlias);
        } else {
            boolean isFirst = true;
            for (IData<?> o : ((QueryParameterWithFocus) args.queryParameter).focusMap.keySet()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    args.sb.append(", ");
                }
                String alias = ((QueryParameterWithFocus) args.queryParameter).focusMap.get(o);
                args.sb.append(entityAlias).append(".").append(DataUtils.findFieldNameByIData(o)).append(" as ").append(alias);
                args.selectNonAggregationAliases.add(alias);
            }
        }
        args.sb.append(" FROM ").append(args.entityClass.getSimpleName()).append(" ").append(entityAlias);

        generateJoinWhere(args); // where
        generateGroupByHaving(args); // group by having

        if (args.queryParameter != null) {
            for (QueryParameterTypes type : args.queryParameter.parameters.keySet()) {
                Object[] argsArr = args.queryParameter.parameters.get(type);
                if (type == QueryParameterTypes.orderBy) {
                    args.sb.append(" ORDER BY ");
                    for (Object o : argsArr) {
                        OrderBase order = (OrderBase) o;
                        Location loc = findEntity(order.data.getEntity(), args);
                        Field f = DataUtils.findFieldByContainedEntity(order.data.getEntity(), args.entity);
                        args.sb.append(args.aliasMap.get(loc)).append(".").append(f.getName());
                    }
                } else if (type != QueryParameterTypes.limit && type != QueryParameterTypes.top) {
                    throw new UnsupportedOperationException(type + " not supported");
                }
            }
        }

        return args.sb.toString();
    }

    /**
     * 向Query填入额外信息,例如limit,top,以及填入需要填写的常量<br>
     * 调用setConstants
     *
     * @param query       Query对象
     * @param parameter   查询信息
     * @param constantMap 常量Map
     * @see JPQLDataAccess#setConstants(Query, Map)
     */
    private void setParametersToQuery(Query query, QueryParameter parameter, ConstantMap constantMap) {
        if (parameter != null) {
            for (QueryParameterTypes type : parameter.parameters.keySet()) {
                Object[] args = parameter.parameters.get(type);
                if (type == QueryParameterTypes.limit) {
                    query.setFirstResult((Integer) args[0]).setMaxResults(((Integer) args[1]) - ((Integer) args[0]) + 1);
                } else if (type == QueryParameterTypes.top) {
                    query.setMaxResults((Integer) args[0]);
                } else if (type != QueryParameterTypes.orderBy) {
                    throw new UnsupportedOperationException(type + " not supported");
                }
            }
        }

        setConstants(query, constantMap);
    }

    /**
     * 向Query填入常量
     *
     * @param query       Query对象
     * @param constantMap 常量Map
     */
    private void setConstants(Query query, Map<Integer, Object> constantMap) {
        for (Integer i : constantMap.keySet()) {
            query.setParameter(i, constantMap.get(i));
        }
    }

    /**
     * 初始化Args对象<br>
     * 初始化args.sb/entity/entityClass/entityAlias/whereClause/queryParameter/aliasMap/constantMap/toJoin
     *
     * @param args        要初始化的Args对象
     * @param entity      目标实体
     * @param whereClause 条件
     * @param parameter   查询参数
     */
    private void initArgs(Args args, Object entity, Where whereClause, QueryParameter parameter) {
        args.sb = new StringBuilder();
        args.entity = entity;
        args.entityClass = entity.getClass();
        args.whereClause = whereClause;
        args.queryParameter = parameter;
        args.aliasMap = new AliasMap(aliasPrefix);
        args.constantMap = new ConstantMap();
        args.toJoin = new LinkedHashMap<Location, String>();
        args.selectNonAggregationAliases = new ArrayList<String>();
    }

    @Override
    public <En> En find(Class<En> entityClass, Object pkValue) {
        return entityManager.find(entityClass, pkValue);
    }

    /**
     * 执行查询,注入实体并返回结果List<br>
     * 调用initArgs,generateSelect,[createQuery],setParametersToQuery
     *
     * @param entity      要查询的实体
     * @param whereClause 查询条件
     * @param parameter   查询参数(可以为空)
     * @param <En>        实体类型
     * @return 查询的实体结果List
     * @see JPQLDataAccess#initArgs(Args, Object, Where, QueryParameter)
     * @see JPQLDataAccess#generateSelect(Args)
     * @see EntityManager#createQuery(String)
     * @see JPQLDataAccess#setParametersToQuery(Query, QueryParameter, ConstantMap)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <En> List<En> list(En entity, Where whereClause, QueryParameter parameter) {
        Args args = new Args();
        initArgs(args, entity, whereClause, parameter);

        String selectQuery = generateSelect(args);
        logger.debug("Generated JPQL Query is : {} ---- WITH PARAMETERS : {}", selectQuery, args.constantMap);

        javax.persistence.Query query = entityManager.createQuery(selectQuery);

        setParametersToQuery(query, parameter, args.constantMap);

        return (List<En>) query.getResultList();
    }

    /**
     * 检查parameter,若为空则返回一个"查询所有非Iterable字段"的parameter.若不为空则直接返回
     *
     * @param entity    要查询的实体
     * @param parameter 查询参数
     * @return 非空的parameter
     */
    private QueryParameterWithFocus validateQueryParameterWithFocus(Object entity, QueryParameterWithFocus parameter) {
        if (parameter == null) {
            try {
                parameter = new QueryParameterWithFocus();
                for (Field f : entity.getClass().getFields()) {
                    if (IData.class.isAssignableFrom(f.getType()) && !ParameterAggregate.class.isAssignableFrom(f.getType())) {
                        parameter.focus((IData<?>) f.get(entity));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return parameter;
    }

    /**
     * 查询部分字段的查询的初始化
     *
     * @param args        Args对象
     * @param entity      要查询实体
     * @param whereClause 查询条件
     * @param parameter   查询参数
     * @return 生成的查询语句
     */
    private String mapInit(Args args, Object entity, Where whereClause, QueryParameterWithFocus parameter) {
        initArgs(args, entity, whereClause, parameter);

        return generateSelect(args);
    }

    /**
     * 将JPA返回的List&lt;Object[]&gt;转化为List&lt;Map&lt;字段名,对象&gt;&gt;
     *
     * @param resultList JPA返回List
     * @param parameter  查询参数
     * @return 转换后的List&lt;Map&gt;
     */
    private List<Map<String, Object>> listToMap(List<Object> resultList, QueryParameterWithFocus parameter) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(resultList.size());
        for (Object res : resultList) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            if (res.getClass().isArray()) {
                int i = 0;
                for (String alias : parameter.focusMap.values()) {
                    map.put(alias, ((Object[]) res)[i]);
                    ++i;
                }
            } else {
                map.put(parameter.focusMap.values().iterator().next(), res);
            }
            list.add(map);

        }
        return list;
    }

    /**
     * 执行查询,生成List&lt;Map&lt;字段名,对象&gt;&gt;形式的结果<br>
     * 调用validateQueryParameterWithFocus,mapInit,[createQuery],setParametersToQuery
     *
     * @param entity      要查询的实体
     * @param whereClause 条件
     * @param parameter   要查询的字段以及查询参数(可以为空,若空则查询所有非Iterable的字段)
     * @return 查询的Map结果
     * @see JPQLDataAccess#validateQueryParameterWithFocus(Object, QueryParameterWithFocus)
     * @see JPQLDataAccess#mapInit(Args, Object, Where, QueryParameterWithFocus)
     * @see EntityManager#createQuery(String)
     * @see JPQLDataAccess#setParametersToQuery(Query, QueryParameter, ConstantMap)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> projection(Object entity, Where whereClause, QueryParameterWithFocus parameter) {
        Args args = new Args();
        parameter = validateQueryParameterWithFocus(entity, parameter);
        String selectQuery = mapInit(args, entity, whereClause, parameter);

        logger.debug("Generated JPQL Query is : {} ---- WITH PARAMETERS : {}", selectQuery, args.constantMap);

        javax.persistence.Query query = entityManager.createQuery(selectQuery);

        setParametersToQuery(query, parameter, args.constantMap);

        return listToMap(query.getResultList(), parameter);
    }

    /**
     * 初始化更新<br>
     * 在args.sb中生成完整Update语句
     *
     * @param args        Args对象
     * @param entity      要更新实体
     * @param whereClause 更新条件
     * @param entries     更新内容
     */
    private void updateInit(Args args, Object entity, Where whereClause, UpdateEntry[] entries) {
        initArgs(args, entity, whereClause, null);
        args.sb.append("UPDATE ").append(entity.getClass().getSimpleName()).append(" ").append(args.aliasMap.get(new Location(null))).append(" SET ");
        for (UpdateEntry entry : entries) {
            args.sb.append(DataUtils.findFieldNameByIData(entry.data)).append(" = ").append(objToString(args.fillObj(entry.updateValue)));
        }
        args.sb.append(" WHERE ").append(generateWhere(args, false));
    }

    /**
     * 更新<br>
     * 调用updateInit,setConstants
     *
     * @param entity      要更新的实体
     * @param whereClause 更新条件
     * @param entries     更新内容(set)
     * @see JPQLDataAccess#updateInit(Args, Object, Where, UpdateEntry[])
     * @see JPQLDataAccess#setConstants(Query, Map)
     */
    @Override
    public void update(Object entity, Where whereClause, UpdateEntry[] entries) {
        Args args = new Args();
        updateInit(args, entity, whereClause, entries);
        logger.debug("Generated JPQL Query is : {} ---- WITH PARAMETERS {}", args.sb.toString(), args.constantMap);

        Query query = entityManager.createQuery(args.sb.toString());
        setConstants(query, args.constantMap);
        query.executeUpdate();
    }

    /**
     * 删除初始化<br>
     * 生成完整Delete语句
     *
     * @param args        Args对象
     * @param entity      删除实体
     * @param whereClause 删除条件
     */
    private void removeInit(Args args, Object entity, Where whereClause) {
        initArgs(args, entity, whereClause, null);
        args.sb.append("DELETE FROM ").append(entity.getClass().getSimpleName()).append(" ").append(args.aliasMap.get(new Location(null))).append(generateWhere(args, false));
    }

    /**
     * 删除<br>
     * 调用removeInit,setConstants
     *
     * @param entity      要删除实体
     * @param whereClause 删除条件
     * @see JPQLDataAccess#removeInit(Args, Object, Where)
     * @see JPQLDataAccess#setConstants(Query, Map)
     */
    @Override
    public void remove(Object entity, Where whereClause) {
        Args args = new Args();
        removeInit(args, entity, whereClause);

        logger.debug("Generated JPQL Query is : {} ---- WITH PARAMETERS {}", args.sb.toString(), args.constantMap);

        Query query = entityManager.createQuery(args.sb.toString());
        setConstants(query, args.constantMap);
        query.executeUpdate();
    }

    @Override
    public void save(Object[] entities) {
        for (Object e : entities) {
            entityManager.persist(e);
        }
    }

    /**
     * 执行查询,返回实体
     *
     * @param query     JPQL查询语句
     * @param parameter 查询参数
     * @param <E>       实体类型
     * @return List&lt;实体&gt;
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> find(Object query, QueryParameter parameter) {
        return (List<E>) entityManager.createQuery((String) query).getResultList();
    }

    /**
     * 执行查询,返回字段Map
     *
     * @param query     JPQL查询语句
     * @param parameter 查询参数
     * @return List&lt;Map&lt;字段名,值&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> find(Object query, QueryParameterWithFocus parameter) {
        return listToMap(entityManager.createQuery((String) query).getResultList(), parameter);
    }

    @Override
    public void execute(Object query) {
        entityManager.createQuery((String) query).executeUpdate();
    }
}
