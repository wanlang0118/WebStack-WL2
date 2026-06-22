package webstack.module.siteComment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteCommentReplyDTO {

    @NotNull(message = "网站 ID 不能为空")
    private Integer siteId;

    /** 回复者用户 ID（管理员），可为空表示系统/管理员 */
    private Integer userId;

    @NotNull(message = "父评论 ID 不能为空")
    private Long parentId;

    @NotBlank(message = "回复内容不能为空")
    private String content;
}
