package webstack.module.siteComment.dto;

import lombok.Data;

@Data
public class SiteCommentQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    /** 按网站 ID 过滤 */
    private Integer siteId;

    /** 按审核状态过滤（0=待审核，1=正常显示） */
    private Integer status;

    /** 模糊搜索评论内容或游客昵称 */
    private String keyword;
}
