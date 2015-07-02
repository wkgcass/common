package test;

import cass.pure.ioc.annotations.Default;
import cass.pure.ioc.annotations.Wire;

@Wire
public class UserDAO {
	private int id=100;
	private String name;
	private String pwdMd5;
	private DatabaseAccess da;
	@Default
	public UserDAO(DatabaseAccess da){
		this.da=da;
	}
	public UserDAO(){
		id=2;
	}
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPwdMd5() {
		return pwdMd5;
	}
	public DatabaseAccess getDa() {
		return da;
	}
	
}
