package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.cassite.style.Async;
import net.cassite.style.Style;
import net.cassite.style.def;
import net.cassite.style.var;

public class Test implements var {

	def<?> arg0;
	def<?> arg1;
	def<?> arg2;
	def<?> arg3;
	def<?> arg4;
	def<?> arg5;
	def<?> arg6;
	def<?> arg7;

	def<Integer> rarg0;
	def<Integer> rarg1;
	def<Integer> rarg2;
	def<Integer> rarg3;
	def<Integer> rarg4;
	def<Integer> rarg5;
	def<Integer> rarg6;
	def<Integer> rarg7;

	String[] strArr;
	Collection<Integer> coll;
	List<Integer> list;
	Map<String, Integer> map;

	public static void main(String[] args) {
		Test t = new Test();
		t.testFuncCreation();
		testApply(t);
		testAsyncAwait(t);
		testCallback(t);
		t.testIfSwitchForWhile();
		t.testArray();
		t.testCollection();
		t.testList();
		t.testMap();
	}

	public void testFuncCreation() {
		System.out.println("Test Func Creation");
		arg0 = $(() -> System.out.println(Thread.currentThread() + "arg0"));
		arg1 = $((Integer i) -> System.out.println(Thread.currentThread() + "arg1" + i));
		arg2 = $((Integer i, String s) -> System.out.println(Thread.currentThread() + "arg2" + i + s));
		arg3 = $((Integer i, String s, Boolean b) -> System.out.println(Thread.currentThread() + "arg3" + i + s + b));
		arg4 = $((Integer i, String s, Boolean b, Double d) -> System.out
				.println(Thread.currentThread() + "arg4" + i + s + b + d));
		arg5 = $((Integer i, String s, Boolean b, Double d, def<Object> func) -> System.out
				.println(Thread.currentThread() + "arg5" + i + s + b + d + func.apply()));
		arg6 = $((Integer i, String s, Boolean b, Double d, def<Object> func, def<Object> func2) -> {
			func2.apply();
			System.out.println(Thread.currentThread() + "arg6" + i + s + b + d + func.apply());
		});
		arg7 = $((Integer i, String s, Boolean b, Double d, def<Object> func, def<Object> func2, Integer i2) -> {
			func2.apply();
			System.out.println(Thread.currentThread() + "arg7" + i + s + b + d + func.apply() + i2);
		});

		rarg0 = $(() -> {
			System.out.println(Thread.currentThread());
			return 0;
		});
		rarg1 = $((Integer i) -> {
			System.out.println(Thread.currentThread());
			return 1;
		});
		rarg2 = $((Integer i, String s) -> {
			System.out.println(Thread.currentThread());
			return 2;
		});
		rarg3 = $((Integer i, String s, Boolean b) -> {
			System.out.println(Thread.currentThread());
			return 3;
		});
		rarg4 = $((Integer i, String s, Boolean b, Double d) -> {
			System.out.println(Thread.currentThread());
			return 4;
		});
		rarg5 = $((Integer i, String s, Boolean b, Double d, def<Object> func) -> {
			System.out.println(Thread.currentThread());
			return 5;
		});
		rarg6 = $((Integer i, String s, Boolean b, Double d, def<Object> func, def<Object> func2) -> {
			System.out.println(Thread.currentThread());
			return 6;
		});
		rarg7 = $((Integer i, String s, Boolean b, Double d, def<Object> func, def<Object> func2, Integer i2) -> {
			System.out.println(Thread.currentThread());
			return 7;
		});
	}

	public static void testApply(Test t) {
		System.out.println("Test Apply");
		t.arg0.apply();
		t.arg1.apply(1);
		t.arg2.apply(1, "2");
		t.arg3.apply(1, "2", true);
		t.arg4.apply(1, "2", true, 4.0);
		t.arg5.apply(1, "2", true, 4.0, t.rarg0);
		t.arg6.apply(1, "2", true, 4.0, t.rarg0, t.arg0);
		t.arg7.apply(1, "2", true, 4.0, t.rarg0, t.arg0, 7);

		System.out.println(t.rarg0.apply());
		System.out.println(t.rarg1.apply(1));
		System.out.println(t.rarg2.apply(1, "2"));
		System.out.println(t.rarg3.apply(1, "2", true));
		System.out.println(t.rarg4.apply(1, "2", true, 4.0));
		System.out.println(t.rarg5.apply(1, "2", true, 4.0, t.rarg0));
		System.out.println(t.rarg6.apply(1, "2", true, 4.0, t.rarg0, t.arg0));
		System.out.println(t.rarg7.apply(1, "2", true, 4.0, t.rarg0, t.arg0, 7));
	}

	public static void testAsyncAwait(Test t) {
		System.out.println("Test AsyncAwait");
		Async<?> a = t.arg1.async(1);
		System.out.println(a.await());
		Async<Integer> b = t.rarg1.async(1);
		System.out.println(b.await());

		System.out.println("Test AsyncAwait onError");
		def<Object> throwErrFunc = Style.$(() -> {
			Style.sleep(1000);
			throw new RuntimeException("abc");
		});
		throwErrFunc.async().onError((err) -> {
			System.out.println(Thread.currentThread());
			err.printStackTrace();
		});
		throwErrFunc.async().awaitError((err) -> {
			System.out.println(Thread.currentThread());
			err.printStackTrace();
		});
	}

	public static void testCallback(Test t) {
		System.out.println("Test Callback");
		Style.$(t.rarg0.async(), t.rarg1.async(1))
				.callback((Integer i1, Integer i2) -> System.out.println(Thread.currentThread() + "" + i1 + " " + i2));
		Style.$(t.rarg0.async(), t.rarg1.async(1)).callbackSync(
				(Integer i1, Integer i2) -> System.out.println(Thread.currentThread() + "" + i1 + " " + i2));
	}

	public void testIfSwitchForWhile() {
		System.out.println("Test IfSwitchForWhile");
		System.out.println(If(3 > 4, 1).ElseIf(3 > 2, () -> 2).ElseIf(4 > 3, 5).Else(3));

		If(1 > 2, () -> System.out.println(1)).ElseIf(3 > 2, () -> System.out.println(2))
				.Else(() -> System.out.println(3));

		String str = Switch(6, String.class).Case(7, () -> "abc").Case(6, "666").Default(() -> "null");
		System.out.println(str);

		For(1).to(21).step(2).loop(i -> {
			if (i < 4)
				Continue();
			if (i > 15)
				Break();
			System.out.println(i);
		});
		System.out.println(For(1).to(21).step(2).loop((Integer i, Integer res) -> {
			System.out.println(res);
			if (i < 4)
				return Continue();
			if (i > 15)
				return BreakWithResult(i + 100);
			else
				return i + 100;
		}));
		System.out.println(For(1).to(21).step(2).loop(i -> {
			if (i < 4)
				return Continue();
			if (i > 15)
				return Break();
			else
				return i + 100;
		}));

		System.out.println(While(() -> true, () -> {
			return BreakWithResult(1);
		}));

		System.out.println(
				If(For(1).to(10).loop(i -> {
					if (i > 11)
						return BreakWithResult(i);
					else
						return null;
				}),
						i -> i)
								.ElseIf(
										While(() -> true,
												(Integer res) -> {
													System.out.println(res);
													BreakWithResult(4);
												}),
										i -> i)
								.End());

	}

	void testArray() {
		System.out.println("Array Enhance");
		strArr = new String[] {
				"a", "b", "c", "d"
		};
		System.out.println($(strArr).first());
		System.out.println($(strArr).forEach(e -> {
			if (e.equals("b"))
				Continue();
			if (e.equals("d"))
				Break();
			return e;
		}));
		System.out.println((String) $(strArr).forEach((e, i) -> {
			if (e.equals("b"))
				Continue();
			if (e.equals("d"))
				Break();
			System.out.println("RES:" + i.lastRes);
			return e + i;
		}));
		$(strArr).forEach((e, i) -> {
			if (e.equals("b"))
				Continue();
			if (e.equals("d"))
				Break();
			System.out.println(e);
		});
		System.out.println($(strArr).to(new ArrayList<String>()).via((e) -> e));
	}

	void testCollection() {
		System.out.println("Collection Enhance");
		coll = $(new HashSet<>(), 1, 3, 5, 9);
		System.out.println($(coll).first());
		System.out.println($(coll).forEach(e -> {
			if (e.equals(3))
				Continue();
			if (e.equals(9))
				Break();
			return e;
		}));
		System.out.println((Integer) $(coll).forEach((e, i) -> {
			if (e.equals(3))
				Continue();
			if (e.equals(9))
				Break();
			return e + $(i) * 100;
		}));
		$(coll).forEach((e, i) -> {
			if (e.equals(1))
				Remove();
			if (e.equals(3))
				Continue();
			if (e.equals(9))
				Break();
			System.out.println(e + $(i) * 100);
		});
		list = $(coll).to(new ArrayList<Integer>()).via(e -> e);
		System.out.println(list);
	}

	void testList() {
		System.out.println("List Enhance");
		System.out.println($(list).first());
		System.out.println($(list).forEach(e -> {
			if (e.equals(3))
				Continue();
			if (e.equals(9))
				Break();
			return e;
		}));
		System.out.println((Integer) $(list).forEach((e, i) -> {
			if (e.equals(3))
				Continue();
			return e + $(i) * 100;
		}));
		$(list).forEach((e, i) -> {
			if (e.equals(3))
				Remove();
			if (e.equals(9))
				Set(10);
			System.out.println(e + $(i) * 100);
		});
		System.out.println($(list).to(new ArrayList<Integer>()).via(e -> e));
	}

	void testMap() {
		System.out.println("Map Enhance");
		map = $(new HashMap<String, Integer>(), map("cass", 1995).$("john", 1996).$("alpha", 1994));
		$(map).forEach((k, v, i) -> {
			if (k.equals("alpha"))
				Remove();
			System.out.println(k + v + $(i));
		});
		System.out.println($(map).forEach((k, v) -> {
			return k + v;
		}));
		System.out.println($(map).to(new ArrayList<Integer>()).via((k, v) -> v));
	}
}
