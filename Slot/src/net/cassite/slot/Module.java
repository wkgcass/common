package net.cassite.slot;

import net.cassite.style.reflect.ClassSup;
import net.cassite.style.reflect.MethodSupport;

import static net.cassite.style.reflect.Reflect.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import static net.cassite.style.aggregation.Aggregation.*;

import net.cassite.slot.exceptions.parse.ClassNotFoundParseException;
import net.cassite.slot.exceptions.parse.MethodNotFoundParseException;
import net.cassite.slot.exceptions.parse.ParseException;
import net.cassite.slot.exceptions.parse.duplicated.DuplicatedDefinitionNameParseException;
import net.cassite.slot.exceptions.parse.duplicated.DuplicatedInNameParseException;
import net.cassite.slot.exceptions.parse.duplicated.DuplicatedModuleNameParseException;
import net.cassite.slot.exceptions.parse.duplicated.DuplicatedOutNameParseException;
import net.cassite.slot.exceptions.parse.duplicated.DuplicatedOuterInNameParseException;
import net.cassite.slot.exceptions.parse.duplicated.DuplicatedOuterOutNameParseException;
import net.cassite.style.$;
import net.cassite.style.ptr;

public class Module {
        @SuppressWarnings("unchecked")
        private MethodSupport<Object, Object> findMethod(ClassSup<Object> cls, String methodDef) {
                ptr<MethodSupport<Object, Object>> methodToReturn = ptr(null);
                int spaceIndex = methodDef.indexOf(' ');
                int leftParIndex = methodDef.indexOf('(');
                int rightParIndex = methodDef.indexOf(')');
                String returnType = methodDef.substring(0, spaceIndex);
                String methodName = methodDef.substring(spaceIndex + 1, leftParIndex);
                String typeStr = methodDef.substring(leftParIndex + 1, rightParIndex);
                String[] types = typeStr.split(",");
                $(types).toSelf($.trim());
                List<String> tmpList = new ArrayList<>();
                $(types).forEach(s -> {
                        if (!s.equals("")) {
                                tmpList.add(s);
                        }
                });
                String[] ntypes = tmpList.toArray(new String[tmpList.size()]);

                $(cls.allMethods()).forEach(method -> {
                        if (method.returnType().getName().equals(returnType) || method.returnType().getSimpleName().equals(returnType)) {
                                // return type checked
                                if (method.name().equals(methodName)) {
                                        // method name checked
                                        if (method.argCount() == ntypes.length) {
                                                // arg length
                                                // checked
                                                if (ntypes.length == 0 || $(method.argTypes()).forEach((t, i) -> {
                                                        if (t.getName().equals(ntypes[$(i)]) || t.getSimpleName().equals(ntypes[$(i)])) {
                                                                return true;
                                                        } else {
                                                                return BreakWithResult(false);
                                                        }
                                                })) {
                                                        // found
                                                        methodToReturn.item = (MethodSupport<Object, Object>) method;
                                                        Break();
                                                }
                                        }
                                }
                        }
                });
                return methodToReturn.item;
        }

        class Definition {
                class In {
                        final String name;
                        final MethodSupport<Object, Object> method;

                        In(String methodDef, String name) {
                                this.name = name;
                                this.method = findMethod(cls, methodDef);
                                if (method == null) {
                                        throw new MethodNotFoundParseException(cls, methodDef);
                                }
                        }

                        public String toString() {
                                return "In(name=" + name + ", method=" + method + ")";
                        }
                }

                class Out {
                        final String name;
                        final MethodSupport<Object, Object> method;

                        Out(String methodDef, String name) {
                                this.name = name;
                                this.method = findMethod(cls, methodDef);
                                if (method == null) {
                                        throw new MethodNotFoundParseException(cls, methodDef);
                                }
                        }

                        public String toString() {
                                return "Out(name=" + name + ", method=" + method + ")";
                        }
                }

                final ClassSup<Object> cls;
                final String name;
                final Map<String, In> in;
                final Map<String, Out> out;

                Definition(ClassSup<Object> cls, String name, List<Element> io) {
                        Map<String, In> in = new HashMap<>();
                        Map<String, Out> out = new HashMap<>();
                        this.cls = cls;
                        this.name = name;
                        for (Element e : io) {
                                String methodDef = e.attributeValue("method");
                                String nameX = e.attributeValue("name");
                                if (e.getName().equals("in")) {
                                        // in
                                        if (null == methodDef) {
                                                throw new ParseException("in", "method");
                                        }
                                        if (null == nameX) {
                                                throw new ParseException("in", "name");
                                        }
                                        if (in.containsKey(nameX)) {
                                                throw new DuplicatedInNameParseException(name, nameX);
                                        }
                                        in.put(nameX, new In(methodDef, nameX));
                                } else if (e.getName().equals("out")) {
                                        // out
                                        if (null == methodDef) {
                                                throw new ParseException("out", "method");
                                        }
                                        if (null == nameX) {
                                                throw new ParseException("out", "name");
                                        }
                                        if (out.containsKey(nameX)) {
                                                throw new DuplicatedOutNameParseException(name, nameX);
                                        }
                                        out.put(nameX, new Out(methodDef, nameX));
                                }
                        }
                        this.in = readOnly(in);
                        this.out = readOnly(out);
                }

                public String toString() {
                        return "Definition(name=" + name + ", class=" + cls + ", in=" + in + ", out=" + out + ")";
                }
        }

        class Connection {
                final Definition from;
                final MethodSupport<Object, Object> out;
                final Definition to;
                final MethodSupport<Object, Object> in;

                Connection(String from, String out, String to, String in) {
                        if (from.contains("#")) {
                                this.from = modules.get(from.substring(0, from.indexOf('#'))).outerOut.get(from.substring(from.indexOf('#') + 1)).def;
                        } else {
                                this.from = definitions.get(from);
                        }
                        this.out = this.from.out.get(out).method;
                        if (to.contains("#")) {
                                this.to = modules.get(to.substring(0, to.indexOf('#'))).outerIn.get(to.substring(to.indexOf('#') + 1)).def;
                        } else {
                                this.to = definitions.get(to);
                        }
                        this.in = this.to.in.get(in).method;
                }

                public String toString() {
                        return "Connection(from=" + from + ", out=" + out + ", to=" + to + ", in=" + in + ")";
                }
        }

        public class OuterIn {
                final Definition def;
                final MethodSupport<Object, Object> in;
                final String name;

                OuterIn(String name, String def, String in) {
                        this.name = name;
                        this.def = definitions.get(def);
                        this.in = this.def.in.get(in).method;
                }

                public String toString() {
                        return "OuterIn(name=" + name + ", def=" + def.name + ", in=" + in + ")";
                }
        }

        public class OuterOut {
                final Definition def;
                final MethodSupport<Object, Object> out;
                final String name;

                OuterOut(String name, String def, String out) {
                        this.name = name;
                        this.def = definitions.get(def);
                        this.out = this.def.out.get(out).method;
                }

                public String toString() {
                        return "OuterOut(name=" + name + ", def=" + def.name + ", out=" + out + ")";
                }
        }

        final String name;
        final Map<String, Module> modules;
        final Map<String, Definition> definitions;
        final List<Connection> connections;
        final ClassSup<Object> outerInterface;
        final Map<String, OuterIn> outerIn;
        final Map<String, OuterOut> outerOut;
        final ModuleManager manager;

        @SuppressWarnings("unchecked")
        Module(ModuleManager mgr, String name, Element imports, Element defs, Element connections, Element outer) {
                this.manager = mgr;
                this.name = name;
                Map<String, Module> modules = new HashMap<String, Module>();
                Map<String, Definition> definitions = new HashMap<>();
                List<Connection> thisConnections = new ArrayList<Module.Connection>();
                Map<String, OuterIn> outerIn = new HashMap<>();
                Map<String, OuterOut> outerOut = new HashMap<>();
                if (null != imports) {
                        for (Element imp : (List<Element>) imports.elements()) {
                                String path = imp.attributeValue("path");
                                if (path == null) {
                                        throw new ParseException("import", "path");
                                }
                                InputStream in = ModuleManager.class.getResourceAsStream(path);
                                SAXReader reader = new SAXReader();
                                try {
                                        Document document = reader.read(in);
                                        Element root = document.getRootElement();

                                        String moduleName = root.attributeValue("name");

                                        Element importsX = root.element("imports");
                                        Element defsX = root.element("definitions");
                                        Element connectionsX = root.element("connections");
                                        Element outerX = root.element("outer");

                                        if (modules.containsKey(moduleName)) {
                                                throw new DuplicatedModuleNameParseException(name, moduleName);
                                        }
                                        modules.put(moduleName, new Module(mgr, moduleName, importsX, defsX, connectionsX, outerX));
                                } catch (DocumentException ex) {
                                        throw new ParseException(ex);
                                }
                        }
                }
                this.modules = readOnly(modules);
                if (null != defs) {
                        for (Element def : (List<Element>) defs.elements()) {
                                String clazz = def.attributeValue("class");
                                String defName = def.attributeValue("name");
                                if (defName == null) {
                                        throw new ParseException("def", "name");
                                }
                                if (clazz == null) {
                                        throw new ParseException("def", "class");
                                }
                                ClassSup<Object> classSup;
                                try {
                                        classSup = (ClassSup<Object>) cls(Class.forName(clazz));
                                } catch (ClassNotFoundException e) {
                                        throw new ClassNotFoundParseException(e);
                                }
                                if (definitions.containsKey(defName)) {
                                        throw new DuplicatedDefinitionNameParseException(name, defName);
                                }
                                definitions.put(defName, new Definition(classSup, defName, def.elements()));
                        }
                }
                this.definitions = readOnly(definitions);
                if (null != connections) {
                        for (Element conn : (List<Element>) connections.elements()) {
                                String from = conn.attributeValue("from");
                                String out = conn.attributeValue("out");
                                String to = conn.attributeValue("to");
                                String in = conn.attributeValue("in");
                                if (from == null) {
                                        throw new ParseException("conn", "from");
                                }
                                if (out == null) {
                                        throw new ParseException("conn", "out");
                                }
                                if (to == null) {
                                        throw new ParseException("conn", "to");
                                }
                                if (in == null) {
                                        throw new ParseException("conn", "in");
                                }
                                thisConnections.add(new Connection(from, out, to, in));
                        }
                }
                this.connections = readOnly(thisConnections);
                if (null != outer) {
                        try {
                                this.outerInterface = (ClassSup<Object>) cls(Class.forName(outer.attributeValue("interface")));
                        } catch (ClassNotFoundException ex) {
                                throw new ClassNotFoundParseException(ex);
                        }
                        for (Element e : (List<Element>) outer.elements()) {
                                if (e.getName().equals("in")) {
                                        // in
                                        String nameX = e.attributeValue("name");
                                        String defX = e.attributeValue("def");
                                        String inX = e.attributeValue("in");
                                        if (nameX == null) {
                                                throw new ParseException("in", "name");
                                        }
                                        if (defX == null) {
                                                throw new ParseException("in", "def");
                                        }
                                        if (inX == null) {
                                                throw new ParseException("in", "in");
                                        }
                                        if (outerIn.containsKey(nameX)) {
                                                throw new DuplicatedOuterInNameParseException(name, nameX);
                                        }
                                        outerIn.put(nameX, new OuterIn(nameX, defX, inX));
                                } else if (e.getName().equals("out")) {
                                        // out
                                        String nameX = e.attributeValue("name");
                                        String defX = e.attributeValue("def");
                                        String outX = e.attributeValue("out");
                                        if (nameX == null) {
                                                throw new ParseException("out", "name");
                                        }
                                        if (defX == null) {
                                                throw new ParseException("out", "def");
                                        }
                                        if (outX == null) {
                                                throw new ParseException("out", "out");
                                        }
                                        if (outerOut.containsKey(nameX)) {
                                                throw new DuplicatedOuterOutNameParseException(name, nameX);
                                        }
                                        outerOut.put(nameX, new OuterOut(nameX, defX, outX));
                                }
                        }
                } else {
                        this.outerInterface = null;
                }
                this.outerIn = readOnly(outerIn);
                this.outerOut = readOnly(outerOut);
        }

        @Override
        public String toString() {
                return toString(0, new StringBuilder());
        }

        private StringBuilder appendTabs(int tabs, StringBuilder sb) {
                for (int i = 0; i < tabs; ++i) {
                        sb.append('\t');
                }
                return sb;
        }

        private String toString(int tabs, StringBuilder sb) {
                sb.append("Module").append(" name=").append(name).append("\n");
                appendTabs(tabs, sb).append("imported_modules={");
                boolean isFirst = true;
                for (Module m : modules.values()) {
                        if (!isFirst) {
                                sb.append(", ");
                        }
                        sb.append(m.toString(tabs + 1, new StringBuilder()));
                        isFirst = false;
                }
                sb.append("}");
                sb.append("\n");
                appendTabs(tabs, sb).append("definitions=").append(definitions);
                sb.append("\n");
                appendTabs(tabs, sb).append("connections=").append(connections);
                sb.append("\n");
                appendTabs(tabs, sb).append("outer_input=").append(outerIn);
                sb.append("\n");
                appendTabs(tabs, sb).append("outer_output=").append(outerOut);
                return sb.toString();
        }
}
