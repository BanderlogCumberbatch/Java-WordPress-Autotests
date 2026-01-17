import org.helpers.BaseRequests;
import org.pojo.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс тестов API для WordPress.
 */
public class APITest {

    /**
     * Список ID постов.
     */
    private final List<Integer> postsId = new ArrayList<>();

    /**
     * Список ID постов.
     */
    private final List<Integer> commentsId = new ArrayList<>();

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

    @BeforeClass
    public void setup() {
        BaseRequests.initRequestSpecification();
    }

    /**
     * Тест создания поста.
     */
    @Test(description = "Posts API test", priority = 1)
    public void testCreatePost() {
        int postId = BaseRequests.createPost(postsId, postPojo);

        Post post = BaseRequests.getPostById(postId, 200);

        SoftAssert softAssertion = new SoftAssert();
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

        Post post = BaseRequests.getPostById(postId, 200);

        SoftAssert softAssertion = new SoftAssert();
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

        Comment comment = BaseRequests.getCommentById(commentId, 200);

        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(comment.getPostId(), postId, "Id поста не совпадает");
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

        Comment comment = BaseRequests.getCommentById(commentId, 200);

        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(comment.getPostId(), postId, "Id поста не совпадает");
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
        int commentId = BaseRequests.createComment(commentsId, commentPojo);

        BaseRequests.deleteCommentsById(commentsId);

        Post post = BaseRequests.getPostById(commentId, 404);

        Assert.assertNull(post.getStatus(), "Комментарий не удалился");
    }

    @AfterClass()
    public void clearPosts() {
        if (!postsId.isEmpty()) {
            BaseRequests.deletePostsById(postsId);
        }
    }
}
