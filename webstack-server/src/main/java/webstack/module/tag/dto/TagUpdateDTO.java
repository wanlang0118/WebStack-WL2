package webstack.module.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagUpdateDTO {

    @NotNull(message = "标签 ID 不能为空")
    private Integer id;

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过 50")
    private String tagName;
}
