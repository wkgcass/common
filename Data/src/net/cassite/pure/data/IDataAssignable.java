package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/19.
 */
public interface IDataAssignable<T> extends IData<T> {
    UpdateEntry as(Object o);
}
