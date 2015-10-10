package net.cassite.pure.data;

import java.util.List;

public interface DataAccess {
        <En> List<En> list(Class<En> entityClass, Where whereClause);

        <En> List<En> list(Class<En> entityClass, Where whereClause, QueryParameter parameter);
}
