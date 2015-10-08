package net.cassite.slot.lang;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import net.cassite.slot.exceptions.ConfigurationException;

public class Pluggable {
        public final String name;
        public final Class<?> cls;
        public final LangConfiguration configuration;
        private final List<Input> inputList = new ArrayList<>();
        private final List<Output> outputList = new ArrayList<>();
        private final List<TwoWay> twoWayList = new ArrayList<>();
        private final List<PluggableInstance> instList = new ArrayList<>();

        private final List<Method> methods;

        public Pluggable(LangConfiguration configuration, String name, Class<?> cls) {
                this.name = name;
                this.cls = cls;
                methods = new CopyOnWriteArrayList<>(cls.getMethods());
                this.configuration = configuration;
                configuration.mapPluggable(name, cls);
        }

        public TwoWay twoWay(String name, String methodName) {
                if (twoWayList.stream().filter(e -> e.name.equals(name) || e.method.getName().equals(methodName)).findFirst().isPresent()) {
                        throw new ConfigurationException("Duplicate name or method");
                }
                Optional<Method> found = methods.stream().filter(
                                m -> m.getName().equals(methodName) && m.getParameterCount() == 2 && m.getReturnType() == m.getParameterTypes()[1])
                                .findFirst();
                if (found.isPresent()) {
                        Method method = found.get();
                        TwoWay res = new TwoWay(name, method, this);
                        twoWayList.add(res);
                        return res;
                } else {
                        throw new ConfigurationException("TwoWay Method " + methodName + " not found");
                }
        }

        public Input input(String name, String methodName) {
                if (inputList.stream().filter(e -> e.name.equals(name) || e.method.getName().equals(methodName)).findFirst().isPresent()) {
                        throw new ConfigurationException("Duplicate name or method");
                }
                Optional<Method> found = methods.stream()
                                .filter(m -> m.getName().equals(methodName) && m.getParameterCount() == 1 && m.getReturnType() == Void.TYPE)
                                .findFirst();
                if (found.isPresent()) {
                        Method method = found.get();
                        Input res = new Input(name, method, this);
                        inputList.add(res);
                        return res;
                } else {
                        throw new ConfigurationException("Input Method " + methodName + " not found");
                }
        }

        public Output output(String name, String methodName) {
                if (outputList.stream().filter(e -> e.name.equals(name) || e.method.getName().equals(methodName)).findFirst().isPresent()) {
                        throw new ConfigurationException("Duplicate name or method");
                }
                Optional<Method> found = methods.stream()
                                .filter(m -> m.getName().equals(methodName) && m.getParameterCount() == 1 && m.getReturnType() == Void.TYPE)
                                .findFirst();
                if (found.isPresent()) {
                        Method method = found.get();
                        Output res = new Output(name, method, this);
                        outputList.add(res);
                        return res;
                } else {
                        throw new ConfigurationException("Output Method " + methodName + " not found");
                }
        }

        public PluggableInstance newInstance(String name) {
                if (instList.stream().filter(inst -> inst.name.equals(name)).findFirst().isPresent()) {
                        throw new ConfigurationException("Duplicate name");
                }
                PluggableInstance inst = new PluggableInstance(name, this);
                instList.add(inst);
                return inst;
        }
}
