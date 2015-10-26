package net.cassite.datafacade;

public class From<En> {
    private final En entity;
    private final DataAccess dataAccess;

    From(En entity, DataAccess dataAccess) {
        this.entity = entity;
        this.dataAccess = dataAccess;
    }

    public PreResult<En> where(Where whereClause) {
        return new PreResult<En>(dataAccess, entity, whereClause);
    }
}
