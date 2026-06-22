package webstack.module.siteComment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteCommentCreateDTO {

    @NotNull(message = "网站 ID 不能为空")
    private Integer siteId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentId;

    private String guestName;

    private String guestEmail;

    private String guestUrl;
}
