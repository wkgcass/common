package net.cassite.daf4j.jpa;

import junit.framework.TestCase;
import net.cassite.daf4j.Query;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

import static net.cassite.daf4j.Functions.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.*;

/**
 * Created by wkgcass on 15/10/26.
 */
public class TestJPQLDataAccess {
        private static EntityManagerFactory factory;

        private EntityManager manager;
        private Query query;

        private static User userVcassv;
        private static User userWkgcass;
        private static Clinic clinicUjs;
        private static Clinic clinicJKD;
        private static Outpatient opDHR_ujs_vcassv;
        private static Outpatient opSXH_ujs_vcassv;
        private static Patient pSxh;
        private static Patient pDhr;

        @BeforeClass
        public static void classSetUp() throws Exception {
                factory = Persistence.createEntityManagerFactory("mysqlJPA");
                // configure mysqlJPA hbm2ddl.auto as 'create'
                EntityManager manager = factory.createEntityManager();
                Query query = new Query(new JPQLDataAccess(manager));

                // 名称为vcassv的用户
                userVcassv = new User();
                userVcassv.setAddtime(new Date());
                userVcassv.setGender("male");
                userVcassv.setName("cass");
                userVcassv.setPassword("123456");
                userVcassv.setUsername("vcassv");
                userVcassv.setPhone(" 15751xxYYYY ");

                // 名称为wkgcass的用户
                userWkgcass = new User();
                userWkgcass.setAddtime(new Date());
                userWkgcass.setGender("male");
                userWkgcass.setName("wkg");
                userWkgcass.setPassword("654321");
                userWkgcass.setUsername("wkgcass");

                // 江苏大学医务室
                clinicUjs = new Clinic();
                clinicUjs.setAddress("江苏大学");
                clinicUjs.setAddtime(new Date());
                clinicUjs.setLeader("Dr.G");
                clinicUjs.setName("江大校医院");
                clinicUjs.getUsers().add(userVcassv);

                userVcassv.setClinic(clinicUjs);

                // 江科大医务室
                clinicJKD = new Clinic();
                clinicJKD.setAddress("江苏科技大学");
                clinicJKD.setAddtime(new Date());
                clinicJKD.setLeader("Dr.XX");
                clinicJKD.setName("江科大校医室");
                clinicJKD.getUsers().add(userWkgcass);

                userWkgcass.setClinic(clinicJKD);

                // 门诊记录
                opDHR_ujs_vcassv = new Outpatient();
                opDHR_ujs_vcassv.setAddtime(new Date());
                opDHR_ujs_vcassv.setAdvice("放弃治疗吧");
                opDHR_ujs_vcassv.setAppointment(new Date());
                opDHR_ujs_vcassv.setClinic(clinicUjs);
                opDHR_ujs_vcassv.setJudgenote("你已经死了");
                opDHR_ujs_vcassv.setPrice(1000);
                opDHR_ujs_vcassv.setTreatnote("没救了");
                opDHR_ujs_vcassv.setUser(userVcassv);

                // SXH的门诊记录
                opSXH_ujs_vcassv = new Outpatient();
                opSXH_ujs_vcassv.setAddtime(new Date());
                opSXH_ujs_vcassv.setAdvice("快死了");
                opSXH_ujs_vcassv.setAppointment(new Date());
                opSXH_ujs_vcassv.setClinic(clinicUjs);
                opSXH_ujs_vcassv.setJudgenote("老坑队友");
                opSXH_ujs_vcassv.setPrice(99999);
                opSXH_ujs_vcassv.setTreatnote("xxx");
                opSXH_ujs_vcassv.setUser(userVcassv);

                pSxh = new Patient();

                opSXH_ujs_vcassv.setPatient(pSxh);

                pSxh.setAddress("江苏大学x区x栋6x3");
                pSxh.setAddtime(new Date());
                pSxh.setAge(22);
                pSxh.setClinic(clinicUjs);
                pSxh.setGender("male");
                pSxh.setName("SXH");
                pSxh.setPhone("1575100xxxx");
                pSxh.setSn("sxh");

                // 病人信息(姓名:DHR)
                pDhr = new Patient();

                opDHR_ujs_vcassv.setPatient(pDhr);

                pDhr.setAddress("江苏大学x区x栋6x0");
                pDhr.setAddtime(new Date());
                pDhr.setAge(21);
                pDhr.setClinic(clinicUjs);
                pDhr.setGender("male");
                pDhr.setName("DHR");
                pDhr.setSn("dhr");

                EntityTransaction tx = manager.getTransaction();
                tx.begin();
                query.save(clinicUjs, userVcassv, clinicJKD, userWkgcass, pDhr, opDHR_ujs_vcassv, pSxh, opSXH_ujs_vcassv);
                tx.commit();

                manager.close();
        }

        @Before
        public void setUp() throws Exception {
                manager = factory.createEntityManager();
                query = new Query(new JPQLDataAccess(manager));
        }

        @org.junit.Test
        public void testListAll() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(null).list();
                assertEquals(2, list.size());
                User test0;
                User test1;
                if (list.get(0).getName().equals(userVcassv.getName())) {
                        test0 = list.get(0);
                        test1 = list.get(1);
                } else {
                        test0 = list.get(1);
                        test1 = list.get(0);
                }

                // assert retrieved data
                assertEquals(userVcassv.getPassword(), test0.getPassword());
                assertEquals(userWkgcass.getPassword(), test1.getPassword());
        }

        @org.junit.Test
        public void testListEq() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(user.password.$eq(userVcassv.getPassword())).list();
                assertEquals(1, list.size());

                assertEquals(userVcassv.getName(), list.get(0).getName());
        }

        @org.junit.Test
        public void testListNe() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(user.password.$ne(userVcassv.getPassword())).list();
                assertEquals(1, list.size());

                assertEquals(userWkgcass.getName(), list.get(0).getName());
        }

        @org.junit.Test
        public void testListGt() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.$gt(5000)).list();
                assertEquals(1, list.size());

                assertEquals(opSXH_ujs_vcassv.getPrice(), list.get(0).getPrice());
        }

        @org.junit.Test
        public void testListLt() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.$lt(5000)).list();
                assertEquals(1, list.size());

                assertEquals(opDHR_ujs_vcassv.getPrice(), list.get(0).getPrice());
        }

        @org.junit.Test
        public void testListGe() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.$ge(opSXH_ujs_vcassv.getPrice())).list();
                assertEquals(1, list.size());

                assertEquals(opSXH_ujs_vcassv.getPrice(), list.get(0).getPrice());
        }

        @org.junit.Test
        public void testListLe() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.$le(opDHR_ujs_vcassv.getPrice())).list();
                assertEquals(1, list.size());

                assertEquals(opDHR_ujs_vcassv.getPrice(), list.get(0).getPrice());
        }

        @org.junit.Test
        public void testListBetween() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.between(5000, 100000)).list();
                assertEquals(1, list.size());

                assertEquals(opSXH_ujs_vcassv.getPrice(), list.get(0).getPrice());
        }

        @org.junit.Test
        public void testListIn() throws Exception {
                Outpatient outpatient = new Outpatient();
                User user = new User();
                List<Outpatient> list = query.from(outpatient).where(outpatient.user.in(query.from(user).where(user.name.$eq(userVcassv.getName())))).list();
                assertEquals(2, list.size());

                list = query.from(outpatient).where(outpatient.user.in(query.from(user).where(user.name.$eq(userWkgcass.getName())))).list();
                assertEquals(0, list.size());
        }

        @org.junit.Test
        public void testListNotIn() throws Exception {
                Outpatient outpatient = new Outpatient();
                User user = new User();
                List<Outpatient> list = query.from(outpatient).where(outpatient.user.notIn(query.from(user).where(user.name.$eq(userWkgcass.getName())))).list();
                assertEquals(2, list.size());

                list = query.from(outpatient).where(outpatient.user.notIn(query.from(user).where(user.name.$eq(userVcassv.getName())))).list();
                assertEquals(0, list.size());
        }

        @org.junit.Test
        public void testListLike() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(user.name.like("%as%")).list();
                TestCase.assertEquals(1, list.size());

                assertEquals(userVcassv.getName(), list.get(0).getName());
        }

        @org.junit.Test
        public void testListIsNull() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(user.phone.isNull()).list();

                TestCase.assertEquals(1, list.size());

                TestCase.assertEquals(userWkgcass.getName(), list.get(0).getName());
        }

        @org.junit.Test
        public void testListIsNotNull() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(user.phone.isNotNull()).list();

                TestCase.assertEquals(1, list.size());

                TestCase.assertEquals(userVcassv.getName(), list.get(0).getName());
        }

        @org.junit.Test
        public void testListSum() throws Exception {
                User user = new User();
                Outpatient outpatient = new Outpatient();
                user.getOutpatients().add(outpatient);
                List<User> list = query.from(user).where(sum(outpatient.price).$eq(100999L)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getName(), list.get(0).getName());
        }

        @org.junit.Test
        public void testListAvg() throws Exception {
                User user = new User();
                Outpatient outpatient = new Outpatient();
                user.getOutpatients().add(outpatient);
                List<User> list = query.from(user).where(sum(outpatient.price).$gt(50000L)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getName(), list.get(0).getName());
        }

        @Test
        public void testListCount() throws Exception {
                User user = new User();
                Outpatient outpatient = new Outpatient();
                user.getOutpatients().add(outpatient);

                List<User> list = query.from(user).where(count(outpatient.id).$eq(2L)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getName(), list.get(0).getName());
        }

        @Test
        public void testListMax() throws Exception {
                User user = new User();
                Outpatient outpatient = new Outpatient();
                user.getOutpatients().add(outpatient);

                List<User> list = query.from(user).where(max(outpatient.price).$eq(99999)).list();
                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getName(), list.get(0).getName());
        }

        @Test
        public void testListMin() throws Exception {
                User user = new User();
                Outpatient outpatient = new Outpatient();
                user.getOutpatients().add(outpatient);

                List<User> list = query.from(user).where(min(outpatient.price).$eq(1000)).list();
                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getName(), list.get(0).getName());
        }

        @Test
        public void testListAdd() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.add(15).$eq(1015)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(opDHR_ujs_vcassv.getJudgenote(), list.get(0).getJudgenote());
        }

        @Test
        public void testListMinus() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.minus(15).$eq(985)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(opDHR_ujs_vcassv.getJudgenote(), list.get(0).getJudgenote());
        }

        @Test
        public void testListMulti() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.multi(2).$eq(2000)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(opDHR_ujs_vcassv.getJudgenote(), list.get(0).getJudgenote());
        }

        @Test
        public void testListDivide() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.divide(2).$eq(500)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(opDHR_ujs_vcassv.getJudgenote(), list.get(0).getJudgenote());
        }

        @Test
        public void testListMod() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.mod(3).$eq(1)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(opDHR_ujs_vcassv.getJudgenote(), list.get(0).getJudgenote());
        }

        @Test
        public void testListReverseMinus() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.reverseMinus(1001).$eq(1)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(opDHR_ujs_vcassv.getJudgenote(), list.get(0).getJudgenote());
        }

        @Test
        public void testListReverseDivide() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.reverseDivide(2000).$eq(2)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(opDHR_ujs_vcassv.getJudgenote(), list.get(0).getJudgenote());
        }

        @Test
        public void testListReverseMod() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.reverseMod(1024).$eq(24)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(opDHR_ujs_vcassv.getJudgenote(), list.get(0).getJudgenote());
        }

        @Test
        public void testListUnaryNegative() throws Exception {
                Outpatient outpatient = new Outpatient();
                List<Outpatient> list = query.from(outpatient).where(outpatient.price.unary_negative().$eq(-1000)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(opDHR_ujs_vcassv.getJudgenote(), list.get(0).getJudgenote());
        }

        @Test
        public void testListConcat() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(concat(user.name, "abc").$eq("wkgabc")).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userWkgcass.getName(), list.get(0).getName());
        }

        @Test
        public void testListSubstring() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(substring(user.name, 1, 2).$eq("wk")).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userWkgcass.getName(), list.get(0).getName());
        }

        @Test
        public void testListLower() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(lower(user.phone).$eq(" 15751xxyyyy ")).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getPhone(), list.get(0).getPhone());
        }

        @Test
        public void testListUpper() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(upper(user.phone).$eq(" 15751XXYYYY ")).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getPhone(), list.get(0).getPhone());
        }

        @Test
        public void testListTrim() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(trim(user.phone).$eq("15751xxYYYY")).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getPhone(), list.get(0).getPhone());
        }

        @Test
        public void testListLength() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(length(user.phone).$eq(13)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getPhone(), list.get(0).getPhone());
        }

        @Test
        public void testListLocate() throws Exception {
                User user = new User();
                List<User> list = query.from(user).where(locate("xx", user.phone).$eq(7)).list();

                TestCase.assertEquals(1, list.size());
                TestCase.assertEquals(userVcassv.getPhone(), list.get(0).getPhone());
        }

        @Test
        public void testListExists() throws Exception {
                User user = new User();
                Outpatient outpatient = new Outpatient();
                user.getOutpatients().add(outpatient);
                List<User> list = query.from(user).where(exists(query.from(user).where(user.name.$eq("cass").and(outpatient.user.$eq(user.id))))).list();

                TestCase.assertEquals(2, list.size());
        }

        @Test
        public void testListNotExists() throws Exception {
                User user = new User();
                Outpatient outpatient = new Outpatient();
                user.getOutpatients().add(outpatient);
                List<User> list = query.from(user).where(notExists(query.from(user).where(user.name.$eq("wkg").and(outpatient.id.member(user.outpatients))))).list();

                TestCase.assertEquals(2, list.size());
        }

        @After
        public void tearDown() {
                manager.close();
        }

        @AfterClass
        public static void classTearDown() throws Exception {
                TestJPQLUpdate t = new TestJPQLUpdate(factory);
                t.testUpdate();
                t.testRemove();
        }
}
