package cass.pure.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cass.toolbox.util.LocalClassRetriever;

@InitIgnore
public final class ApplicationEntrance {

	private static Map<String, Object> constants = new HashMap<String, Object>();
	private static Logger logger = Logger.getLogger(ApplicationEntrance.class);

	public static void doInit(String[] args) throws Throwable {
		// get all InitializeSupport
		LocalClassRetriever retriever = new LocalClassRetriever();
		retriever.addBaseDir(ApplicationEntrance.class.getResource("/")
				.getPath().replace("%20", " "));

		Set<String> classNames = retriever.getMapping().keySet();
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (String s : classNames) {
			classes.add(ApplicationEntrance.class.getClassLoader().loadClass(s));
		}
		Set<InitializeSupport> supports = getSupport(classes);

		logger.info("ApplicationEntrance has detected the following InitializeSupport implementations");
		for (InitializeSupport is : supports) {
			logger.info(is.getClass().getName() + "\t\tpriority : "
					+ is.priority());
		}
		for (InitializeSupport is : supports) {
			for (Class<?> cls : classes) {
				if (null != cls.getAnnotation(InitIgnore.class)) {
					continue;
				}
				is.addClass(cls);
			}
		}
		SupportChain chain = new SupportChain(supports);
		while (chain.hasNext()) {
			InitializeSupport is = chain.next();
			is.doInit(args);
		}

	}

	private static Set<InitializeSupport> getSupport(Set<Class<?>> classes) {
		Set<InitializeSupport> ret = new HashSet<InitializeSupport>();
		for (Class<?> cls : classes) {
			if (InitializeSupport.class.isAssignableFrom(cls)) {
				try {
					InitializeSupport is = (InitializeSupport) cls
							.newInstance();
					ret.add(is);
				} catch (Exception e) {
				}
			}
		}
		return ret;
	}

	public static void addConstant(String alias, Object obj) {
		constants.put(alias, obj);
	}

	@SuppressWarnings("unchecked")
	public static <T> T retrieveConstant(String alias) {
		return (T) constants.get(alias);
	}
}

class SupportChain implements Iterator<InitializeSupport> {
	private Set<InitializeSupport> supports;
	private InitializeSupport current = null;
	private InitializeSupport next = null;

	SupportChain(Set<InitializeSupport> supports) {
		this.supports = supports;
		for (InitializeSupport is : supports) {
			if (null == next) {
				next = is;
				continue;
			}
			if (is.priority() > next.priority()) {
				next = is;
			}
		}
	}

	@Override
	public boolean hasNext() {
		return null != next;
	}

	@Override
	public InitializeSupport next() {
		current = next;

		InitializeSupport futureNext = null;
		for (InitializeSupport is : supports) {
			if (null == futureNext) {
				if (is.priority() < next.priority()) {
					futureNext = is;
				}
			} else {
				if (is.priority() < next.priority()
						&& is.priority() > futureNext.priority()) {
					futureNext = is;
				}
			}
		}
		next = futureNext;

		return current;
	}

	@Override
	public void remove() {
	}

}
