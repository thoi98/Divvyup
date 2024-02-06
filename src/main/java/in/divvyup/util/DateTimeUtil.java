package in.divvyup.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DateTimeUtil {
    public static ZonedDateTime currentTime() {
        return currentTime(ZoneOffset.UTC);
    }

    public static ZonedDateTime startOfDay() {
        return startOfDay(currentTime());
    }

    public static ZonedDateTime startOfDay(ZonedDateTime time) {
        return time.withHour(0).withMinute(0).withSecond(0);
    }

    public static ZonedDateTime startOfYear(int year) {
        return ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    }

    public static ZonedDateTime withDaysAdjusted(int numberOfDays) {
        return withDaysAdjusted(currentTime(), numberOfDays);
    }

    public static ZonedDateTime withDaysAdjusted(ZonedDateTime dateTime, int numberOfDays) {
        return dateTime.plusDays(numberOfDays);
    }

    public static ZonedDateTime withHoursAdjusted(int numberOfHours) {
        return withHoursAdjusted(currentTime(), numberOfHours);
    }

    public static ZonedDateTime withHoursAdjusted(ZonedDateTime dateTime, int numberOfHours) {
        return dateTime.plusHours(numberOfHours);
    }

    public static ZonedDateTime withMinutesAdjusted(ZonedDateTime dateTime, int numberOfMinutes) {
        return dateTime.plusMinutes(numberOfMinutes);
    }

    public static ZonedDateTime withMinutesAdjusted(int numberOfMinutes) {
        return withMinutesAdjusted(currentTime(), numberOfMinutes);
    }

    public static ZonedDateTime withSecondsAdjusted(long numberOfSeconds) {
        return withSecondsAdjusted(currentTime(), numberOfSeconds);
    }

    public static ZonedDateTime withSecondsAdjusted(ZonedDateTime time, long numberOfSeconds) {
        return time.plusSeconds(numberOfSeconds);
    }

    public static ZonedDateTime withMonthsAdjusted(int numberOfMonths) {
        return withMonthsAdjusted(currentTime(), numberOfMonths);
    }

    public static ZonedDateTime withMonthsAdjusted(ZonedDateTime time, int numberOfMonths) {
        return time.plusMonths(numberOfMonths);
    }

    public static ZonedDateTime fromInstant(Instant instant) {
        return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static ZonedDateTime fromEpochMilli(long epochMilli) {
        return fromInstant(Instant.ofEpochMilli(epochMilli));
    }

    public static ZonedDateTime currentTime(ZoneOffset zoneOffset) {
        return ZonedDateTime.now(zoneOffset);
    }

    public static String formattedDate(String format) {
        return formattedDate(currentTime(), format);
    }

    public static String formattedDate(ZonedDateTime time, String format) {
        return DateTimeFormatter.ofPattern(format).format(time);
    }

    public static ZonedDateTime parseDateTimeWithZone(String time, String format) {
        return ZonedDateTime.parse(time, DateTimeFormatter.ofPattern(format));
    }

    public static ZonedDateTime parseDateTimeWithoutZone(String time, String format, ZoneId zoneId) {
        return ZonedDateTime.parse(time, DateTimeFormatter.ofPattern(format).withZone(zoneId));
    }

    public static ZonedDateTime parseDateWithoutZone(String date, String format, ZoneId zoneId) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(format)).atStartOfDay(zoneId);
    }

    public static List<ZonedDateTime> getHourlyTimesBetween(ZonedDateTime startTime, ZonedDateTime endTime, int hourGap) {
        List<ZonedDateTime> times = new ArrayList<>();
        ZonedDateTime currentTime = startTime.withMinute(0).withSecond(0);
        while (currentTime.isBefore(endTime)) {
            times.add(currentTime);
            currentTime = currentTime.plusHours(hourGap);
        }
        return times.subList(1, times.size());
    }

    public static ZonedDateTime getTimeFromResultSet(ResultSet resultSet, String columnName) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columnName);
        if (Objects.isNull(timestamp)) {
            return null;
        }
        return fromInstant(timestamp.toInstant());
    }
}
