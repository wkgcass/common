package net.cassite.pure.data.jpa;

import net.cassite.pure.data.DataAccess;
import net.cassite.pure.data.QueryParameter;
import net.cassite.pure.data.Where;

import java.util.List;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/10.
 */
public class JPQLDataAccess implements DataAccess {

    @Override
    public <En> List<En> list(Class<En> entityClass, Where whereClause, QueryParameter parameter) {
        return null;
    }

    @Override
    public Map<String, Object> map(Class<?> entityClass, Where whereClause, QueryParameter parameter) {
        return null;
    }

    @Override
    public <En> void update(Class<En> entityClass, Where whereClause, En samples) {

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
