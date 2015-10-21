package net.cassite.pure.data;

public abstract class Parameter {

    public Condition $eq(Object obj) {
        return new Condition(this, ConditionTypes.eq, new Object[]{obj});
    }

    public Condition $ne(Object obj) {
        return new Condition(this, ConditionTypes.ne, new Object[]{obj});
    }

    public Condition in(PreResult<?> query) {
        return new Condition(this, ConditionTypes.in, new Object[]{query});
    }

    public Condition notIn(PreResult<?> query) {
        return new Condition(this, ConditionTypes.notIn, new Object[]{query});
    }

    public Condition like(Object... obj) {
        return new Condition(this, ConditionTypes.like, new Object[]{obj});
    }

    public Condition isNull() {
        return new Condition(this, ConditionTypes.isNull, new Object[0]);
    }

    public Condition isNotNull() {
        return new Condition(this, ConditionTypes.isNotNull, new Object[0]);
    }

    public Condition member(ParameterAggregate pc) {
        return new Condition(this, ConditionTypes.member, new Object[]{pc});
    }

    public Condition notMember(ParameterAggregate pc) {
        return new Condition(this, ConditionTypes.notMember, new Object[]{pc});
    }
}
