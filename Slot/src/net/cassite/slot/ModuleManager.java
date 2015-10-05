package net.cassite.slot;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import net.cassite.slot.exceptions.parse.ParseException;

public class ModuleManager {

        private static Logger LOGGER = Logger.getLogger(ModuleManager.class);

        public final Module root;

        public ModuleManager(String rootModulesXml) {
                InputStream in = ModuleManager.class.getResourceAsStream(rootModulesXml);
                SAXReader reader = new SAXReader();
                try {
                        Document document = reader.read(in);
                        Element root = document.getRootElement();

                        // imports
                        Element imports = root.element("imports");
                        Element defs = root.element("definitions");
                        Element connections = root.element("connections");
                        Element outer = root.element("outer");

                        this.root = new Module(this, root.attributeValue("name"), imports, defs, connections, outer);
                } catch (DocumentException ex) {
                        throw new ParseException(ex);
                }
                LOGGER.debug("Initialize with Modules\n" + root.toString());
        }
}
