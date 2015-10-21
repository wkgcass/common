package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/11.
 */
public interface IData<T> {
    T get();

    Object getEntity();

    OrderBase desc();

    OrderBase asc();
}
