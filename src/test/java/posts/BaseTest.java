package posts;

import org.helpers.BaseRequests;
import org.helpers.DBHelper;
import org.helpers.PropertyProvider;
import org.pojo.Post;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс для тестов.
 */
public abstract class BaseTest {

    /**
     * Список ID постов.
     */
    protected final List<Integer> postsId = new ArrayList<>();

    /**
     * Список с данными поста из базы данных.
     */
    protected List<String> dbPostRow = new ArrayList<>();

    /**
     * Создание pojo запроса для взаимодействия с постами.
     */
    protected final Post postPojo = Post.builder()
            .status("publish")
            .title("New post")
            .content("sample text")
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
        DBHelper.connect(
                envLocalProvider.getProperty("db.url"),
                secretsProvider.getProperty("db.user"),
                secretsProvider.getProperty("db.password"));
    }

    @AfterClass()
    public void clearPosts() {
        DBHelper.disconnect();
        if (!postsId.isEmpty()) {
            BaseRequests.deletePostsById(postsId);
        }
    }
}
