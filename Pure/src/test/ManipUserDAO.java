package test;

import cass.pure.aop.AOP;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.annotations.Use;

@AOP
public class ManipUserDAO extends AutoWire {
	private UserDAO userDAO;
	private Circular0 c0;
	private int i;
	
	@Use(clazz=UserDAO2.class)
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setC0(Circular0 c0) {
		this.c0 = c0;
	}

	public Circular0 getC0() {
		return c0;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getI() {
		return i;
	}
}
