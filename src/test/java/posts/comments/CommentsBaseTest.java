package posts.comments;

import org.pojo.Comment;
import posts.BaseTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс для тестов комментариев.
 */
public abstract class CommentsBaseTest extends BaseTest {

    /**
     * Список ID постов.
     */
    protected final List<Integer> commentsId = new ArrayList<>();

    /**
     * Список с данными комментария из базы данных.
     */
     protected List<String> dbCommRow = new ArrayList<>();

    /**
     * Создание pojo запроса для взаимодействия с комментариями.
     */
    protected final Comment commentPojo = Comment.builder()
            .authorName("Anon")
            .authorEmail("unowen@unknown.com")
            .content("sample content")
            .build();

}
