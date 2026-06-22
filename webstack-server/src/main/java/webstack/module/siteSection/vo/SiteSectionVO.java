package webstack.module.siteSection.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteSectionVO {

    private Integer id;

    private Integer siteId;

    private String title;

    private Integer sort;

    private LocalDateTime createTime;
}
