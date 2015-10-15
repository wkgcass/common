package test;

import static net.cassite.pure.data.Functions.*;

import net.cassite.pure.data.Query;
import net.cassite.pure.data.jpa.JPQLDataAccess;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wkgcass on 15/10/10.
 */
public class Test {
    public static void main(String[] args) {
        Query query = new Query(new JPQLDataAccess(new EntityManager() {
            @Override
            public void persist(Object o) {

            }

            @Override
            public <T> T merge(T t) {
                return null;
            }

            @Override
            public void remove(Object o) {

            }

            @Override
            public <T> T find(Class<T> aClass, Object o) {
                return null;
            }

            @Override
            public <T> T getReference(Class<T> aClass, Object o) {
                return null;
            }

            @Override
            public void flush() {

            }

            @Override
            public void setFlushMode(FlushModeType flushModeType) {

            }

            @Override
            public FlushModeType getFlushMode() {
                return null;
            }

            @Override
            public void lock(Object o, LockModeType lockModeType) {

            }

            @Override
            public void refresh(Object o) {

            }

            @Override
            public void clear() {

            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public javax.persistence.Query createQuery(String s) {
                return new javax.persistence.Query() {
                    @Override
                    public List getResultList() {
                        return null;
                    }

                    @Override
                    public Object getSingleResult() {
                        return null;
                    }

                    @Override
                    public int executeUpdate() {
                        return 0;
                    }

                    @Override
                    public javax.persistence.Query setMaxResults(int i) {
                        return null;
                    }

                    @Override
                    public javax.persistence.Query setFirstResult(int i) {
                        return null;
                    }

                    @Override
                    public javax.persistence.Query setHint(String s, Object o) {
                        return null;
                    }

                    @Override
                    public javax.persistence.Query setParameter(String s, Object o) {
                        return null;
                    }

                    @Override
                    public javax.persistence.Query setParameter(String s, Date date, TemporalType temporalType) {
                        return null;
                    }

                    @Override
                    public javax.persistence.Query setParameter(String s, Calendar calendar, TemporalType temporalType) {
                        return null;
                    }

                    @Override
                    public javax.persistence.Query setParameter(int i, Object o) {
                        return null;
                    }

                    @Override
                    public javax.persistence.Query setParameter(int i, Date date, TemporalType temporalType) {
                        return null;
                    }

                    @Override
                    public javax.persistence.Query setParameter(int i, Calendar calendar, TemporalType temporalType) {
                        return null;
                    }

                    @Override
                    public javax.persistence.Query setFlushMode(FlushModeType flushModeType) {
                        return null;
                    }
                };
            }

            @Override
            public javax.persistence.Query createNamedQuery(String s) {
                return null;
            }

            @Override
            public javax.persistence.Query createNativeQuery(String s) {
                return null;
            }

            @Override
            public javax.persistence.Query createNativeQuery(String s, Class aClass) {
                return null;
            }

            @Override
            public javax.persistence.Query createNativeQuery(String s, String s1) {
                return null;
            }

            @Override
            public void joinTransaction() {

            }

            @Override
            public Object getDelegate() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public EntityTransaction getTransaction() {
                return null;
            }
        }));

        User user = new User();
        Role role = new Role();
        user.getRoles().add(role);
        role.getUsers().add(user);

        // test > <>
        query.from(user).where(user.age.$gt(15).and(role.name.$ne(user.name))).list();

        // test member
        query.from(role).where(user.age.$gt(18).and(role.id.member(user.roles))).list();

        // test exists and sub query
        query.from(user).where(exists(query.from(role).where(role.id.$ne(1).and(user.id.$ne(5))))).list();
    }
}
