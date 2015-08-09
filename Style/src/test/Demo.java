package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cassite.style.style;
import net.cassite.style.Async;
import net.cassite.style.Entry;

public class Demo extends style {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		System.out.println("forEach===================");
		System.out.println("on array:");

		Integer[] intArr = new Integer[] { 1, 2, 3, 5, 8, 10 };
		$(intArr).forEach((e) -> System.out.println(e));

		System.out.println("on collection (such as list):");

		List<String> strList = new ArrayList<>();
		$(strList).add("123").add("456").add("1024");
		$(strList).forEach((e) -> System.out.println(e));

		System.out.println("on map:");

		Map<String, Integer> map = new HashMap<String, Integer>();
		$(map).append("cass", 1995).append("john", 1994).append("cassie", 1996);
		$(map).forEach((k, v) -> {
			System.out.println(k + " " + v);
		});

		System.out.println("on iterable, 'on collection' extends 'on iterable' so there's no need to demostrate");

		System.out.println("\nLoop===========");
		System.out.println("For:");
		For(0, (i) -> i < 10, (i) -> i + 1, (i) -> System.out.println(i));
		System.out.println("While:");
		Iterator<String> strIt = strList.iterator();
		While(() -> strIt.hasNext(), () -> System.out.println(strIt.next()));

		System.out.println("\nTransform==========");
		System.out.println("array to collection");
		List<Integer> arrayToList = $(intArr).to(new ArrayList<Integer>()).via((e) -> {
			return e + 100;
		});
		$(arrayToList).forEach((e) -> System.out.println(e));

		System.out.println("iterator to collection");
		Set<String> collectionToSet = $(strList).to(new HashSet<String>()).via((e) -> e + ",abc");
		$(collectionToSet).forEach((e) -> System.out.println(e));

		System.out.println("map to collection");
		List<String> mapToList = $(map).to(new ArrayList<String>()).via((k, v) -> "key is " + k + " value is " + v);
		$(mapToList).forEach((e) -> System.out.println(e));

		System.out.println("map to map");
		Map<String, Integer> mapToMap = $(map).to(new HashMap<String, Integer>())
				.via((k, v) -> new Entry<>(k + ",abc", v + 200));
		$(mapToMap).forEach((k, v) -> System.out.println("key is " + k + " value is " + v));

		System.out.println("loop control============");
		System.out.println("break and continue");
		$(intArr).forEach((e) -> {
			if (e == 3)
				throw Continue;
			if (e == 8)
				throw Break;
			System.out.println(e);
		});
		System.out.println("remove");
		$(map).forEach((k, v) -> {
			if (k.equals("john"))
				throw Remove;
		});
		System.out.println(map);

		System.out.println("\nfunction===========");
		function<Integer> func = $((Integer i, Integer end, function<Integer> fun) -> {
			if (i < end) {
				return i + fun.apply(i + 1, end, fun);
			} else {
				return i;
			}
		});
		System.out.println(func.apply(1, 10, func));

		System.out.println("async/await and callback=============");
		function<Double> oneToTen = $(() -> {
			System.out.println(Thread.currentThread());
			double mul = 1;
			for (double i = 10; i >= 1; --i) {
				mul *= i;
			}
			return mul;
		});
		function<Double> elevenToTwenty = $(() -> {
			System.out.println(Thread.currentThread());
			double mul = 1;
			for (double i = 20; i >= 11; --i) {
				mul *= i;
			}
			return mul;
		});
		function<Double> twentyOneToThirty = $(() -> {
			System.out.println(Thread.currentThread());
			double mul = 1;
			for (double i = 30; i >= 21; --i) {
				mul *= i;
			}
			return mul;
		});
		function<Double> ThirtyOneToForty = $(() -> {
			System.out.println(Thread.currentThread());
			double mul = 1;
			for (double i = 40; i >= 31; --i) {
				mul *= i;
			}
			return mul;
		});

		$(oneToTen.async(), elevenToTwenty.async(), twentyOneToThirty.async(), ThirtyOneToForty.async())
				.callback($((Double r1, Double r2, Double r3, Double r4) -> {
					System.out.println(r1 * r2 * r3 * r4);
					return Void.TYPE;
				}));
		System.out.println("\nasync a function and retrieve values at the end of the program...");
		Async<Double> async = oneToTen.async();

		System.out.println("Throwable support============");
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {
			$(e1).throwIn(RuntimeException.class);
			$(e1).throwNotIn(Error.class);
		}

		System.out.println("\nCollection enhanced with forThose");
		$(strList).forThose((e) -> true, (e) -> System.out.println(e));
		$(map).forThose((k, v) -> true, (k, v) -> System.out.println(k + " " + v));

		System.out.println("\nretrieve the async value using await:");
		System.out.println(await(async));
		System.out.println("or use async.await()");
		System.out.println(async.await());

		System.out.println("\nCollection builder===============");
		List<Integer> list = $(new ArrayList<Integer>(), 1, 2, 3, 4);
		$(list).forEach((e) -> System.out.println(e));
	}
}
