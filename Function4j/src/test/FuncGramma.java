package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cassite.function.$f;
import net.cassite.function.Entry;

public class FuncGramma extends $f {
	public static void main(String[] args) {
		Integer[] intArr = new Integer[] { 1, 2, 3, 5, 8, 10 };

		$(intArr).forEach((e) -> System.out.println(e));

		List<String> strList = new ArrayList<>();
		$(strList).add("123").add("456").add("1024");

		$(strList).forEach((e) -> System.out.println(e));

		Map<String, Integer> map = new HashMap<String, Integer>();
		$(map).append("cass", 1995).append("john", 1994).append("cassie", 1996);
		$(map).forEach((k, v) -> {
			System.out.println(k + " " + v);
		});

		For(0, (i) -> i < 10, (i) -> i + 1, (i) -> System.out.println(i));
		Iterator<String> strIt = strList.iterator();
		While(() -> strIt.hasNext(), () -> System.out.println(strIt.next()));

		List<Integer> arrayToList = $(intArr).to(new ArrayList<Integer>()).via((e) -> {
			return e + 100;
		});
		$(arrayToList).forEach((e) -> System.out.println(e));

		Set<String> collectionToSet = $(strList).to(new HashSet<String>()).via((e) -> e + ",abc");
		$(collectionToSet).forEach((e) -> System.out.println(e));

		List<String> mapToList = $(map).to(new ArrayList<String>()).via((k, v) -> "key is " + k + " value is " + v);
		$(mapToList).forEach((e) -> System.out.println(e));

		Map<String, Integer> mapToMap = $(map).to(new HashMap<String, Integer>())
				.via((k, v) -> new Entry<>(k + ",abc", v + 200));
		$(mapToMap).forEach((k, v) -> System.out.println("key is " + k + " value is " + v));

		$(intArr).forEach((e) -> {
			if (e == 3)
				throw Continue;
			if (e == 8)
				throw Break;
			System.out.println(e);
		});

		$(map).forEach((k, v) -> {
			if (k.equals("john"))
				throw Remove;
		});
		System.out.println(map);

		function<Integer> func = $((Integer i, Integer end, function<Integer> fun) -> {
			if (i < end) {
				return i + fun.apply(i + 1, end, fun);
			} else {
				return i;
			}
		});
		System.out.println(func.apply(1, 10, func));
	}
}
