package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * класс берет дату из сайта sql.ru  и конвектирует ее в определенный формат
 */
public class SqlRuDateTimeParser implements DateTimeParser {

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
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd MM yy, HH:mm");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d MM yy, HH:mm");
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("dd MM yy");
        LocalDateTime result = null;

        String[] dates = parse.split(",");
        String[] dividedDate = dates[0].split(" ");

        if (dates[0].contains("сегодня") || dates[0].contains("вчера")) {
            if (dates[0].contains("сегодня")) {
                LocalDate localDate = LocalDate.now();
                String date = localDate.format(formatter3);
                String rsl = date + "," + dates[1];
                result = LocalDateTime.parse(rsl, formatter1);
            } else {
                LocalDate localDate = LocalDate.now().minusDays(1);
                String date = localDate.format(formatter3);
                String rsl = date + "," + dates[1];
                result = LocalDateTime.parse(rsl, formatter1);
            }
        } else {
            String rsl = dividedDate[0] + " " + MONTHS.get(dividedDate[1]) + " "
                    + dividedDate[2] + "," + dates[1];

            if (dividedDate[0].length() < 2) {
                result = LocalDateTime.parse(rsl, formatter2);
            } else {
                result = LocalDateTime.parse(rsl, formatter1);
            }
        }
        return result;
    }
}