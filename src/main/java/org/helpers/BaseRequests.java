package org.helpers;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import org.pojo.Comment;
import org.pojo.Post;

/**
 * Базовый класс спецификации запроса.
 */
public final class BaseRequests {

    private BaseRequests() { }

    /**
     * Экземпляр PropertyProvider с загруженными локальными параметрами.
     */
    private static final PropertyProvider envLocalProvider = new PropertyProvider();

    /**
     * Экземпляр с загруженными секретами.
     */
    private static final PropertyProvider secretsProvider = new PropertyProvider("secrets.properties");

    /**
     * URL WordPress.
     */
    private static final String WP_URL = envLocalProvider.getProperty("base.url");

    /**
     * API для взаимодействия с постами.
     */
    private static final String WP_POSTS = "index.php?rest_route=/wp/v2/posts";

    /**
     * API для взаимодействия с постами.
     */
    private static final String WP_COMMENTS = "index.php?rest_route=/wp/v2/comments";

    /**
     * Логин для WordPress.
     */
    private static final String USERNAME = secretsProvider.getProperty("api.username");

    /**
     * Пароль для WordPress.
     */
    private static final String PASSWORD = secretsProvider.getProperty("api.password");

    /**
     * Подготовка спецификации запроса.
     */
    public static void initRequestSpecification() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setContentType(ContentType.JSON)
                .setBaseUri(WP_URL)
                .setAccept(ContentType.JSON);
        requestSpecification = requestSpecBuilder.build();
    }

    /**
     * Создание поста и добавление его id в список.
     * @param postsId список id
     * @param postPojo экземпляр создаваемого поста
     * @return id поста
     */
    public static int createPost(final List<Integer> postsId,
                                  final Post postPojo) {

        // Ожидаемое содержание в ответе
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";

        int postId = given()
                .spec(requestSpecification)
                .auth().preemptive().basic(USERNAME, PASSWORD)
                .body(postPojo)
                .when()
                    .post(WP_POSTS)
                .then()
                    .statusCode(201)
                    .body("id", instanceOf(Integer.class))
                    .body("status", equalTo(postPojo.getStatus()))
                    .body("title.rendered", equalTo(postPojo.getTitle()))
                    .body("content.rendered", equalTo(contentExpected))
                .extract()
                    .jsonPath().getInt("id");

        postsId.add(postId);
        return postId;
    }

    /**
     * Изменение поста.
     * @param postId id поста
     * @param postPojo экземпляр создаваемой поста
     */
    public static void patchPost(final int postId, final Post postPojo) {

        // Ожидаемое содержание в ответе
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";

        given()
            .spec(requestSpecification)
            .auth().preemptive().basic(USERNAME, PASSWORD)
            .body(postPojo)
            .pathParam("id", postId)
            .when()
                .patch(WP_POSTS + "/{id}")
            .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("status", equalTo(postPojo.getStatus()))
                .body("title.rendered", equalTo(postPojo.getTitle()))
                .body("content.rendered", equalTo(contentExpected));
    }

    /**
     * Получение поста по id.
     * @param postId id поста
     * @param statusCode ожидаемый статус-код
     * @return экземпляр поста
     */
    public static Post getPostById(final int postId, final int statusCode) {
        Response response = given()
                .spec(requestSpecification)
                .pathParam("id", postId)
                .when()
                    .get(WP_POSTS + "/{id}")
                .then()
                    .statusCode(statusCode)
                    .extract().response();

        return Post.builder()
                .status(response.jsonPath().getString("status"))
                .title(response.jsonPath().getString("title.rendered"))
                .content(response.jsonPath().getString("content.rendered"))
                .build();
    }

    /**
     * Удаление постов по списку id и его очистка.
     * @param postsId список id удаляемых постов
     */
    public static void deletePostsById(final List<Integer> postsId) {

        for (Integer postId : postsId) {
            given()
                    .spec(requestSpecification)
                    .auth().preemptive().basic(USERNAME, PASSWORD)
                    .pathParam("id", postId)
                    .when()
                        .delete(WP_POSTS + "/{id}&force=true")
                    .then()
                        .statusCode(200)
                        .body("deleted", equalTo(true))
                        .body("previous.id", equalTo(postId));
        }

        postsId.clear();
    }

    /**
     * Создание комментария и добавление его id в список.
     * @param commentsId список id комментариев
     * @param commentPojo экземпляр создаваемого поста
     * @return id комментария
     */
    public static int createComment(final List<Integer> commentsId,
                                       final Comment commentPojo) {

        // Ожидаемое содержание в ответе
        String contentExpected = "<p>" + commentPojo.getContent() + "</p>\n";

        int commId = given()
                .spec(requestSpecification)
                .auth().preemptive().basic(USERNAME, PASSWORD)
                .body(commentPojo)
                .when()
                    .post(WP_COMMENTS)
                .then()
                    .statusCode(201)
                    .body("id", instanceOf(Integer.class))
                    .body("post", equalTo(commentPojo.getPostId()))
                    .body("author_name", equalTo(commentPojo.getAuthorName()))
                    .body("author_email", equalTo(commentPojo.getAuthorEmail()))
                    .body("content.rendered", equalTo(contentExpected))
                .extract()
                    .jsonPath().getInt("id");

        commentsId.add(commId);
        return commId;
    }

    /**
     * Изменение комментария.
     * @param commId id комментария
     * @param commentPojo экземпляр создаваемого поста
     */
    public static void patchComment(final int commId, final Comment commentPojo) {

        // Ожидаемое содержание в ответе
        String contentExpected = "<p>" + commentPojo.getContent() + "</p>\n";

        given()
                .spec(requestSpecification)
                .auth().preemptive().basic(USERNAME, PASSWORD)
                .body(commentPojo)
                .pathParam("id", commId)
                .when()
                    .patch(WP_COMMENTS + "/{id}")
                .then()
                    .statusCode(200)
                    .body("id", equalTo(commId))
                    .body("post", equalTo(commentPojo.getPostId()))
                    .body("author_name", equalTo(commentPojo.getAuthorName()))
                    .body("author_email", equalTo(commentPojo.getAuthorEmail()))
                    .body("content.rendered", equalTo(contentExpected));
    }

    /**
     * Получение комментария по id.
     * @param commId id поста
     * @param statusCode ожидаемый статус-код
     * @return экземпляр поста
     */
    public static Comment getCommentById(final int commId, final int statusCode) {
        Response response = given()
                .spec(requestSpecification)
                .auth().preemptive().basic(USERNAME, PASSWORD)
                .pathParam("id", commId)
                .when()
                    .get(WP_COMMENTS + "/{id}")
                .then()
                    .statusCode(statusCode)
                    .extract().response();

        return Comment.builder()
                .postId((response.jsonPath().getInt("post")))
                .authorName(response.jsonPath().getString("author_name"))
                .authorEmail(response.jsonPath().getString("author_email"))
                .content(response.jsonPath().getString("content.rendered"))
                .build();
    }

    /**
     * Удаление комментариев по списку id и его очистка.
     * @param commentsId список id удаляемых комментариев
     */
    public static void deleteCommentsById(final List<Integer> commentsId) {

        for (Integer commId : commentsId) {
            given()
                    .spec(requestSpecification)
                    .auth().preemptive().basic(USERNAME, PASSWORD)
                    .pathParam("id", commId)
                    .when()
                        .delete(WP_COMMENTS + "/{id}&force=true")
                    .then()
                        .statusCode(200)
                        .body("deleted", equalTo(true))
                        .body("previous.id", equalTo(commId));
        }

        commentsId.clear();
    }

}
