package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/11.
 */
public interface IData<T> {
    T get();

    void set(T t);

    Object getEntity();

    OrderBase desc();

    OrderBase asc();
}
