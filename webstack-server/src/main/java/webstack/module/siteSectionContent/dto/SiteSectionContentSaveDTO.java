package webstack.module.siteSectionContent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteSectionContentSaveDTO {

    @NotNull(message = "分段 ID 不能为空")
    private Integer sectionId;

    @NotBlank(message = "内容类型不能为空")
    private String contentType;

    private String textContent;

    private String jsonData;

    private Integer sort;
}
