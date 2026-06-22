package webstack.module.siteClickLog.dto;

import lombok.Data;

@Data
public class SiteClickLogQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    /** 按网站 ID 过滤（可选） */
    private Integer siteId;
}
