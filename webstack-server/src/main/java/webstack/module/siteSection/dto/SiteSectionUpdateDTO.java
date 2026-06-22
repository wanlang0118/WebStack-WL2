package webstack.module.siteSection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteSectionUpdateDTO {

    @NotNull(message = "分段 ID 不能为空")
    private Integer id;

    @NotBlank(message = "分段标题不能为空")
    private String title;

    private Integer sort;
}
