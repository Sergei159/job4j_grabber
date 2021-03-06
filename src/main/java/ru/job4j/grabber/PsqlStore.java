package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
            Class.forName(cfg.getProperty("driver-class-name"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (PsqlStore psqlStore = new PsqlStore(properties)) {
            Post post1 = new Post(
                    "name1",
                    "link",
                    "description1",
                    LocalDateTime.now()
            );
            Post post2 = new Post(
                    "name2",
                    "link2",
                    "description2",
                    LocalDateTime.now()
            );
            psqlStore.save(post1);
            psqlStore.save(post2);
            System.out.println("save:");
            System.out.println(post1);
            System.out.println(post2);
            System.out.println("find by id:");
            System.out.println(psqlStore.findById(post2.getId()));
            List<Post> posts = psqlStore.getAll();
            System.out.println("get all");
            System.out.println(posts);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        @Override
    public void save(Post post) {
        Timestamp timestamp = Timestamp.valueOf(post.getCreated());
        try (PreparedStatement ps = cnn.prepareStatement(
                "insert into post(name, text, link, created) values (?, ?, ?, ?)",
                             Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, timestamp);
            ps.execute();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement ps = cnn.prepareStatement("Select * from post")) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Post item = setPostData(resultSet);
                    posts.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement ps = cnn.prepareStatement(
                "Select * from post where id = ?")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    post = setPostData(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }


    public Post setPostData(ResultSet resultSet) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("created");
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("link"),
                resultSet.getString("text"),
                timestamp.toLocalDateTime()
        );

    }
}
