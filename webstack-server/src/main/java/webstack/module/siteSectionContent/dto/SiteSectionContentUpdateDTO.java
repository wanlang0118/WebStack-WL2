package webstack.module.siteSectionContent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteSectionContentUpdateDTO {

    @NotNull(message = "内容 ID 不能为空")
    private Integer id;

    @NotBlank(message = "内容类型不能为空")
    private String contentType;

    private String textContent;

    private String jsonData;

    private Integer sort;
}
