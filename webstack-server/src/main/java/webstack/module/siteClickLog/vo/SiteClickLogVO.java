package webstack.module.siteClickLog.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteClickLogVO {

    private Long id;

    private Integer siteId;

    /** 关联查询 site.title，方便前端直接展示 */
    private String siteTitle;

    private Integer userId;

    private String ip;

    private LocalDateTime createTime;
}
