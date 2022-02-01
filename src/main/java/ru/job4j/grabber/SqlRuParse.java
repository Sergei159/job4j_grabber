package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
        List<Post> posts = new ArrayList<>();
        int countOfPages = 5;
        StringBuilder url = new StringBuilder(link);
        for (int i = 0; i < countOfPages; i++) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url.toString() + "/" + (i + 1)).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                posts.add(detail(href.attr("href")));
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

        String dateText = doc.select(".msgFooter").get(0).text();

        String[] dividedDate = dateText.split("", 4);
        dateText = dividedDate[0];

        dateTimeParser.parse(dateText);
        Post post = new Post(parsedTitle, link, parsedDescription, dateTimeParser);
        return post;
    }
}