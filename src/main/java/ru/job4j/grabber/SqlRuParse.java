package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс парсит список постов и их описание из веб-страницы.
 */
public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }



    /**
     *
     * @param link ссылка на веб-страницу
     * @return  список всех постов.
     */

    @Override
    public List<Post> list(String link) {
        int countOfPages = 5;
        String toShow = "Java";
        String notToShow = "Script";
        List<Post> posts = new ArrayList<>();
        StringBuilder url = new StringBuilder(link);
        for (int i = 0; i < countOfPages; i++) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url + "/" + (i + 1)).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                if ((href.text().contains(toShow) || href.text().contains(toShow.toLowerCase()) || href.text().contains(toShow.toUpperCase()))
                && !(href.text().contains(notToShow) || href.text().contains(notToShow.toLowerCase()) || href.text().contains(notToShow.toUpperCase()))) {
                    System.out.println(href.text());
                    posts.add(detail(href.attr("href")));
                }

            }
        }
        return posts;
    }

    /**
     *   метод загружает все детали одного поста
     * @param link ссылка на определенный пост
     * @return объект класса Post
     */
    @Override
    public Post detail(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String parsedTitle = doc.select(".messageHeader").get(0).ownText();

        String parsedDescription = doc.select(".msgBody").get(1).text();

        String dateText = doc.select(".msgFooter").first().text();

        String[] dividedDate = dateText.split("\\[");
        dateText = dividedDate[0];

        LocalDateTime time = dateTimeParser.parse(dateText);
        Post post = new Post(parsedTitle, link, parsedDescription, time);
        return post;
    }

    public static void main(String[] args) {

    }

}