package net.cassite.pure.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreResult<En> {
    private final DataAccess dataAccess;
    public final En entity;
    public final Where whereClause;

    PreResult(DataAccess dataAccess, En entity, Where whereClause) {
        this.dataAccess = dataAccess;
        this.entity = entity;
        this.whereClause = whereClause;
    }

    public List<En> list() {
        return dataAccess.list(entity, whereClause, null);
    }

    public List<En> list(QueryParameter parameters) {
        return dataAccess.list(entity, whereClause, parameters);
    }

    public En first() {
        List<En> list = dataAccess.list(entity, whereClause, new QueryParameter().top(1));
        if (list == null || list.isEmpty()) return null;
        return list.get(0);
    }

    public List<Map<String, Object>> map() {
        return dataAccess.map(entity, whereClause, null);
    }

    public List<Map<String, Object>> map(QueryParameterWithFocus parameters) {
        return dataAccess.map(entity, whereClause, parameters);
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
        dataAccess.update(entity, whereClause, tmpList.toArray(new UpdateEntry[tmpList.size()]));
    }

    public void set(UpdateEntry... toUpdate) {
        dataAccess.update(entity, whereClause, toUpdate);
    }

    public void remove() {
        dataAccess.remove(entity, whereClause);
    }

    public void makeList(String name, QueryParameter parameter) {
        // TODO
    }

    public void makeMap(String name, QueryParameterWithFocus parameter) {
        // TODO
    }

    public void makeUpdate(String name, UpdateEntry... entires) {
        // TODO
    }

    public void makeDelete(String name) {
        // TODO
    }

    @Override
    public String toString() {
        return "from " + entity.getClass().getSimpleName() + " where " + whereClause;
    }
}
