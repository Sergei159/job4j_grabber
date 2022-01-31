package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * класс берет дату из сайта sql.ru  и конвектирует ее в определенный формат
 */
public class SqlRuDateTimeParser implements DateTimeParser {
    private final DateTimeFormatter dayIsGreaterThan10 = DateTimeFormatter.ofPattern("dd MM yy, HH:mm");
    private final DateTimeFormatter dayIsLessThan10 = DateTimeFormatter.ofPattern("d MM yy, HH:mm");
    private final DateTimeFormatter onlyDates = DateTimeFormatter.ofPattern("dd MM yy");

    private static final Map<String, String> MONTHS = Map.ofEntries(
            Map.entry("янв", "01"),
            Map.entry("фев", "02"),
            Map.entry("мар", "03"),
            Map.entry("апр", "04"),
            Map.entry("май", "05"),
            Map.entry("июн", "06"),
            Map.entry("июл", "07"),
            Map.entry("авг", "08"),
            Map.entry("сен", "09"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")

    );

    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime result = null;
        LocalDate localDate = null;
        String[] dates = parse.split(",");
        String[] dividedDate = dates[0].split(" ");

        if (dates[0].contains("сегодня") || dates[0].contains("вчера")) {
            if (dates[0].contains("сегодня")) {
                localDate = LocalDate.now();
            } else {
                localDate = LocalDate.now().minusDays(1);
            }
            String date = localDate.format(onlyDates);
            String dateAndTime = date + "," + dates[1];
            result = LocalDateTime.parse(dateAndTime, dayIsGreaterThan10);

        } else {
            String rsl = dividedDate[0] + " " + MONTHS.get(dividedDate[1]) + " "
                    + dividedDate[2] + "," + dates[1];

            if (dividedDate[0].length() < 2) {
                result = LocalDateTime.parse(rsl, dayIsLessThan10);
            } else {
                result = LocalDateTime.parse(rsl, dayIsGreaterThan10);
            }
        }
        return result;
    }
}