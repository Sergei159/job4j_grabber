package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

/**
 * Класс выводит на консоль определнные данные из веб-страницы.
 */
public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        int countOfPages = 5;
        StringBuilder url = new StringBuilder("https://www.sql.ru/forum/job-offers");
        for (int i = 0; i < countOfPages; i++) {
            Document doc = Jsoup.connect(url.toString() + "/" + (i + 1)).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element date = td.parent().child(5);
                System.out.println(date.text());
                SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
                System.out.println(timeParser.parse(date.text()));
            }

        }


    }
}