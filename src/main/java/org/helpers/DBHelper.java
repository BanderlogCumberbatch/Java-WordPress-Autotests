package org.helpers;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * Класс для работы с БД wordpress.
 */
public final class DBHelper {

    private static Connection connection;
    private static Statement statement;

    /**
     * Открытие соединения с БД.
     * @param baseUrl url БД WordPress
     * @param user пользователь БД WordPress
     * @param password пароль БД WordPress
     */
    public static void connect(String baseUrl, String user, String password) {
        try {
            System.out.println("Открывается соединение с БД");
            Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", password);
            connection = DriverManager.getConnection(
                        baseUrl,
                        props);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Закрытие соединения с БД.
     */
    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Соединение с БД закрыто");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Поиск данных поста в БД по id.
     * @param postId id поста
     * @return Список с данными поста (статус, заголовок, содержание).
     * Если ничего не найдено, то пустой список
     */
    public static List<String> selectPostById(final int postId) {
        try (ResultSet rs = statement.executeQuery(
                "SELECT id, post_status, post_title, post_content"
                        + " FROM wordpress.wp_posts"
                        + " WHERE id = " + postId)) {
            rs.next();
            return List.of(rs.getString("post_status"),
                            rs.getString("post_title"),
                            rs.getString("post_content"));
        } catch (SQLException e) {
            return List.of();
        }
    }

    /**
     * Поиск данных комментария в БД по id.
     * @param commId id комментария
     * @return Список с данными поста (id поста, имя автора, email автора, содержание).
     * Если ничего не найдено, то пустой список
     */
    public static List<String> selectCommentById(final int commId) {
        try (ResultSet rs = statement.executeQuery(
                "SELECT comment_id, comment_post_id, comment_author,"
                        + " comment_author_email, comment_content"
                        + " FROM wordpress.wp_comments"
                        + " WHERE comment_id = " + commId)) {
            rs.next();
            return List.of(rs.getString("comment_post_id"),
                            rs.getString("comment_author"),
                            rs.getString("comment_author_email"),
                            rs.getString("comment_content"));
        } catch (SQLException e) {
            return List.of();
        }
    }

}
