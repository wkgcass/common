package net.cassite.daf4j;

import junit.framework.TestCase;
import net.cassite.daf4j.stream.QueryProjectionStream;
import net.cassite.daf4j.stream.QueryStream;
import net.cassite.daf4j.stream.StreamTestUtils;
import net.cassite.daf4j.util.Selectable;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static net.cassite.daf4j.ConditionTypes.*;
import static net.cassite.daf4j.ExpressionTypes.*;
import static net.cassite.daf4j.Functions.*;

/**
 * Created by wkgcass on 15/10/26.
 */
public class GeneralTest extends TestCase {
        private static Query query;
        private static Entity entity;

        @Before
        public void setUp() throws Exception {
                query = new Query(new DataAccessEmptyImpl());
                entity = new Entity();
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


                PreResult<Entity> preResult = query.from(entity).where(null);

                testPreResultEquality(preResult, null, new HashMap<QueryParameterTypes, Object[]>());
        }

        @Test
        public void testOrderBy() throws Exception {
                assertEquals(DataAccessEmptyImpl.class, query.dataAccess.getClass());


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


                PreResult<Entity> preResult = query.from(entity).where(null).param(new QueryParameter().limit(1, 10));

                Map<QueryParameterTypes, Object[]> map = new HashMap<QueryParameterTypes, Object[]>();
                map.put(QueryParameterTypes.limit, new Object[]{1, 10});
                testPreResultEquality(preResult, null, map);
        }

        @Test
        public void testTop() throws Exception {
                assertEquals(DataAccessEmptyImpl.class, query.dataAccess.getClass());


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

                Condition condition = entity.age.$eq(10);

                testConditionEquality(condition, entity.age, eq, 10);
        }

        @Test
        public void testNe() throws Exception {

                Condition condition = entity.age.$ne(10);

                testConditionEquality(condition, entity.age, ne, 10);
        }

        @Test
        public void testGt() throws Exception {

                Condition condition = entity.age.$gt(10);

                testConditionEquality(condition, entity.age, gt, 10);
        }

        @Test
        public void testGe() throws Exception {

                Condition condition = entity.age.$ge(10);

                testConditionEquality(condition, entity.age, ge, 10);
        }

        @Test
        public void testLt() throws Exception {

                Condition condition = entity.age.$lt(10);

                testConditionEquality(condition, entity.age, lt, 10);
        }

        @Test
        public void testLe() throws Exception {

                Condition condition = entity.age.$le(10);

                testConditionEquality(condition, entity.age, le, 10);
        }

        @Test
        public void testBetweeen() throws Exception {

                Condition condition = entity.age.between(10, 20);

                testConditionEquality(condition, entity.age, between, 10, 20);
        }

        @Test
        public void testLike() throws Exception {

                Condition condition = entity.name.like("name");

                testConditionEquality(condition, entity.name, like, (Object) new Object[]{"name"});
        }

        @Test
        public void testIsNull() throws Exception {

                Condition condition = entity.age.isNull();

                testConditionEquality(condition, entity.age, isNull);
        }

        @Test
        public void testIsNotNull() throws Exception {

                Condition condition = entity.age.isNotNull();

                testConditionEquality(condition, entity.age, isNotNull);
        }

        @Test
        public void testIn() throws Exception {

                PreResult<?> r = query.from(entity).where(null);
                Condition condition = entity.age.in(r);

                testConditionEquality(condition, entity.age, in, r);
        }

        @Test
        public void testNotIn() throws Exception {

                PreResult<?> r = query.from(entity).where(null);
                Condition condition = entity.age.notIn(r);

                testConditionEquality(condition, entity.age, notIn, r);
        }

        @Test
        public void testMember() throws Exception {

                Condition condition = entity.age.member(entity.oneToMany);

                testConditionEquality(condition, entity.age, member, entity.oneToMany);
        }

        @Test
        public void testNotMember() throws Exception {

                Condition condition = entity.age.notMember(entity.oneToMany);

                testConditionEquality(condition, entity.age, notMember, entity.oneToMany);
        }

        @Test
        public void testReverseMember() throws Exception {

                Condition condition = entity.oneToMany.reverseMember(entity.age);

                testConditionEquality(condition, entity.oneToMany, reverseMember, entity.age);
        }

        @Test
        public void testReverseNotMember() throws Exception {

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

                IExpression exp = entity.age.add(5);

                testExpEquality(exp, add, entity.age, 5);
        }

        @Test
        public void testMinus() throws Exception {

                IExpression exp = entity.age.minus(5);

                testExpEquality(exp, minus, entity.age, 5);
        }

        @Test
        public void testMultiple() throws Exception {

                IExpression exp = entity.age.multi(5);

                testExpEquality(exp, multi, entity.age, 5);
        }

        @Test
        public void testDivide() throws Exception {

                IExpression exp = entity.age.divide(5);

                testExpEquality(exp, divide, entity.age, 5);
        }

        @Test
        public void testMod() throws Exception {

                IExpression exp = entity.age.mod(5);

                testExpEquality(exp, mod, entity.age, 5);
        }

        @Test
        public void testReverseMod() throws Exception {

                IExpression exp = entity.age.reverseMod(5);

                testExpEquality(exp, mod, 5, entity.age);
        }

        @Test
        public void testReverseMinus() throws Exception {

                IExpression exp = entity.age.reverseMinus(5);

                testExpEquality(exp, minus, 5, entity.age);
        }

        @Test
        public void testReverseDivide() throws Exception {

                IExpression exp = entity.age.reverseDivide(5);

                testExpEquality(exp, divide, 5, entity.age);
        }

        @Test
        public void testSum() throws Exception {

                IExpression exp = sum(entity.age);

                testExpEquality(exp, sum, entity.age);
        }

        @Test
        public void testAvg() throws Exception {

                IExpression exp = avg(entity.age);

                testExpEquality(exp, avg, entity.age);
        }

        @Test
        public void testCount() throws Exception {

                IExpression exp = count(entity.age);

                testExpEquality(exp, count, entity.age);
        }

        @Test
        public void testMax() throws Exception {

                IExpression exp = max(entity.age);

                testExpEquality(exp, max, entity.age);
        }

        @Test
        public void testMin() throws Exception {

                IExpression exp = min(entity.age);

                testExpEquality(exp, min, entity.age);
        }

        @Test
        public void testExist() throws Exception {

                PreResult<?> r = query.from(entity).where(null);
                IExpression exp = exists(r);

                testExpEquality(exp, exists, r);
        }

        @Test
        public void testNotExist() throws Exception {

                PreResult<?> r = query.from(entity).where(null);
                IExpression exp = notExists(r);

                testExpEquality(exp, notExists, r);
        }

        @Test
        public void testUnaryNegative() throws Exception {

                IExpression exp = entity.age.unary_negative();

                testExpEquality(exp, unary_negative, entity.age);
        }

        @Test
        public void testConcat1() throws Exception {

                IExpression exp = concat(entity.name, "1");

                testExpEquality(exp, concat, entity.name, "1");
        }

        @Test
        public void testConcat2() throws Exception {

                IExpression exp = concat("1", entity.name);

                testExpEquality(exp, concat, "1", entity.name);
        }

        @Test
        public void testConcat3() throws Exception {

                IExpression exp = concat(entity.district, entity.name);

                testExpEquality(exp, concat, entity.district, entity.name);
        }

        @Test
        public void testSubstring() throws Exception {

                IExpression exp = substring(entity.district, 1, 2);

                testExpEquality(exp, substring, entity.district, 1, 2);
        }

        @Test
        public void testTrim() throws Exception {

                IExpression exp = trim(entity.district);

                testExpEquality(exp, trim, entity.district);
        }

        @Test
        public void testLower() throws Exception {

                IExpression exp = lower(entity.district);

                testExpEquality(exp, lower, entity.district);
        }

        @Test
        public void testUpper() throws Exception {

                IExpression exp = upper(entity.district);

                testExpEquality(exp, upper, entity.district);
        }

        @Test
        public void testLocate1() throws Exception {

                IExpression exp = locate(entity.district, "a");

                testExpEquality(exp, locate, entity.district, "a");
        }

        @Test
        public void testLocate2() throws Exception {

                IExpression exp = locate(entity.district, entity.name);

                testExpEquality(exp, locate, entity.district, entity.name);
        }

        @Test
        public void testLocate3() throws Exception {

                IExpression exp = locate("a", entity.name);

                testExpEquality(exp, locate, "a", entity.name);
        }

        @Test
        public void testlength() throws Exception {

                IExpression exp = length(entity.district);

                testExpEquality(exp, length, entity.district);
        }

        @Test
        public void testQueryParameterWithFocus() throws Exception {

                QueryParameterWithFocus qpwf = new QueryParameterWithFocus(new QueryParameter(), new Focus().focus(entity.id).focus(entity.name, "AliasName"));
                qpwf.focus(entity.age);

                assertEquals(0, qpwf.parameters.size());
                Map<IData<?>, Object> map = new HashMap<IData<?>, Object>();
                map.put(entity.id, "Entity.id");
                map.put(entity.name, "AliasName");
                map.put(entity.age, "Entity.age");
                for (Object d : qpwf.focusMap.keySet()) {
                        assertTrue(map.containsKey(d));
                        assertEquals(map.get(d), qpwf.focusMap.get(d));
                }
        }

        @Test
        public void testUpdateEntry() throws Exception {


                UpdateEntry entry = entity.age.as(1);

                assertEquals(entity.age, entry.data);
                assertEquals(1, entry.updateValue);
        }

        @Test
        public void testIExpressionEquals() {


                assertEquals(sum(entity.age), sum(entity.age));
                assertNotSame(avg(entity.age), sum(entity.age));
        }

        @Test
        public void testAndOrGeneral() throws Exception {

                PreResult<?> r = query.from(entity).where(null);

                And and = entity.age.$gt(5).and(entity.name.$ne("abc").or(entity.id.$lt(8)).and(entity.district.isNull())).and(exists(r));
                assertTrue(and.getConditionList().contains(entity.age.$gt(5)));
                assertTrue(and.getConditionList().contains(entity.district.isNull()));
                assertTrue(and.getExpBoolList().contains(exists(r)));
                assertTrue(and.getOrList().get(0).getConditionList().contains(entity.name.$ne("abc")));
                assertTrue(and.getOrList().get(0).getConditionList().contains(entity.id.$lt(8)));
        }

        @Test
        public void testConditionInvokeAndCondition() throws Exception {


                And where = entity.age.$gt(10).and(entity.name.$ne("abc"));
                assertTrue(where.getConditionList().contains(entity.age.$gt(10)));
                assertTrue(where.getConditionList().contains(entity.name.$ne("abc")));
        }

        @Test
        public void testConditionInvokeOrCondition() throws Exception {


                Or where = entity.age.$gt(10).or(entity.name.$ne("abc"));
                assertTrue(where.getConditionList().contains(entity.age.$gt(10)));
                assertTrue(where.getConditionList().contains(entity.name.$ne("abc")));
        }

        @Test
        public void testConditionInvokeAndExp() throws Exception {

                PreResult<?> r = query.from(entity).where(null);

                And where = entity.age.$gt(10).and(exists(r));
                assertTrue(where.getConditionList().contains(entity.age.$gt(10)));
                assertTrue(where.getExpBoolList().contains(exists(r)));
        }

        @Test
        public void testConditionInvokeOrExp() throws Exception {

                PreResult<?> r = query.from(entity).where(null);

                Or where = entity.age.$gt(10).or(exists(r));
                assertTrue(where.getConditionList().contains(entity.age.$gt(10)));
                assertTrue(where.getExpBoolList().contains(exists(r)));
        }

        @Test
        public void testExpInvokeAndCondition() throws Exception {

                PreResult<?> r = query.from(entity).where(null);

                And where = exists(r).and(entity.age.$gt(10));
                assertTrue(where.getConditionList().contains(entity.age.$gt(10)));
                assertTrue(where.getExpBoolList().contains(exists(r)));
        }

        @Test
        public void testExpInvokeOrCondition() throws Exception {

                PreResult<?> r = query.from(entity).where(null);

                Or where = exists(r).or(entity.age.$gt(10));
                assertTrue(where.getConditionList().contains(entity.age.$gt(10)));
                assertTrue(where.getExpBoolList().contains(exists(r)));
        }

        @Test
        public void testConditionInvokeAndAnd() throws Exception {


                And where = entity.age.$gt(10).and(entity.name.$ne("abc").and(entity.district.isNotNull()));
                assertTrue(where.getConditionList().contains(entity.age.$gt(10)));
                assertTrue(where.getConditionList().contains(entity.name.$ne("abc")));
                assertTrue(where.getConditionList().contains(entity.district.isNotNull()));
        }

        @Test
        public void testConditionInvokeOrAnd() throws Exception {


                Or where = entity.age.$gt(10).or(entity.name.$ne("abc").and(entity.district.isNotNull()));
                assertTrue(where.getConditionList().contains(entity.age.$gt(10)));
                assertTrue(where.getAndList().get(0).getConditionList().contains(entity.name.$ne("abc")));
                assertTrue(where.getAndList().get(0).getConditionList().contains(entity.district.isNotNull()));

        }

        @Test
        public void testQueryStreamGeneral() throws Exception {
                Entity entity = new Entity();
                QueryStream<Entity> stream = query.stream(entity).filter(entity.age.$gt(18)).sort(entity.age.asc()).limit(1, 20);

                assertEquals(entity, StreamTestUtils.getEntity(stream));
                assertEquals(entity.age.$gt(18), StreamTestUtils.getAndOr(stream));
                assertTrue(StreamTestUtils.getDataAccess(stream).getClass() == DataAccessEmptyImpl.class);

                QueryParameter parameter = StreamTestUtils.getParameter(stream);
                Map<QueryParameterTypes, Object[]> args = new HashMap<QueryParameterTypes, Object[]>();
                args.put(QueryParameterTypes.limit, new Object[]{1, 20});
                args.put(QueryParameterTypes.orderBy, new Object[]{entity.age.asc()});
                for (QueryParameterTypes type : parameter.parameters.keySet()) {
                        assertTrue(args.containsKey(type));
                        Object[] expectedObjs = args.get(type);
                        Object[] objs = parameter.parameters.get(type);
                        assertEquals(expectedObjs.length, objs.length);
                        for (int i = 0; i < expectedObjs.length; ++i) {
                                assertEquals(expectedObjs[i], objs[i]);
                        }
                }
        }

        @Test
        public void testQueryStreamTop() throws Exception {
                Entity entity = new Entity();
                QueryStream<Entity> stream = query.stream(entity).filter(entity.age.$gt(18)).sort(entity.age.asc()).limit(1);

                QueryParameter parameter = StreamTestUtils.getParameter(stream);
                Map<QueryParameterTypes, Object[]> args = new HashMap<QueryParameterTypes, Object[]>();
                args.put(QueryParameterTypes.top, new Object[]{1});
                args.put(QueryParameterTypes.orderBy, new Object[]{entity.age.asc()});
                for (QueryParameterTypes type : parameter.parameters.keySet()) {
                        assertTrue(args.containsKey(type));
                        Object[] expectedObjs = args.get(type);
                        Object[] objs = parameter.parameters.get(type);
                        assertEquals(expectedObjs.length, objs.length);
                        for (int i = 0; i < expectedObjs.length; ++i) {
                                assertEquals(expectedObjs[i], objs[i]);
                        }
                }
        }

        @Test
        public void testQueryStreamNonParameter() throws Exception {
                Entity entity = new Entity();
                QueryStream<Entity> stream = query.stream(entity).filter(entity.age.$gt(18));

                assertEquals(null, StreamTestUtils.getParameter(stream));
        }

        @Test
        public void testQueryProjectionStream() throws Exception {
                Entity entity = new Entity();
                QueryProjectionStream<Entity> stream = query.stream(entity).filter(entity.age.$gt(18)).map(new Focus().focus(entity.name));

                Map<Selectable, String> map = new HashMap<Selectable, String>();
                map.put(entity.name, "Entity.name");
                for (Selectable selectable : ((QueryParameterWithFocus) StreamTestUtils.getParameter(stream)).focusMap.keySet()) {
                        String alias = ((QueryParameterWithFocus) StreamTestUtils.getParameter(stream)).focusMap.get(selectable);
                        assertTrue(map.containsKey(selectable));

                        assertEquals(map.get(selectable), alias);
                }
        }
}
