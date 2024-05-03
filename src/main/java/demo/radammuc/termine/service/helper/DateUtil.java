package demo.radammuc.termine.service.helper;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX");
    final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    public static LocalDateTime fromString(String date) {
        return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
    }

    public static String fromDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * @param dateTime     LocalDateTime
     * @param slotDuration Duration in minutes
     * @return The start of the slot
     */
    public static LocalDateTime getSlotStart(LocalDateTime dateTime, int slotDuration) {
        LocalTime time = getSlotStart(dateTime.toLocalTime(), slotDuration);
        return dateTime.withHour(time.getHour()).withMinute(time.getMinute());
    }

    /**
     * Determine the start of the slot that fits best to the given time with slotStart <= given minute
     * Assumption: The first slot within an hour starts always at :00.
     *
     * @param time         LocalTime
     * @param slotDuration Slot duration in minutes
     * @return The start of the slot
     */
    public static LocalTime getSlotStart(LocalTime time, int slotDuration) {
        int minute = time.getMinute();
        int slotWithinHour = minute / slotDuration;
        int lowerSlotBoundary = slotWithinHour * slotDuration; // must be <= minute

        return (minute == lowerSlotBoundary) ? time : time.withMinute(lowerSlotBoundary);
    }

    public static LocalTime timeFromString(String time) {
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    public static LocalTime min(LocalTime a, LocalTime b) {
        return a.isBefore(b) ? a : b;
    }

    public static LocalTime max(LocalTime a, LocalTime b) {
        return a.isAfter(b) ? a : b;
    }
}
