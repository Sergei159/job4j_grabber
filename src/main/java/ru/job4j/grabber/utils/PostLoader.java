package ru.job4j.grabber.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Класс парсит страницу по ссылке link и вводит данные в класс Post
 */
public class PostLoader {
     String parsedTitle;
     private final String link = "https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t";
     String parsedDescription;
     LocalDateTime parsedLocalDateTime;

    public Post loadPost() throws IOException {
        Document doc = Jsoup.connect(link).get();
        Elements title = doc.select(".msgTable").select("td[class=messageHeader]");
        parsedTitle = title.get(0).text();

        Elements description = doc.select(".msgBody");
        parsedDescription = description.get(1).text();

        Elements date = doc.select(".msgTable").select("td[class=msgFooter]");
        String dateText = date.get(0).text();
        String[] dividedDate = dateText.split(" ");
        dateText = dividedDate[0] + " " + dividedDate[1] + " " + dividedDate[2] + " " + dividedDate[3];
        SqlRuDateTimeParser sqlRuDateTimeParser = new SqlRuDateTimeParser();
        parsedLocalDateTime = sqlRuDateTimeParser.parse(dateText);

        Post post = new Post(parsedTitle, link, parsedDescription, parsedLocalDateTime);
        return post;
    }

    public static void main(String[] args) throws IOException {
        PostLoader postLoader = new PostLoader();
        Post post = postLoader.loadPost();
        System.out.println(post.toString());
    }
}
