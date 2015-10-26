package net.cassite.datafacade;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/26.
 */
public class Focus {
        public Map<IData<?>, String> focusMap = new LinkedHashMap<IData<?>, String>();

        public Focus focus(IData<?> data) {
                focusMap.put(data, DataUtils.dataToStringUtil(data));
                return this;
        }

        public Focus focus(IData<?> data, String alias) {
                focusMap.put(data, alias);
                return this;
        }
}
