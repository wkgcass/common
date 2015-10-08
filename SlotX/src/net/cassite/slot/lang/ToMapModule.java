package net.cassite.slot.lang;

import net.cassite.slot.configuration.Mapping;

public class ToMapModule {
        private final Mapping map;
        private final LangConfiguration config;

        ToMapModule(Mapping map, LangConfiguration config) {
                this.map = map;
                this.config = config;
        }

        public void prototype(String name) {
                config.mapPrototype(name, map);
        }
}
