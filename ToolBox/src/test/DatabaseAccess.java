package test;

import cass.toolbox.ioc.annotations.Bean;
import cass.toolbox.ioc.annotations.Default;

@Bean(name="abc")
@Default(bean="def")
public class DatabaseAccess {
	
}
