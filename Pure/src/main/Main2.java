package main;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cass.pure.init.ApplicationEntrance;
import cass.pure.persist.Entity;
import cass.pure.persist.EntityManager;
import cass.pure.persist.PersistController;
import cass.pure.persist.SQLParser;
import cass.pure.persist.Transaction;
import cass.pure.persist.annotation.Reference;
import cass.pure.persist.parser.MySQLParser;
import cass.pure.persist.pool.C3p0;

public class Main2 {

	public static void main(String[] args) throws Throwable {
		ApplicationEntrance.doInit(args);

		EntityManager.createTables();

		List<UserInfo> uiList = EntityManager.findAll(UserInfo.class);
		UserInfo ui = uiList.get(0);
		System.out.println(ui);
		ui.setMajor("改一下1");
		System.out.println(ui);

		System.out.println(ui.isModified());

		Transaction t = PersistController.getTransaction();
		t.commit();
		t.close();
	}

}

class User extends Entity {
	private int id;
	private String name;
	@Reference(ref = UserInfo.class)
	private List<UserInfo> info;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UserInfo> getInfo() {
		return info;
	}
}

class UserInfo extends Entity {
	private int id;
	private User user;
	private String major;
	private String clazz;

	public int getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}