package posts;

import org.helpers.BaseRequests;
import org.helpers.DBHelper;
import org.pojo.Post;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Iterator;

/**
 * Тесты создания поста.
 */
public final class CreatePostTests extends BaseTest {

    /**
     * Тест получения созданного поста через запрос к БД.
     */
    @Test(description = "Get in db created with REST API post", priority = 1)
    public void testCreatePost() {
        int postId = BaseRequests.createPost(postsId, postPojo);

        dbPostRow = DBHelper.selectPostById(postId);
        Iterator<String> col = dbPostRow.iterator();
        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(col.next(), postPojo.getStatus(), "Статус поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), postPojo.getTitle(), "Заголовок поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), postPojo.getContent(),"Содержание поста не совпадает с данными в БД");
        softAssertion.assertAll();
    }

    /**
     * Тест получения созданного поста через GET запрос.
     */
    @Test(description = "REST API GET created post test", priority = 2)
    public void testGetCreatedPost() {
        int postId = BaseRequests.createPost(postsId, postPojo);

        Post post = BaseRequests.getPostById(postId, 200);
        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(post.getStatus(), postPojo.getStatus(), "Статус поста не совпадает");
        softAssertion.assertEquals(post.getTitle(), postPojo.getTitle(), "Заголовок поста не совпадает");
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(post.getContent(), contentExpected, "Содержание поста не совпадает");
        softAssertion.assertAll();
    }
}
