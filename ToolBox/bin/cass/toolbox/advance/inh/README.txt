version 0.0.0
[usage]
	Simulates multiple inheritance of java.
	1.check if all super classes must have interfaces.
	2.create an interface (and optionally extends MIInterface).
	3.create your class implements the interface you created and extends MultipleInheritable.
	4.create static Class<?> fields representing all classes you really want to extend.
	5.create non-static Extends fields if you need to access super classes.
	6.use con/invoke/field methods to construct/invoke methods/get fields.
	7.use static method generate of InheritanceHandler to generate.
	8.access the generated object with interfaces.
[containing]
	Extends	[class]simulates what a normal class does.
	InheritanceHandler	[class]generates proxy objects.
	InheritanceRuntimeException	[class]thrown when invoking/getting an unknown member, or trying to constructing the class with wrong parameters.
	MIInterface	[interface]extended by the interface of multiple inheritable class if you want to get access to super classes of that class. 
	MultipleInheritable	[abstract class]extended by your multiple inheritable class.
[requirement]
	JDK1.6 or higher
	cass.toolbox.reflect 0.0.0 or higher
	cass.toolbox.util 0.0.0 or higher