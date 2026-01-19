package org.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Comment {
    /**
     * ID поста.
     */
    @Builder.Default
    @SerializedName("post")
    private int postId = 0;

    /**
     * Статус записи.
     */
    @Builder.Default
    @SerializedName("author_name")
    private String authorName = "Unknown";

    /**
     * Название записи.
     */
    @Builder.Default
    @SerializedName("author_email")
    private String authorEmail = "unknown@unknown.com";

    /**
     * Содержимое записи.
     */
    @Builder.Default
    private String content = "sample text";
}
