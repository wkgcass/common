package cass.pure.ioc.listener;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import cass.pure.init.InitIgnore;

@InitIgnore
public class DebugIOCListener extends DefaultIOCListener {
	PrintStream out;
	public DebugIOCListener(){
		this(System.out);
	}
	public DebugIOCListener(PrintStream ps){
		if(null==ps) throw new NullPointerException();
		out=ps;
	}
	public void listenInvokeSetter(Object target, Method m){
		out.println("IOCController.invokeSetter("+target+"    "+m.getName()+")");
	}
	public void listenConstruct(Constructor<?> con, Object[] parameterValues){
		out.println("IOCController.construct("+con+"    "+Arrays.toString(parameterValues)+")");
	}
	public void listenConstructBean(String beanname){
		out.println("IOCController.constructBean("+beanname+")");
	}
	public void listenConstructObject(Class<?> cls){
		out.println("IOCController.constructObject("+cls+")");
	}
	public void listenGet(Class<?> cls){
		out.println("IOCController.get("+cls+")");
	}
	public void listenGetBean(String beanname){
		out.println("IOCController.getBean("+beanname+")");
	}
	public void listenGetObject(Class<?> cls){
		out.println("IOCController.getObject("+cls+")");
	}
}
