package net.cassite.pure.data.jpa;

import net.cassite.pure.data.*;
import net.cassite.pure.data.util.AliasMap;
import net.cassite.pure.data.util.ConstantMap;
import net.cassite.pure.data.util.DataUtils;

import javax.persistence.*;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/10.
 */
public class JPQLDataAccess implements DataAccess {

    private static class Args {
        public StringBuilder sb;
        public Where whereClause;
        public QueryParameter queryParameter;

        public Class<?> entityClass;
        public AliasMap aliasMap;
        public ConstantMap constantMap;
        public Map<Class<?>, String> toJoin;

        public IExpression expression;
        public Object obj;
        public Condition condition;

        public Args doClone() {
            Args args = new Args();
            args.sb = this.sb;
            args.whereClause = this.whereClause;
            args.queryParameter = queryParameter;
            args.entityClass = this.entityClass;
            args.aliasMap = this.aliasMap;
            args.constantMap = this.constantMap;
            args.toJoin = this.toJoin;
            return args;
        }
    }

    private final EntityManager entityManager;

    /*
        public JPQLDataAccess(EntityManager entityManager) {
            this.entityManager = entityManager;
        }
    */
    // TODO
    public JPQLDataAccess() {
        entityManager = null;
    }

    private Args fillArgsWithObj(Args toClone, Object obj) {
        Args a = toClone.doClone();
        a.obj = obj;
        return a;
    }

    /**
     * generate + - * / % and functions
     */
    private String generateExpression(Args args) {
        if (args.expression.expType() == ExpressionType.add) {
            return objToString(fillArgsWithObj(args, args.expression.expArgs()[0]));
        } else if (args.expression.expType() == ExpressionType.avg) {
            return "AVG(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.concat) {
            return "CONCAT(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ", " + objToString(fillArgsWithObj(args, args.expression.expArgs()[1])) + ")";
        } else if (args.expression.expType() == ExpressionType.count) {
            return "Count(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.divide) {
            return objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + "/" + objToString(fillArgsWithObj(args, args.expression.expArgs()[1]));
        } else if (args.expression.expType() == ExpressionType.exists) {
            PreResult<?> query = (PreResult<?>) args.expression.expArgs()[0];
            Args a = new Args();
            a.aliasMap = new AliasMap("var", args.aliasMap.getAliasCount());
            a.constantMap=args.constantMap;
            a.toJoin=new LinkedHashMap<Class<?>, String>();
            a.entityClass=query.entityClass;
            a.sb=new StringBuilder();
            a.whereClause = query.whereClause;
            a.queryParameter = null;
            String toReturn = "EXISTS(" + generateSelect(a) + ")";
            args.aliasMap.setAliasCount(a.aliasMap.getAliasCount());
            return toReturn;
        } else if (args.expression.expType() == ExpressionType.length) {
            return "LENGTH(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.locate) {
            return "LOCATE(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ", " + objToString(fillArgsWithObj(args, args.expression.expArgs()[1])) + ")";
        } else if (args.expression.expType() == ExpressionType.lower) {
            return "LOWER(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.max) {
            return "MAX(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.min) {
            return "MIN(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.minus) {
            return objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + " - " + objToString(fillArgsWithObj(args, args.expression.expArgs()[1]));
        } else if (args.expression.expType() == ExpressionType.mod) {
            return objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + " % " + objToString(fillArgsWithObj(args, args.expression.expArgs()[1]));
        } else if (args.expression.expType() == ExpressionType.multi) {
            return objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + " * " + objToString(fillArgsWithObj(args, args.expression.expArgs()[1]));
        } else if (args.expression.expType() == ExpressionType.notExists) {
            PreResult<?> query = (PreResult<?>) args.expression.expArgs()[0];
            Args a = new Args();
            a.aliasMap = new AliasMap("var", args.aliasMap.getAliasCount());
            a.constantMap=args.constantMap;
            a.toJoin=new LinkedHashMap<Class<?>, String>();
            a.entityClass=query.entityClass;
            a.sb=new StringBuilder();
            a.whereClause = query.whereClause;
            a.queryParameter = null;
            String toReturn = "NOT EXISTS(" + generateSelect(a) + ")";
            args.aliasMap.setAliasCount(a.aliasMap.getAliasCount());
            return toReturn;
        } else if (args.expression.expType() == ExpressionType.substring) {
            return "SUBSTRING(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ", " + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ", " + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.sum) {
            return "SUM(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.trim) {
            return "TRIM(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else if (args.expression.expType() == ExpressionType.unary_negative) {
            return "- " + objToString(fillArgsWithObj(args, args.expression.expArgs()[0]));
        } else if (args.expression.expType() == ExpressionType.upper) {
            return "UPPER(" + objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + ")";
        } else {
            throw new UnsupportedOperationException(args.expression.expType() + " not supported");
        }
    }

    private Args fillArgsWithExpression(Args toClone, IExpression expression) {
        Args a = toClone.doClone();
        a.expression = expression;
        return a;
    }

    /**
     * convert an object to string, for constants it leave a place to fill
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
                sb.append(objToString(fillArgsWithObj(args, o)));
            }
            return sb.append(")").toString();
        } else if (args.obj instanceof List) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            boolean isFirst = true;
            for (Object o : (List<?>) args.obj) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(", ");
                }
                sb.append(objToString(fillArgsWithObj(args, o)));
            }
            return sb.append(")").toString();
        } else {
            if (args.obj.getClass().getName().startsWith("java.") || args.obj.getClass().getName().startsWith("javax.")) {
                return "?" + args.constantMap.add(args.obj);
            } else {
                if (args.obj instanceof IData) {
                    return dataToString((IData<?>) args.obj, args);
                } else if (args.obj instanceof IExpression) {
                    return generateExpression(fillArgsWithExpression(args, (IExpression) args.obj));
                } else {
                    throw new UnsupportedOperationException(args.obj.getClass() + " not supported");
                }
            }
        }
    }

    private String dataToString(IData<?> data, Args args) {
        if (!args.aliasMap.containsKey(data.getEntity().getClass())) {
            args.toJoin.put(data.getEntity().getClass(), args.aliasMap.get(data.getEntity().getClass()));
        }
        return args.aliasMap.get(data.getEntity().getClass()) + "." + DataUtils.findFieldNameByIData(data);
    }

    /**
     * generate condition, such as user.id=1
     */
    private String generateCondition(Args args) {
        if (args.condition.type == ConditionTypes.between) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " BETWEEN " + objToString(fillArgsWithObj(args, args.condition.args.get(0))) + " AND " + objToString(fillArgsWithObj(args, args.condition.args.get(1)));
        } else if (args.condition.type == ConditionTypes.eq) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " = " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.ge) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " >= " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.gt) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " > " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.in) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " IN " + objToString(fillArgsWithObj(args, args.condition.args));
        } else if (args.condition.type == ConditionTypes.isNotNull) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " IS NOT NULL";
        } else if (args.condition.type == ConditionTypes.isNull) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " IS NULL";
        } else if (args.condition.type == ConditionTypes.le) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " <= " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.like) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " LIKE " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.lt) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " < " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.member) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " MEMBER " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.ne) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " <> " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.notIn) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " NOT IN " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.notMember) {
            return objToString(fillArgsWithObj(args, args.condition.data)) + " NOT MEMBER " + objToString(fillArgsWithObj(args, args.condition.args.get(0)));
        } else if (args.condition.type == ConditionTypes.reverseMember) {
            return objToString(fillArgsWithObj(args, args.condition.args.get(0))) + " MEMBER " + objToString(fillArgsWithObj(args, args.condition.data));
        } else if (args.condition.type == ConditionTypes.reverseNotMember) {
            return objToString(fillArgsWithObj(args, args.condition.args.get(0))) + " NOT MEMBER " + objToString(fillArgsWithObj(args, args.condition.data));
        } else {
            throw new UnsupportedOperationException(args.condition.type + " not supported");
        }
    }

    private Args fillArgsWithWhereClause(Args toClone, Where whereClause) {
        Args a = toClone.doClone();
        a.whereClause = whereClause;
        return a;
    }

    private Args fillArgsWithCondition(Args toClone, Condition condition) {
        Args a = toClone.doClone();
        a.condition = condition;
        return a;
    }

    /**
     * generate where clause (the generate string won't contain the word 'WHERE')
     */
    private String generateWhere(Args args) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        if (args.whereClause.isAnd()) {
            for (Or or : ((And) args.whereClause).getOrList()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" AND ");
                }
                sb.append("(").append(generateWhere(fillArgsWithWhereClause(args, or))).append(")");
            }
            for (Condition condition : ((And) args.whereClause).getConditionList()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" AND ");
                }
                sb.append(generateWhere(fillArgsWithWhereClause(args, condition)));
            }
            for (ExpressionBoolean expBool : ((And) args.whereClause).getExpBoolList()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" AND ");
                }
                sb.append(generateWhere(fillArgsWithWhereClause(args, expBool)));
            }
        } else if (args.whereClause.isOr()) {
            for (And and : ((Or) args.whereClause).getAndList()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" OR ");
                }
                sb.append(generateWhere(fillArgsWithWhereClause(args, and)));
            }
            for (Condition condition : ((Or) args.whereClause).getConditionList()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" OR ");
                }
                sb.append(generateWhere(fillArgsWithWhereClause(args, condition)));
            }
            for (ExpressionBoolean expBool : ((Or) args.whereClause).getExpBoolList()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(" OR ");
                }
                sb.append(generateWhere(fillArgsWithWhereClause(args, expBool)));
            }
        } else if (args.whereClause.isCondition()) {
            Condition condition = (Condition) args.whereClause;
            Parameter parameter = condition.data;
            if (parameter instanceof IData) {
                Class<?> cls = ((IData<?>) parameter).getEntity().getClass();
                String alias = args.aliasMap.get(cls);
                if (cls != args.entityClass && !args.toJoin.containsKey(cls)) {
                    args.toJoin.put(cls, alias);
                }
            }
            sb.append(generateCondition(fillArgsWithCondition(args, (Condition) args.whereClause)));
        } else if (args.whereClause.isExpression()) {
            sb.append(generateExpression(fillArgsWithExpression(args, (IExpression) args.whereClause)));
        }
        return sb.toString();
    }

    private String generateJoin(Args args) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> joinCls : args.toJoin.keySet()) {
            boolean found = false;
            for (Field f : args.entityClass.getFields()) {
                if (IData.class.isAssignableFrom(f.getType())) {
                    ParameterizedType pt = (ParameterizedType) f.getGenericType();
                    Type[] types = pt.getActualTypeArguments();
                    Class<?> genericType = (Class<?>) types[0];
                    if (joinCls.isAssignableFrom(genericType)) {
                        sb.append(" JOIN ").append(args.aliasMap.get(args.entityClass)).append(".").append(f.getName());
                        found = true;
                        break;
                    }
                }
            }
            if (!found) throw new RuntimeException("Cannot find type reference of " + joinCls + " in fields of " + args.entityClass);
            String alias = args.toJoin.get(joinCls);
            sb.append(" ").append(alias);
        }
        return sb.toString();
    }

    /**
     * generate select query after 'from ...', including join and where
     */
    private void generateJoinWhere(Args args, boolean generateJoin) {
        String where = " WHERE " + generateWhere(args);
        if (generateJoin) {
            args.sb.append(generateJoin(args)).append(where);
        } else {
            args.sb.append(where);
        }
    }

    /**
     * generate a jqpl query select string
     */
    private String generateSelect(Args args) {
        args.sb.append("SELECT ");
        if (args.queryParameter == null || !(args.queryParameter instanceof QueryParameterWithFocus)) {
            args.sb.append(args.aliasMap.get(args.entityClass));
        } else {
            String entityAlias = args.aliasMap.get(args.entityClass);
            for (IData<?> data : ((QueryParameterWithFocus) args.queryParameter).focusList) {
                args.sb.append(entityAlias).append(".").append(DataUtils.findFieldNameByIData(data));
            }
        }
        args.sb.append(" FROM ").append(args.entityClass.getSimpleName()).append(" ").append(args.aliasMap.get(args.entityClass));

        generateJoinWhere(args, true);

        if (args.queryParameter != null) {
            for (QueryParameterTypes type : args.queryParameter.parameters.keySet()) {
                Object[] argsArr = args.queryParameter.parameters.get(type);
                if (type == QueryParameterTypes.orderBy) {
                    args.sb.append(" ORDER BY ");
                    for (Object o : argsArr) {
                        OrderBase order = (OrderBase) o;
                        String fieldName = DataUtils.findFieldNameByIData(order.data);
                        args.sb.append(args.aliasMap.get(order.data.getClass())).append(".").append(fieldName);
                    }
                } else if (type != QueryParameterTypes.limit && type != QueryParameterTypes.top) {
                    throw new UnsupportedOperationException(type + " not supported");
                }
            }
        }

        return args.sb.toString();
    }

    /**
     * Set Paging Parameters and fill constants
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

        for (Integer i : constantMap.keySet()) {
            query.setParameter(i, constantMap.get(i));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <En> List<En> list(Class<En> entityClass, Where whereClause, QueryParameter parameter) {
        AliasMap clsToAlias = new AliasMap("var");
        ConstantMap constantMap = new ConstantMap();

        Args args = new Args();
        args.sb = new StringBuilder();
        args.entityClass = entityClass;
        args.whereClause = whereClause;
        args.queryParameter = parameter;
        args.aliasMap = clsToAlias;
        args.constantMap = constantMap;
        args.toJoin = new LinkedHashMap<Class<?>, String>();
        // TODO
        System.out.println(generateSelect(args));
        return null;
/*
        javax.persistence.Query query = entityManager.createQuery(generateSelect(args));

        setParametersToQuery(query, parameter, constantMap);

        return (List<En>) query.getResultList();
*/
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> map(Class<?> entityClass, Where whereClause, QueryParameterWithFocus parameter) {
        if (parameter == null) {
            try {
                parameter = new QueryParameterWithFocus();
                Object entity = entityClass.newInstance();
                for (Field f : entityClass.getFields()) {
                    if (IData.class.isAssignableFrom(f.getType())) {
                        parameter.focus((IData<?>) f.get(entity));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        AliasMap clsToAlias = new AliasMap("var");
        ConstantMap constantMap = new ConstantMap();

        Args args = new Args();
        args.entityClass = entityClass;
        args.whereClause = whereClause;
        args.queryParameter = parameter;
        args.aliasMap = clsToAlias;
        args.constantMap = constantMap;

        javax.persistence.Query query = entityManager.createQuery(generateSelect(args));

        setParametersToQuery(query, parameter, constantMap);

        List<Object[]> resultList = (List<Object[]>) query.getResultList();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(resultList.size());
        for (Object[] res : resultList) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            for (int i = 0; i < res.length; ++i) {
                map.put(DataUtils.findFieldNameByIData(parameter.focusList.get(i)), res[i]);
            }
            list.add(map);
        }
        return list;
    }

    @Override
    public <En> void update(Class<En> entityClass, Where whereClause, UpdateEntry[] entries) {

    }

    @Override
    public void remove(Class<?> entityClass, Where whereClause) {

    }

    @Override
    public void save(Object[] entity) {

    }

    @Override
    public <E, T extends Iterable<E>> T find(Class<E> cls, String query) {
        return null;
    }

    @Override
    public void execute(String query) {

    }
}
