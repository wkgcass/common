package net.cassite.pure.data;

/**
 * Created by blador01 on 2015/10/12.
 */
public class UpdateEntry {
    public final IData<?> data;
    public final Object updateValue;

    UpdateEntry(IData<?> data, Object updateValue) {
        this.data = data;
        this.updateValue = updateValue;
    }
}
