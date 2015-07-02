package test;

import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.annotations.Force;
import cass.toolbox.ioc.annotations.Ignore;
import cass.toolbox.ioc.annotations.Use;

public class ManipUserDAO extends AutoWire {
	private UserDAO userDAO;
	private Circular0 c0;
	private int i;
	@Use(bean="hehe")
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
}
