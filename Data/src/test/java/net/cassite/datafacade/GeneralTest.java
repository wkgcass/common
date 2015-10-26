package net.cassite.datafacade;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static net.cassite.datafacade.ConditionTypes.*;
import static net.cassite.datafacade.ExpressionTypes.*;
import static net.cassite.datafacade.Functions.*;

/**
 * Created by wkgcass on 15/10/26.
 */
public class GeneralTest extends TestCase {
        private static Query query;

        @Before
        public void setUp() throws Exception {
                query = new Query(new DataAccessEmptyImpl());
        }

        private Entity getEntity() {
                return new Entity();
        }

        private void testPreResultEquality(PreResult<Entity> preRes, Where whereClause, Map<QueryParameterTypes, Object[]> args) {
                assertEquals(whereClause, preRes.whereClause);
                if (preRes.parameter != null) {
                        for (QueryParameterTypes type : preRes.parameter.parameters.keySet()) {
                                assertTrue(args.containsKey(type));
                                Object[] expectedObjs = args.get(type);
                                Object[] objs = preRes.parameter.parameters.get(type);
                                assertEquals(expectedObjs.length, objs.length);
                                for (int i = 0; i < expectedObjs.length; ++i) {
                                        assertEquals(expectedObjs[i], objs[i]);
                                }
                        }
                }
        }

        @Test
        public void testQuery() throws Exception {
                assertEquals(DataAccessEmptyImpl.class, query.dataAccess.getClass());

                Entity entity = getEntity();
                PreResult<Entity> preResult = query.from(entity).where(null);

                testPreResultEquality(preResult, null, new HashMap<QueryParameterTypes, Object[]>());
        }

        @Test
        public void testOrderBy() throws Exception {
                assertEquals(DataAccessEmptyImpl.class, query.dataAccess.getClass());

                Entity entity = getEntity();

                OrderBase base = entity.age.desc();
                assertEquals(OrderTypes.desc, base.type);
                assertEquals(entity.age, base.data);

                PreResult<Entity> preResult = query.from(entity).where(null).param(new QueryParameter().orderBy(base));

                Map<QueryParameterTypes, Object[]> map = new HashMap<QueryParameterTypes, Object[]>();
                map.put(QueryParameterTypes.orderBy, new Object[]{base});
                testPreResultEquality(preResult, null, map);
        }

        @Test
        public void testLimit() throws Exception {
                assertEquals(DataAccessEmptyImpl.class, query.dataAccess.getClass());

                Entity entity = getEntity();

                PreResult<Entity> preResult = query.from(entity).where(null).param(new QueryParameter().limit(1, 10));

                Map<QueryParameterTypes, Object[]> map = new HashMap<QueryParameterTypes, Object[]>();
                map.put(QueryParameterTypes.limit, new Object[]{1, 10});
                testPreResultEquality(preResult, null, map);
        }

        @Test
        public void testTop() throws Exception {
                assertEquals(DataAccessEmptyImpl.class, query.dataAccess.getClass());

                Entity entity = getEntity();

                PreResult<Entity> preResult = query.from(entity).where(null).param(new QueryParameter().top(5));

                Map<QueryParameterTypes, Object[]> map = new HashMap<QueryParameterTypes, Object[]>();
                map.put(QueryParameterTypes.top, new Object[]{5});
                testPreResultEquality(preResult, null, map);
        }

        private void testConditionEquality(Condition condition, Parameter parameter, ConditionTypes type, Object... args) {
                assertEquals("条件类型不一致", type, condition.type);
                assertEquals("字段不一致", parameter, condition.data);
                assertEquals("参数个数不一致", args.length, condition.args.size());
                for (int i = 0; i < args.length; ++i) {
                        if (args[i].getClass().isArray()) {
                                Object[] expectedArr = (Object[]) args[i];
                                Object[] arr = (Object[]) condition.args.get(i);
                                assertEquals("第" + i + "个参数是数组,长度不一致", expectedArr.length, arr.length);
                                for (int j = 0; j < expectedArr.length; ++j) {
                                        assertEquals("第" + i + "个参数是数组,第" + j + "个元素不一致", expectedArr[j], arr[j]);
                                }
                        } else {
                                assertEquals("第" + i + "个参数不一致", args[i], condition.args.get(i));
                        }
                }
        }

        @Test
        public void testEq() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.$eq(10);

                testConditionEquality(condition, entity.age, eq, 10);
        }

        @Test
        public void testNe() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.$ne(10);

                testConditionEquality(condition, entity.age, ne, 10);
        }

        @Test
        public void testGt() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.$gt(10);

                testConditionEquality(condition, entity.age, gt, 10);
        }

        @Test
        public void testGe() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.$ge(10);

                testConditionEquality(condition, entity.age, ge, 10);
        }

        @Test
        public void testLt() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.$lt(10);

                testConditionEquality(condition, entity.age, lt, 10);
        }

        @Test
        public void testLe() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.$le(10);

                testConditionEquality(condition, entity.age, le, 10);
        }

        @Test
        public void testBetweeen() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.between(10, 20);

                testConditionEquality(condition, entity.age, between, 10, 20);
        }

        @Test
        public void testLike() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.name.like("name");

                testConditionEquality(condition, entity.name, like, (Object) new Object[]{"name"});
        }

        @Test
        public void testIsNull() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.isNull();

                testConditionEquality(condition, entity.age, isNull);
        }

        @Test
        public void testIsNotNull() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.isNotNull();

                testConditionEquality(condition, entity.age, isNotNull);
        }

        @Test
        public void testIn() throws Exception {
                Entity entity = getEntity();
                PreResult<?> r = query.from(entity).where(null);
                Condition condition = entity.age.in(r);

                testConditionEquality(condition, entity.age, in, r);
        }

        @Test
        public void testNotIn() throws Exception {
                Entity entity = getEntity();
                PreResult<?> r = query.from(entity).where(null);
                Condition condition = entity.age.notIn(r);

                testConditionEquality(condition, entity.age, notIn, r);
        }

        @Test
        public void testMember() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.member(entity.oneToMany);

                testConditionEquality(condition, entity.age, member, entity.oneToMany);
        }

        @Test
        public void testNotMember() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.age.notMember(entity.oneToMany);

                testConditionEquality(condition, entity.age, notMember, entity.oneToMany);
        }

        @Test
        public void testReverseMember() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.oneToMany.reverseMember(entity.age);

                testConditionEquality(condition, entity.oneToMany, reverseMember, entity.age);
        }

        @Test
        public void testReverseNotMember() throws Exception {
                Entity entity = getEntity();
                Condition condition = entity.oneToMany.reverseNotMember(entity.age);

                testConditionEquality(condition, entity.oneToMany, reverseNotMember, entity.age);
        }

        private void testExpEquality(IExpression exp, ExpressionTypes type, Object... expectedArgs) {
                assertEquals(type, exp.expType());
                Object[] args = exp.expArgs();
                assertEquals(expectedArgs.length, args.length);
                for (int i = 0; i < args.length; ++i) {
                        assertEquals(expectedArgs[i], args[i]);
                }
        }

        @Test
        public void testAdd() throws Exception {
                Entity entity = getEntity();
                IExpression exp = entity.age.add(5);

                testExpEquality(exp, add, entity.age, 5);
        }

        @Test
        public void testMinus() throws Exception {
                Entity entity = getEntity();
                IExpression exp = entity.age.minus(5);

                testExpEquality(exp, minus, entity.age, 5);
        }

        @Test
        public void testMultiple() throws Exception {
                Entity entity = getEntity();
                IExpression exp = entity.age.multi(5);

                testExpEquality(exp, multi, entity.age, 5);
        }

        @Test
        public void testDivide() throws Exception {
                Entity entity = getEntity();
                IExpression exp = entity.age.divide(5);

                testExpEquality(exp, divide, entity.age, 5);
        }

        @Test
        public void testMod() throws Exception {
                Entity entity = getEntity();
                IExpression exp = entity.age.mod(5);

                testExpEquality(exp, mod, entity.age, 5);
        }

        @Test
        public void testReverseMod() throws Exception {
                Entity entity = getEntity();
                IExpression exp = entity.age.reverseMod(5);

                testExpEquality(exp, mod, 5, entity.age);
        }

        @Test
        public void testReverseMinus() throws Exception {
                Entity entity = getEntity();
                IExpression exp = entity.age.reverseMinus(5);

                testExpEquality(exp, minus, 5, entity.age);
        }

        @Test
        public void testReverseDivide() throws Exception {
                Entity entity = getEntity();
                IExpression exp = entity.age.reverseDivide(5);

                testExpEquality(exp, divide, 5, entity.age);
        }

        @Test
        public void testSum() throws Exception {
                Entity entity = getEntity();
                IExpression exp = sum(entity.age);

                testExpEquality(exp, sum, entity.age);
        }

        @Test
        public void testAvg() throws Exception {
                Entity entity = getEntity();
                IExpression exp = avg(entity.age);

                testExpEquality(exp, avg, entity.age);
        }

        @Test
        public void testCount() throws Exception {
                Entity entity = getEntity();
                IExpression exp = count(entity.age);

                testExpEquality(exp, count, entity.age);
        }

        @Test
        public void testMax() throws Exception {
                Entity entity = getEntity();
                IExpression exp = max(entity.age);

                testExpEquality(exp, max, entity.age);
        }

        @Test
        public void testMin() throws Exception {
                Entity entity = getEntity();
                IExpression exp = min(entity.age);

                testExpEquality(exp, min, entity.age);
        }

        @Test
        public void testExist() throws Exception {
                Entity entity = getEntity();
                PreResult<?> r = query.from(entity).where(null);
                IExpression exp = exists(r);

                testExpEquality(exp, exists, r);
        }

        @Test
        public void testNotExist() throws Exception {
                Entity entity = getEntity();
                PreResult<?> r = query.from(entity).where(null);
                IExpression exp = notExists(r);

                testExpEquality(exp, notExists, r);
        }

        @Test
        public void testUnaryNegative() throws Exception {
                Entity entity = getEntity();
                IExpression exp = entity.age.unary_negative();

                testExpEquality(exp, unary_negative, entity.age);
        }

        @Test
        public void testConcat1() throws Exception {
                Entity entity = getEntity();
                IExpression exp = concat(entity.name, "1");

                testExpEquality(exp, concat, entity.name, "1");
        }

        @Test
        public void testConcat2() throws Exception {
                Entity entity = getEntity();
                IExpression exp = concat("1", entity.name);

                testExpEquality(exp, concat, "1", entity.name);
        }

        @Test
        public void testConcat3() throws Exception {
                Entity entity = getEntity();
                IExpression exp = concat(entity.district, entity.name);

                testExpEquality(exp, concat, entity.district, entity.name);
        }

        @Test
        public void testSubstring() throws Exception {
                Entity entity = getEntity();
                IExpression exp = substring(entity.district, 1, 2);

                testExpEquality(exp, substring, entity.district, 1, 2);
        }

        @Test
        public void testTrim() throws Exception {
                Entity entity = getEntity();
                IExpression exp = trim(entity.district);

                testExpEquality(exp, trim, entity.district);
        }

        @Test
        public void testLower() throws Exception {
                Entity entity = getEntity();
                IExpression exp = lower(entity.district);

                testExpEquality(exp, lower, entity.district);
        }

        @Test
        public void testUpper() throws Exception {
                Entity entity = getEntity();
                IExpression exp = upper(entity.district);

                testExpEquality(exp, upper, entity.district);
        }

        @Test
        public void testLocate1() throws Exception {
                Entity entity = getEntity();
                IExpression exp = locate(entity.district, "a");

                testExpEquality(exp, locate, entity.district, "a");
        }

        @Test
        public void testLocate2() throws Exception {
                Entity entity = getEntity();
                IExpression exp = locate(entity.district, entity.name);

                testExpEquality(exp, locate, entity.district, entity.name);
        }

        @Test
        public void testLocate3() throws Exception {
                Entity entity = getEntity();
                IExpression exp = locate("a", entity.name);

                testExpEquality(exp, locate, "a", entity.name);
        }

        @Test
        public void testlength() throws Exception {
                Entity entity = getEntity();
                IExpression exp = length(entity.district);

                testExpEquality(exp, length, entity.district);
        }

}
