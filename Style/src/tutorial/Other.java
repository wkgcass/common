package tutorial;

import java.util.Date;

import net.cassite.style.style;

public class Other extends style {

	public static void main(String[] args) {
		System.out.println("\nDate===============");
		Date date = new Date();
		System.out.println(date);
		$(date).add(d -> d.day(1).hour(2).minute(3).second(4).milli(5)).nextYear().nextMonth()
				.substract(d -> d.day(5).hour(4).minute(3).second(2).milli(1));
		System.out.println(date);
	}

}
