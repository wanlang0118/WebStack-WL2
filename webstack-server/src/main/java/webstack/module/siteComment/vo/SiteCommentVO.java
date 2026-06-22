package webstack.module.siteComment.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SiteCommentVO {

    private Long id;

    private Integer siteId;

    private String siteTitle;

    private Integer userId;

    private String username;

    private String guestName;

    private String guestEmail;

    private String guestUrl;

    private String content;

    private Long parentId;

    private Integer status;

    private LocalDateTime createTime;

    private List<SiteCommentVO> children;
}
