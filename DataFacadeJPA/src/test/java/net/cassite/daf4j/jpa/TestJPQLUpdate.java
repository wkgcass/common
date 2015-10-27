package net.cassite.daf4j.jpa;

import junit.framework.TestCase;
import net.cassite.daf4j.Query;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * Created by wkgcass on 15/10/27.
 */
public class TestJPQLUpdate {
        private EntityManager manager;
        private Query query;

        public TestJPQLUpdate(EntityManagerFactory factory) {
                manager = factory.createEntityManager();
                query = new Query(new JPQLDataAccess(manager));
        }

        public void testUpdate() throws Exception {
                EntityTransaction tx = manager.getTransaction();
                tx.begin();
                User user = new User();
                query.from(user).where(user.name.$eq("wkg")).set(user.name.as("wkgwkg"));
                tx.commit();

                manager.clear();
                TestCase.assertEquals(1, query.from(user).where(user.name.$eq("wkgwkg")).list().size());
        }

        public void testRemove() throws Exception {
                EntityTransaction tx = manager.getTransaction();
                tx.begin();
                User user = new User();
                Outpatient outpatient = new Outpatient();
                outpatient.setUser(user);
                query.from(outpatient).where(outpatient.user.in(query.from(user).where(user.name.$eq("cass")))).remove();
                tx.commit();

                manager.clear();
                TestCase.assertEquals(0, query.from(outpatient).where(outpatient.user.in(query.from(user).where(user.name.$eq("cass")))).list().size());
        }
}
