package posts.comments;

import org.helpers.BaseRequests;
import org.helpers.DBHelper;
import org.pojo.Comment;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Iterator;

/**
 * Тесты удаления комментария.
 */
public final class DeleteCommentTests extends CommentsBaseTest {

    /**
     * Тест получения удалённого комментария через запрос к БД.
     */
    @Test(description = "Get in db deleted with REST API comment", priority = 1)
    public void testDeleteComment() {
        int postId = BaseRequests.createPost(postsId, postPojo);
        commentPojo.setPostId(postId);
        BaseRequests.createComment(commentsId, commentPojo);

        BaseRequests.deleteCommentsById(commentsId);

        dbCommRow = DBHelper.selectCommentById(postId);
        Iterator<String> col = dbCommRow.iterator();
        Assert.assertFalse(col.hasNext(), "Комментарий не удалился из базы данных");
    }

    /**
     * Тест получения удалённого комментария через GET запрос.
     */
    @Test(description = "REST API GET deleted comment test", priority = 2)
    public void testGetDeletedComment() {
        int postId = BaseRequests.createPost(postsId, postPojo);
        commentPojo.setPostId(postId);
        BaseRequests.createComment(commentsId, commentPojo);

        BaseRequests.deleteCommentsById(commentsId);

        Comment comment = BaseRequests.getCommentById(postId, 404);
        Assert.assertNull(comment.getContent(), "Пост не удалился");
    }

}
