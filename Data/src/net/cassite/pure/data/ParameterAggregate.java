package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/11.
 */
public abstract class ParameterAggregate extends Parameter {
    public Condition reverseMember(Object o) {
        return new Condition(this, ConditionTypes.reverseMember, new Object[]{o});
    }

    public Condition reverseNotMember(Object o) {
        return new Condition(this, ConditionTypes.reverseNotMember, new Object[]{o});
    }
}
