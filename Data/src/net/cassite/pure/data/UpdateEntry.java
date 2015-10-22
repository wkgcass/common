package net.cassite.pure.data;

/**
 * 更新依据
 */
public class UpdateEntry {
    /**
     * 要更新的字段
     */
    public final IData<?> data;
    /**
     * 要更新为的值(可以是表达式)
     */
    public final Object updateValue;

    UpdateEntry(IData<?> data, Object updateValue) {
        this.data = data;
        this.updateValue = updateValue;
    }
}
