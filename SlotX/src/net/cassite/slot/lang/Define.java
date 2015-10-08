package net.cassite.slot.lang;

public class Define {
        private final LangConfiguration configuration;

        public Define(LangConfiguration configuration) {
                this.configuration = configuration;
        }

        public Pluggable $(String name, Class<?> cls) {
                return new Pluggable(configuration, name, cls);
        }
}
