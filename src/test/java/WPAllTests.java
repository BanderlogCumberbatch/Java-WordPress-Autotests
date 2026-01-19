import org.helpers.BaseRequests;
import org.helpers.DBHelper;
import org.helpers.PropertyProvider;
import org.pojo.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Объединённый класс тестов REST API для WordPress.
 */
public class WPAllTests {

    /**
     * Список ID постов.
     */
    private final List<Integer> postsId = new ArrayList<>();

    /**
     * Список ID постов.
     */
    private final List<Integer> commentsId = new ArrayList<>();

    /**
     * Список с данными поста из базы данных.
     */
    private List<String> dbPostRow = new ArrayList<>();

    /**
     * Список с данными поста из базы данных.
     */
    private List<String> dbCommRow = new ArrayList<>();

    /**
     * Создание pojo запроса для взаимодействия с постами.
     */
    private final Post postPojo = Post.builder()
            .status("publish")
            .title("New post")
            .content("sample text")
            .build();

    /**
     * Создание pojo запроса для взаимодействия с комментариями.
     */
    private final Comment commentPojo = Comment.builder()
            .authorName("Anon")
            .authorEmail("unowen@unknown.com")
            .content("sample content")
            .build();

    /**
     * Экземпляр PropertyProvider с загруженными локальными параметрами.
     */
    private final PropertyProvider envLocalProvider = new PropertyProvider();

    /**
     * Экземпляр с загруженными секретами.
     */
    private final PropertyProvider secretsProvider = new PropertyProvider("secrets.properties");

    @BeforeClass
    public void setup() {
        BaseRequests.initRequestSpecification(
                envLocalProvider.getProperty("base.url"),
                secretsProvider.getProperty("api.username"),
                secretsProvider.getProperty("api.password"));
        DBHelper.connect(envLocalProvider.getProperty("db.url"), secretsProvider.getProperty("db.user"), secretsProvider.getProperty("db.password"));
    }

    /**
     * Тест создания поста.
     */
    @Test(description = "Posts API test", priority = 1)
    public void testCreatePost() {
        int postId = BaseRequests.createPost(postsId, postPojo);

        // Проверка с данными БД
        dbPostRow = DBHelper.selectPostById(postId);
        Iterator<String> col = dbPostRow.iterator();
        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(col.next(), postPojo.getStatus(), "Статус поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), postPojo.getTitle(), "Заголовок поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), postPojo.getContent(),"Содержание поста не совпадает с данными в БД");
        softAssertion.assertAll();

        // Проверка с поиском по API
        Post post = BaseRequests.getPostById(postId, 200);
        softAssertion.assertEquals(post.getStatus(), postPojo.getStatus(), "Статус поста не совпадает");
        softAssertion.assertEquals(post.getTitle(), postPojo.getTitle(), "Заголовок поста не совпадает");
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание поста не совпадает");
        softAssertion.assertAll();
    }

    /**
     * Тест изменения поста.
     */
    @Test(description = "Change post API test", priority = 2)
    public void testChangePost() {
        int postId = BaseRequests.createPost(postsId, postPojo);

        postPojo.setTitle("New new post");
        BaseRequests.patchPost(postId, postPojo);

        // Сравнение с данными БД
        dbPostRow = DBHelper.selectPostById(postId);
        Iterator<String> col = dbPostRow.iterator();
        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(col.next(), postPojo.getStatus(), "Статус поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), postPojo.getTitle(), "Заголовок поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), postPojo.getContent(),"Содержание поста не совпадает с данными в БД");
        softAssertion.assertAll();

        // Проверка с поиском по API
        Post post = BaseRequests.getPostById(postId, 200);
        softAssertion.assertEquals(post.getStatus(), postPojo.getStatus(), "Статус поста не совпадает");
        softAssertion.assertEquals(post.getTitle(), postPojo.getTitle(), "Заголовок поста не совпадает");
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание поста не совпадает");
        softAssertion.assertAll();
    }

    /**
     * Тест удаления поста
     */
    @Test(description = "Delete post API test", priority = 3)
    public void testDeletePost() {
        int postId = BaseRequests.createPost(postsId, postPojo);

        BaseRequests.deletePostsById(postsId);

        // Сравнение с данными БД
        dbPostRow = DBHelper.selectPostById(postId);
        Iterator<String> col = dbCommRow.iterator();
        Assert.assertFalse(col.hasNext(), "Пост не удалился");

        // Проверка с поиском по API
        Post post = BaseRequests.getPostById(postId, 404);
        Assert.assertNull(post.getStatus(), "Пост не удалился");
    }

    /**
     * Тест создания комментария.
     */
    @Test(description = "Posts API test", priority = 4)
    public void testCreateComment() {
        int postId = BaseRequests.createPost(postsId, postPojo);
        commentPojo.setPostId(postId);
        int commentId = BaseRequests.createComment(commentsId, commentPojo);

        // Проверка с данными БД
        dbCommRow = DBHelper.selectCommentById(commentId);
        Iterator<String> col = dbCommRow.iterator();
        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(Integer.parseInt(col.next()), commentPojo.getPostId(), "ID поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), commentPojo.getAuthorName(), "Имя автора комментария не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), commentPojo.getAuthorEmail(), "Email автора комментария не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), commentPojo.getContent(),"Содержание комментария не совпадает с данными в БД");
        softAssertion.assertAll();

        // Проверка с поиском по API
        Comment comment = BaseRequests.getCommentById(commentId, 200);
        softAssertion.assertEquals(comment.getPostId(), postId, "ID поста не совпадает");
        softAssertion.assertEquals(comment.getAuthorName(), commentPojo.getAuthorName(), "Имя автора комментария не совпадает");
        String contentExpected = "<p>" + commentPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(comment.getContent(), contentExpected,"Содержание комментария не совпадает");
        softAssertion.assertAll();
    }

    /**
     * Тест изменения комментария.
     */
    @Test(description = "Change post API test", priority = 5)
    public void testChangeComment() {
        int postId = BaseRequests.createPost(postsId, postPojo);
        commentPojo.setPostId(postId);
        int commentId = BaseRequests.createComment(commentsId, commentPojo);

        commentPojo.setAuthorEmail("unowen@unknown.com");
        BaseRequests.patchComment(commentId, commentPojo);

        // Проверка с данными БД
        dbCommRow = DBHelper.selectCommentById(commentId);
        Iterator<String> col = dbCommRow.iterator();
        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(Integer.parseInt(col.next()), commentPojo.getPostId(), "ID поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), commentPojo.getAuthorName(), "Имя автора комментария не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), commentPojo.getAuthorEmail(), "Email автора комментария не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), commentPojo.getContent(),"Содержание комментария не совпадает с данными в БД");
        softAssertion.assertAll();

        // Проверка с поиском по API
        Comment comment = BaseRequests.getCommentById(commentId, 200);
        softAssertion.assertEquals(comment.getPostId(), postId, "ID поста не совпадает");
        softAssertion.assertEquals(comment.getAuthorName(), commentPojo.getAuthorName(), "Имя автора комментария не совпадает");
        String contentExpected = "<p>" + commentPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(comment.getContent(), contentExpected,"Содержание комментария не совпадает");
        softAssertion.assertAll();
    }

    /**
     * Тест удаления комментария
     */
    @Test(description = "Delete post API test", priority = 6)
    public void testDeleteComment() {
        int postId = BaseRequests.createPost(postsId, postPojo);
        commentPojo.setPostId(postId);
        BaseRequests.createComment(commentsId, commentPojo);

        BaseRequests.deleteCommentsById(commentsId);

        // Сравнение с данными БД
        dbCommRow = DBHelper.selectCommentById(postId);
        Iterator<String> col = dbCommRow.iterator();
        Assert.assertFalse(col.hasNext(), "Комментарий не удалился из базы данных");
    }

    @AfterClass()
    public void clearPosts() {
        DBHelper.disconnect();
        if (!postsId.isEmpty()) {
            BaseRequests.deletePostsById(postsId);
        }
    }
}
