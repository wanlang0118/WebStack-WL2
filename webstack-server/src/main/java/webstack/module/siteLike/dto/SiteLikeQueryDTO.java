package webstack.module.siteLike.dto;

import lombok.Data;

@Data
public class SiteLikeQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    /** 按网站 ID 过滤（可选） */
    private Integer siteId;

    /** 按用户 ID 过滤（可选） */
    private Integer userId;
}
