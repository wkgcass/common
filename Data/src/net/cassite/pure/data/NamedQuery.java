package net.cassite.pure.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/18.
 */
public abstract class NamedQuery {
    private String name;
    private Map<String, Integer> map = new HashMap<String, Integer>();

    protected NamedQuery(String name) {
        this.name = name;
    }

    /**
     * Fill in the query
     *
     * @param index index start at 1
     * @param arg   arg to fill
     */
    public abstract void fill(int index, Object arg);

    public final String getName() {
        return name;
    }

    public final void alias(int index, String name) {
        if (map.containsValue(index)) {
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String str = it.next();
                if (map.get(str).equals(index)) {
                    it.remove();
                    break;
                }
            }
            map.put(name, index);
        }
    }

    public final void alias(String... aliases) {
        for (int i = 0; i < aliases.length; ++i) {
            alias(i, aliases[i]);
        }
    }

    public final void fill(Object... args) {
        for (int i = 0; i < args.length; ++i) {
            fill(i + 1, args[i]);
        }
    }

    public final void fill(String alias, Object arg) {
        fill(map.get(alias), arg);
    }
}
