package net.cassite.pure.data.jpa;

import net.cassite.pure.data.*;
import net.cassite.pure.data.util.AliasMap;
import net.cassite.pure.data.util.ConstantMap;
import net.cassite.pure.data.util.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/10.
 */
public class JPQLDataAccess implements DataAccess {

    private static final String aliasPrefix = "var";

    private Logger logger = LoggerFactory.getLogger(DataAccess.class);

    private static String generateAlias(Object entity, int count) {
        return entity.getClass().getSimpleName().substring(0, 1).toLowerCase() + "_" + aliasPrefix + count;
    }

    private static class Args {
        public StringBuilder sb;
        public Where whereClause;
        public QueryParameter queryParameter;

        public Object entity;
        public Class<?> entityClass;
        public String entityAlias;
        public AliasMap aliasMap;
        public ConstantMap constantMap;
        public Map<Field, String> toJoin;

        public IExpression expression;
        public Object obj;
        public Condition condition;

        public Args doClone() {
            Args args = new Args();
            args.sb = this.sb;
            args.whereClause = this.whereClause;
            args.queryParameter = queryParameter;
            args.entity = entity;
            args.entityClass = this.entityClass;
            args.entityAlias = this.entityAlias;
            args.aliasMap = this.aliasMap;
            args.constantMap = this.constantMap;
            args.toJoin = this.toJoin;
            return args;
        }
    }

    private final EntityManager entityManager;

    public JPQLDataAccess(EntityManager entityManager) {
        this.entityManager = entityManager;
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
            return objToString(fillArgsWithObj(args, args.expression.expArgs()[0])) + " / " + objToString(fillArgsWithObj(args, args.expression.expArgs()[1]));
        } else if (args.expression.expType() == ExpressionType.exists) {
            PreResult<?> query = (PreResult<?>) args.expression.expArgs()[0];
            Args a = args.doClone();
            a.aliasMap = new AliasMap(aliasPrefix, args.aliasMap.getAliasCount() + 1);
            a.toJoin = new LinkedHashMap<Field, String>();
            a.sb = new StringBuilder();
            a.entity = query.entity;
            a.entityClass = query.entity.getClass();
            a.entityAlias = generateAlias(query.entity, args.aliasMap.getAliasCount() + 1);
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
            Args a = args.doClone();
            a.aliasMap = new AliasMap(aliasPrefix, args.aliasMap.getAliasCount() + 1);
            a.toJoin = new LinkedHashMap<Field, String>();
            a.sb = new StringBuilder();
            a.entity = query.entity;
            a.entityClass = query.entity.getClass();
            a.entityAlias = generateAlias(query.entity, args.aliasMap.getAliasCount() + 1);
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
        Object entity = data.getEntity();
        String alias;
        if (entity == args.entity) {
            alias = args.entityAlias;
        } else {
            Field f = DataUtils.findFieldByContainedEntity(entity, args.entity);
            alias = args.aliasMap.get(f);
            if (!args.toJoin.containsKey(f)) {
                args.toJoin.put(f, alias);
            }
        }
        return alias + "." + DataUtils.findFieldNameByIData(data);
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
            sb.append(generateCondition(fillArgsWithCondition(args, (Condition) args.whereClause)));
        } else if (args.whereClause.isExpression()) {
            sb.append(generateExpression(fillArgsWithExpression(args, (IExpression) args.whereClause)));
        }
        return sb.toString();
    }

    private String generateJoin(Args args) {
        StringBuilder sb = new StringBuilder();
        for (Field joinField : args.toJoin.keySet()) {
            sb.append(" JOIN ").append(args.entityAlias).append(".").append(joinField.getName()).append(" ").append(args.aliasMap.get(joinField));
        }
        return sb.toString();
    }

    /**
     * generate select selectQuery after 'from ...', including join and where
     */
    private void generateJoinWhere(Args args) {
        String where = " WHERE " + generateWhere(args);
        args.sb.append(generateJoin(args)).append(where);
    }

    /**
     * generate a jqpl selectQuery select string
     */
    private String generateSelect(Args args) {
        args.sb.append("SELECT ");
        if (args.queryParameter == null || !(args.queryParameter instanceof QueryParameterWithFocus)) {
            args.sb.append(args.entityAlias);
        } else {
            boolean isFirst = true;
            for (IData<?> data : ((QueryParameterWithFocus) args.queryParameter).focusList) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    args.sb.append(", ");
                }
                args.sb.append(args.entityAlias).append(".").append(DataUtils.findFieldNameByIData(data));
            }
        }
        args.sb.append(" FROM ").append(args.entityClass.getSimpleName()).append(" ").append(args.entityAlias);

        generateJoinWhere(args);

        if (args.queryParameter != null) {
            for (QueryParameterTypes type : args.queryParameter.parameters.keySet()) {
                Object[] argsArr = args.queryParameter.parameters.get(type);
                if (type == QueryParameterTypes.orderBy) {
                    args.sb.append(" ORDER BY ");
                    for (Object o : argsArr) {
                        OrderBase order = (OrderBase) o;
                        Field f = DataUtils.findFieldByContainedEntity(order.data.getEntity(), args.entity);
                        args.sb.append(args.aliasMap.get(f)).append(".").append(f.getName());
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

        setConstants(query, constantMap);
    }

    /**
     * fill constants
     */
    private void setConstants(Query query, Map<Integer, Object> constantMap) {
        for (Integer i : constantMap.keySet()) {
            query.setParameter(i, constantMap.get(i));
        }
    }

    private void initArgs(Args args, Object entity, Where whereClause, QueryParameter parameter) {
        args.sb = new StringBuilder();
        args.entity = entity;
        args.entityClass = entity.getClass();
        args.entityAlias = generateAlias(args.entity, 0);
        args.whereClause = whereClause;
        args.queryParameter = parameter;
        args.aliasMap = new AliasMap(aliasPrefix);
        args.constantMap = new ConstantMap();
        args.toJoin = new LinkedHashMap<Field, String>();
    }

    @Override
    public <En> En find(Class<En> entityClass, Object pkValue) {
        return entityManager.find(entityClass, pkValue);
    }

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

    private String mapInit(Args args, Object entity, Where whereClause, QueryParameterWithFocus parameter) {
        initArgs(args, entity, whereClause, parameter);

        return generateSelect(args);
    }

    private List<Map<String, Object>> listToMap(List<Object[]> resultList, QueryParameterWithFocus parameter) {
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

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> map(Object entity, Where whereClause, QueryParameterWithFocus parameter) {
        Args args = new Args();
        parameter = validateQueryParameterWithFocus(entity, parameter);
        String selectQuery = mapInit(args, entity, whereClause, parameter);

        logger.debug("Generated JPQL Query is : {} ---- WITH PARAMETERS : {}", selectQuery, args.constantMap);

        javax.persistence.Query query = entityManager.createQuery(selectQuery);

        setParametersToQuery(query, parameter, args.constantMap);

        return listToMap(query.getResultList(), parameter);
    }

    private void updateInit(Args args, Object entity, Where whereClause, UpdateEntry[] entries) {
        initArgs(args, entity, whereClause, null);
        args.sb.append("UPDATE ").append(entity.getClass().getSimpleName()).append(" ").append(args.entityAlias).append(" SET ");
        for (UpdateEntry entry : entries) {
            args.sb.append(DataUtils.findFieldNameByIData(entry.data)).append(" = ").append(objToString(fillArgsWithObj(args, entry.updateValue)));
        }
        args.sb.append(" WHERE ").append(generateWhere(args));
    }

    @Override
    public void update(Object entity, Where whereClause, UpdateEntry[] entries) {
        Args args = new Args();

        logger.debug("Generated JPQL Query is : {} ---- WITH PARAMETERS {}", args.sb.toString(), args.constantMap);

        Query query = entityManager.createQuery(args.sb.toString());
        setConstants(query, args.constantMap);
        query.executeUpdate();
    }

    private void removeInit(Args args, Object entity, Where whereClause) {
        initArgs(args, entity, whereClause, null);
        args.sb.append("DELETE FROM ").append(entity.getClass().getSimpleName()).append(" ").append(args.entityAlias).append(generateWhere(args));
    }

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
    public void save(Object[] entity) {
        for (Object e : entity) {
            entityManager.persist(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> find(String query, QueryParameter parameter) {
        return (List<E>) entityManager.createQuery(query).getResultList();
    }

    @Override
    public <En> NamedListQuery<En> makeList(String name, En entity, Where whereClause, QueryParameter parameter) {
        Args args = new Args();
        initArgs(args, entity, whereClause, parameter);

        String selectQuery = generateSelect(args);

        return new JPQLNamedListQuery<En>(name, selectQuery);
    }

    @Override
    public NamedMapQuery makeMap(String name, Object entity, Where whereClause, QueryParameterWithFocus parameter) {
        Args args = new Args();
        parameter = validateQueryParameterWithFocus(entity, parameter);
        String selectQuery = mapInit(args, entity, whereClause, parameter);

        return new JPQLNamedMapQuery(name, selectQuery, parameter);
    }

    @Override
    public NamedUpdateQuery makeUpdate(String name, Object entity, Where whereClause, UpdateEntry[] entries) {
        Args args = new Args();
        updateInit(args, entity, whereClause, entries);
        return new JPQLNamedUpdateQuery(name, args.sb.toString());
    }

    @Override
    public NamedUpdateQuery makeDelete(String name, Object entity, Where whereClause) {
        Args args = new Args();
        removeInit(args, entity, whereClause);
        return new JPQLNamedUpdateQuery(name, args.sb.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <En> List<En> runNamedListQuery(NamedListQuery<En> query) throws IllegalArgumentException {
        Query q = entityManager.createQuery(((JPQLNamedListQuery<En>) query).queryString);
        setConstants(q, query.getConstants());
        return (List<En>) q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> runNamedMapQuery(NamedMapQuery query) throws IllegalArgumentException {
        Query q = entityManager.createQuery(((JPQLNamedMapQuery) query).selectQuery);
        setConstants(q, query.getConstants());
        return listToMap(q.getResultList(), ((JPQLNamedMapQuery) query).parameter);
    }

    @Override
    public void runNamedUpdateQuery(NamedUpdateQuery query) throws IllegalArgumentException {
        Query q = entityManager.createQuery(((JPQLNamedUpdateQuery) query).updateQuery);
        setConstants(q, query.getConstants());
        q.executeUpdate();
    }

    @Override
    public void execute(String query) {
        entityManager.createQuery(query).executeUpdate();
    }
}
