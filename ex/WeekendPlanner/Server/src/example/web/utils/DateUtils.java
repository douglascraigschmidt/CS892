package example.web.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import example.web.model.Flight;

/**
 * A utility class to compute appropriate times and time ranges
 */
public class DateUtils {
	
	public static String getFormattedDateOfNext(DayOfWeek day) {
		return getDateOfNext(day)
			.format(DateTimeFormatter.ISO_LOCAL_DATE);
	}
	
	private static LocalDate getDateOfNext(DayOfWeek day) {
		LocalDate today = LocalDate.now();
		return day == DayOfWeek.FRIDAY ?
			today.with(TemporalAdjusters.nextOrSame(day)) :
			today.plusDays(1).with(TemporalAdjusters.next(day));
	}
	
	public static String makeDateTimeRange(Flight flight) {
		return removeSeconds(flight.getDepartingArrivalDateTime())
			+ " TO "
			+ removeSeconds(flight.getReturningDepartureDateTime());
	}
	
	public static Integer getNumDaysUntilNext(DayOfWeek day) {
		LocalDate today = LocalDate.now();
		return Period.between(today, getDateOfNext(day)).getDays()
				+ (today.getDayOfWeek().compareTo(DayOfWeek.FRIDAY) > 0 ? 7 : 0);
	}
	
	private static String removeSeconds(String dateTime) {
		return dateTime.substring(0, dateTime.lastIndexOf(":"));
	}
	
}
