package net.cassite.pure.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreResult<En> {
    private final DataAccess dataAccess;
    private final Class<En> entityClass;
    private final Where whereClause;

    PreResult(DataAccess dataAccess, Class<En> entityClass, Where whereClause) {
        this.dataAccess = dataAccess;
        this.entityClass = entityClass;
        this.whereClause = whereClause;
    }

    public List<En> list() {
        return dataAccess.list(entityClass, whereClause, null);
    }

    public List<En> list(QueryParameter parameters) {
        return dataAccess.list(entityClass, whereClause, parameters);
    }

    public Map<String, Object> map() {
        return dataAccess.map(entityClass, whereClause, null);
    }

    public Map<String, Object> map(QueryParameter parameters) {
        return dataAccess.map(entityClass, whereClause, parameters);
    }

    public void saveAs(En samples) {
        List<UpdateEntry> tmpList = new ArrayList<UpdateEntry>();
        try {
            for (Field f : samples.getClass().getFields()) {
                Object o = f.get(samples);
                if ((o instanceof IData) && !(o instanceof ParameterAggregate)) {
                    IData<?> data = (IData<?>) o;
                    tmpList.add(data.as(data.get()));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        dataAccess.update(entityClass, whereClause, tmpList.toArray(new UpdateEntry[tmpList.size()]));
    }

    public void set(UpdateEntry... toUpdate) {
        dataAccess.update(entityClass, whereClause, toUpdate);
    }

    public void remove() {
        dataAccess.remove(entityClass, whereClause);
    }

    public Where getWhereClause() {
        return whereClause;
    }

    @Override
    public String toString() {
        return "from " + entityClass.getSimpleName() + " where " + whereClause;
    }
}
