package posts;

import org.helpers.BaseRequests;
import org.helpers.DBHelper;
import org.pojo.Post;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Iterator;

public final class DeletePostTests extends BaseTest {

    /**
     * Тест получения удалённого поста через запрос к БД.
     */
    @Test(description = "Get in db deleted with REST API post", priority = 1)
    public void testDeletePost() {
        int postId = BaseRequests.createPost(postsId, postPojo);

        BaseRequests.deletePostsById(postsId);

        dbPostRow = DBHelper.selectPostById(postId);
        Iterator<String> col = dbPostRow.iterator();
        Assert.assertFalse(col.hasNext(), "Пост не удалился");
    }

    /**
     * Тест получения удалённого поста через GET запрос.
     */
    @Test(description = "REST API GET deleted post test", priority = 2)
    public void testGetDeletedPost() {
        int postId = BaseRequests.createPost(postsId, postPojo);

        BaseRequests.deletePostsById(postsId);

        Post post = BaseRequests.getPostById(postId, 404);
        Assert.assertNull(post.getContent(), "Пост не удалился");
    }

}
