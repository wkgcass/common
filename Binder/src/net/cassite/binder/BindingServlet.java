package net.cassite.binder;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BindingServlet extends HttpServlet {

        /**
         * 
         */
        private static final long serialVersionUID = -7870975167860496363L;

        private static Map<String, Binding> binded = new LinkedHashMap<>();
        private static Gson gson = new Gson();

        @Override
        public void init(ServletConfig config) throws ServletException {
                super.init(config);
                Enumeration<String> names = config.getInitParameterNames();
                while (names.hasMoreElements()) {
                        String name = names.nextElement();
                        if (name.startsWith("mapping$") && name.length() > 8) {
                                String boundName = name.substring(9);
                                String clsName = config.getInitParameter(boundName);
                                try {
                                        Class<?> cls = Class.forName(clsName);

                                        binded.put(boundName, (Binding) cls.newInstance());
                                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                                        throw new ServletException(e);
                                }
                        }
                }
        }

        protected Binding getBinding(String name) {
                synchronized (binded) {
                        return binded.get(name);
                }
        }

        @SuppressWarnings("unchecked")
        protected void doAll(HttpServletRequest req, HttpServletResponse resp) {
                String differenceJSON = req.getParameter("difference");
                Map<String, Object> difference = gson.fromJson(differenceJSON, new TypeToken<Map<String, Object>>() {
                }.getType());
                Map<String, Object> $set = (Map<String, Object>) difference.get("$set");
                Map<String, Integer> $unset = (Map<String, Integer>) difference.get("$unset");
                Map<String, List<Object>> $pushAll = (Map<String, List<Object>>) difference.get("$pushAll");
                Map<String, List<Object>> $pullAll = (Map<String, List<Object>>) difference.get("$pullAll");
                // TODO
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
                doAll(req, resp);
        }

        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
                doAll(req, resp);
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
                doAll(req, resp);
        }

        @Override
        protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
                doAll(req, resp);
        }

        @Override
        protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
                doAll(req, resp);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
                doAll(req, resp);
        }

        @Override
        protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
                doAll(req, resp);
        }
}
