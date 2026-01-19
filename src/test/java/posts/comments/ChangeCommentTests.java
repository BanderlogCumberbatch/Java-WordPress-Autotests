package posts.comments;

import org.helpers.BaseRequests;
import org.helpers.DBHelper;
import org.pojo.Comment;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Iterator;

public final class ChangeCommentTests extends CommentsBaseTest {

    /**
     * Тест получения изменённого комментария через запрос к БД.
     */
    @Test(description = "Get in db changed with REST API comment", priority = 1)
    public void testChangeComment() {
        int postId = BaseRequests.createPost(postsId, postPojo);
        commentPojo.setPostId(postId);
        int commentId = BaseRequests.createComment(commentsId, commentPojo);

        commentPojo.setAuthorEmail("unowen@unknown.com");
        BaseRequests.patchComment(commentId, commentPojo);

        dbCommRow = DBHelper.selectCommentById(commentId);
        Iterator<String> col = dbCommRow.iterator();
        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(Integer.parseInt(col.next()), commentPojo.getPostId(), "ID поста не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), commentPojo.getAuthorName(), "Имя автора комментария не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), commentPojo.getAuthorEmail(), "Email автора комментария не совпадает с данными в БД");
        softAssertion.assertEquals(col.next(), commentPojo.getContent(),"Содержание комментария не совпадает с данными в БД");
        softAssertion.assertAll();
    }

    /**
     * Тест получения изменённого комментария через GET запрос.
     */
    @Test(description = "REST API GET changed comment test", priority = 2)
    public void testGetChangedComment() {
        int postId = BaseRequests.createPost(postsId, postPojo);
        commentPojo.setPostId(postId);
        int commentId = BaseRequests.createComment(commentsId, commentPojo);

        commentPojo.setAuthorEmail("unowen@unknown.com");
        BaseRequests.patchComment(commentId, commentPojo);

        Comment comment = BaseRequests.getCommentById(commentId, 200);
        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(comment.getPostId(), postId, "ID поста не совпадает");
        softAssertion.assertEquals(comment.getAuthorName(), commentPojo.getAuthorName(), "Имя автора комментария не совпадает");
        String contentExpected = "<p>" + commentPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(comment.getContent(), contentExpected,"Содержание комментария не совпадает");
        softAssertion.assertAll();
    }

}
