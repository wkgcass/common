package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Pattern;

import test.test1.IT;
import test.test1.T;
import test.test2.U;

import cass.toolbox.advance.inh.Extends;
import cass.toolbox.advance.inh.InheritanceHandler;
import cass.toolbox.advance.inh.MultipleInheritable;
import cass.toolbox.ioc.IOCController;
import cass.toolbox.ioc.annotations.Bean;
import cass.toolbox.ioc.annotations.Use;
import cass.toolbox.ioc.listener.DebugIOCListener;
import cass.toolbox.ioc.listener.IOCListener;
import cass.toolbox.other.Swap;
import cass.toolbox.reflect.ConstructorDeposit;
import cass.toolbox.reflect.FieldDeposit;
import cass.toolbox.reflect.MethodDeposit;
import cass.toolbox.xml.XMLParser;
import cass.toolbox.xml.XMLPojoParser;


public class Main {
	public static void main(String[] args) throws Throwable {
		/*
		Integer[] a=new Integer[]{1,2};
		Integer[] b=new Integer[]{3,4};
		System.out.println(Arrays.toString(a)+"      "+Arrays.toString(b));
		Swap.synchronizedSwapObj(a, b);
		System.out.println(Arrays.toString(a)+"      "+Arrays.toString(b));
		*/
		/*
		FieldDeposit fd=new FieldDeposit(new U());
		fd.addAll();
		System.out.println(fd.get("a"));
		MethodDeposit md=new MethodDeposit(new U());
		md.addAll();
		System.out.println(md.invoke("p"));
		md.invoke("h",2,6);
		System.out.println(int.class.isInstance((new Integer(1)).intValue()));
		System.out.println((Character.class== char.class));
		*/
		/*
		ConstructorDeposit cd=new ConstructorDeposit(U.class);
		U u =(U)cd.newInstance();
		MethodDeposit md=new MethodDeposit(u);
		MethodDeposit md2=new MethodDeposit(md);
		System.out.println(md2.invoke("invoke","p"));
		ConstructorDeposit cd2=new ConstructorDeposit(U.class);
		T t=(T)cd2.newInstance(4l);
		System.out.println(t.a);
		*/
		/*
		ITest itest= InheritanceHandler.generate(ITest.class,Test.class);
		System.out.println(itest.p());
		System.out.println(((ITest)itest).sup("T").invoke("p"));
		*/
		/*
		Y y=new Y();
		System.out.println(y.getI());
		*/
		/*
		IOCListener listener=new DebugIOCListener();
		IOCController.register(listener);
		ManipUserDAO manipUserDAO=new ManipUserDAO();
		//System.out.println(manipUserDAO.getUserDAO().getDa());
		//System.out.println(manipUserDAO.getUserDAO().getId());
		//System.out.println(a.getB().getX());
		//System.out.println(a.getB().getX());
		//Circular0 c0=new Circular0();
		Thread.sleep(1);
		//System.out.println(c0.getC1().getC2());
		System.out.println(manipUserDAO.getC0().getC1().getC2()+"    "+manipUserDAO.getUserDAO().getDa());
		*/
		/*
		Pattern p=Pattern.compile("/test/TestServlet.*");
		System.out.println(p.matcher("/test/TestServlet").matches());
		*/
		XMLParser xp=new XMLParser(new File("C:\\mapping.xml"));
		//System.out.println(xp.delete("hotplug.init"));
		//System.out.println(xp.rollback());
		//System.out.println(xp.insert("hotplug.abc(a=2, b=5)"));
		//System.out.println(xp.commite());
		//System.out.println(xp.childElementNames("hotplug"));
		XMLPojoParser parser=new XMLPojoParser(xp);
		Pojo p=parser.getPojo("hotplug.init", Pojo.class);
		
		System.out.println(xp.childElementNames("hotplug"));
		
		System.out.println(p.getJars());
	}

}

abstract class X{
	public X() throws IllegalAccessException, InvocationTargetException{
		Method[] methods=this.getClass().getMethods();
		for(Method m:methods){
			if(m.getName().startsWith("set") && m.getName().charAt(3)>='A' && m.getName().charAt(3)<='Z'){
				m.invoke(this, 20);
			}
		}
	}
}
class Y extends X{
	public Y() throws IllegalAccessException, InvocationTargetException {
		super();
	}

	int i;
	
	public void setI(int i){
		this.i=i;
	}
	public int getI(){
		return i;
	}
	
}