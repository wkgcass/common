package main;

import cass.pure.init.InitializeSupport;
import cass.pure.ioc.IOCController;
import test.ManipUserDAO;

public class Main {
	public void addClass(Class<?> cls) {
	}

	public void doInit(String[] args) {
		main2(args);
	}

	public double priority() {
		return Double.MIN_EXPONENT;
	}
	
	public static void main2(String[] args) {
		ManipUserDAO manip = new ManipUserDAO();
		
		System.out.println(manip.getC0().getC1().getC2()+"    "+manip.getUserDAO());
		System.out.println(manip.getC0().getC1().getC2().getC1());
		System.out.println(manip.getC0().getC1());
		IOCController.retrieveVariable("abc");
		System.out.println(IOCController.retrieveVariable("def"));
		System.out.println(IOCController.retrieveConstant("ghi"));
	}

}
