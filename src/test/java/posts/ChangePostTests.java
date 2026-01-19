package posts;

import org.helpers.BaseRequests;
import org.helpers.DBHelper;
import org.pojo.Post;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Iterator;

public final class ChangePostTests extends BaseTest {

    /**
     * Тест получения изменённого поста через запрос к БД.
     */
    @Test(description = "Get in db changed with REST API post", priority = 1)
    public void testChangePost() {
        int postId = BaseRequests.createPost(postsId, postPojo);

        postPojo.setTitle("New new post");
        BaseRequests.patchPost(postId, postPojo);

        dbPostRow = DBHelper.selectPostById(postId);
        Iterator<String> col = dbPostRow.iterator();
        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(col.next(), postPojo.getStatus(), "Статус поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), postPojo.getTitle(), "Заголовок поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), postPojo.getContent(),"Содержание поста не совпадает с данными в БД");
        softAssertion.assertAll();
    }

    /**
     * Тест получения изменённого поста через GET запрос.
     */
    @Test(description = "REST API GET changed post test", priority = 2)
    public void testGetChangedPost() {
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

}
