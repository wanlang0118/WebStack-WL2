package webstack.module.siteSection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteSectionSaveDTO {

    @NotNull(message = "网站 ID 不能为空")
    private Integer siteId;

    @NotBlank(message = "分段标题不能为空")
    private String title;

    private Integer sort;
}
