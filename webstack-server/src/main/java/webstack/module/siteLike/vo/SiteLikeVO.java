package webstack.module.siteLike.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteLikeVO {

    private Long id;

    private Integer siteId;

    /** 关联查询 site.title，方便前端直接展示 */
    private String siteTitle;

    private Integer userId;

    /** 关联查询 sys_user.username，方便前端直接展示 */
    private String username;

    /** 客户端 IP */
    private String ip;

    private LocalDateTime createTime;
}
