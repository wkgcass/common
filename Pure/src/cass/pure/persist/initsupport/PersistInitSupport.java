package cass.pure.persist.initsupport;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import cass.pure.init.InitializeSupport;
import cass.pure.persist.Entity;
import cass.pure.persist.EntityManager;
import cass.pure.persist.PersistController;
import cass.pure.persist.Pool;
import cass.pure.persist.SQLParser;

public class PersistInitSupport implements InitializeSupport {

	private static Logger logger = Logger.getLogger(PersistInitSupport.class);

	private Set<Class<?>> parserClass = new HashSet<Class<?>>();
	private Set<Class<?>> poolClass = new HashSet<Class<?>>();
	private Set<Class<?>> entityClass = new HashSet<Class<?>>();

	@Override
	public void doInit(String[] args) {
		for (Class<?> cls : entityClass) {
			EntityManager.register(cls);
			logger.debug("Entity " + cls.getSimpleName()
					+ " has been registered.");
		}
		for (Class<?> cls : poolClass) {
			try {
				Pool pool = (Pool) cls.newInstance();
				PersistController.setPool(pool);
				logger.debug("Pool " + pool + " has been registered.");
			} catch (Throwable e) {
				logger.error(
						"Exception occured while initializing pool "
								+ cls.getSimpleName(), e);
				continue;
			}
		}
		for (Class<?> cls : parserClass) {
			SQLParser.registerParser(cls);
			logger.debug("SQLParser " + cls.getSimpleName()
					+ " has been registered.");
		}
	}

	@Override
	public void addClass(Class<?> cls) {
		if (Entity.class.isAssignableFrom(cls) && cls != Entity.class) {
			entityClass.add(cls);
		}
		if (Pool.class.isAssignableFrom(cls) && Pool.class != cls) {
			poolClass.add(cls);
		}
		if (SQLParser.class.isAssignableFrom(cls) && SQLParser.class != cls) {
			parserClass.add(cls);
		}
	}

	@Override
	public double priority() {
		return -4.2;
	}

}
