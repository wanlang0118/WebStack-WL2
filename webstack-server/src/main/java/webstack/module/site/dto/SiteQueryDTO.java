package webstack.module.site.dto;

import lombok.Data;

@Data
public class SiteQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private String title;

    private Integer categoryId;

    private Integer visible;

    private Integer isRecommended;

    private String sortField;

    private String sortOrder;
}
