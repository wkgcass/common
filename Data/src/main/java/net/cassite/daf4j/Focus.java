package net.cassite.daf4j;

import net.cassite.daf4j.util.Selectable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/26.
 */
public class Focus {
        public Map<Selectable, String> focusMap = new LinkedHashMap<Selectable, String>();

        public Focus focus(IData<?> data) {
                return focus(data, DataUtils.dataToStringUtil(data));
        }

        public Focus focus(Selectable selectable, String alias) {
                focusMap.put(selectable, alias);
                return this;
        }
}
