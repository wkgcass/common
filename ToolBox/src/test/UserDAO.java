package test;

import cass.toolbox.ioc.annotations.Bean;
import cass.toolbox.ioc.annotations.Default;
import cass.toolbox.ioc.annotations.Force;
import cass.toolbox.ioc.annotations.IsSingleton;
import cass.toolbox.ioc.annotations.Use;
import cass.toolbox.ioc.annotations.Wire;
@Bean(name={"dao","hehe"})
@Wire
public class UserDAO {
	private int id=100;
	private String name;
	private String pwdMd5;
	private DatabaseAccess da;
	@Default(bean="hehe")
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
