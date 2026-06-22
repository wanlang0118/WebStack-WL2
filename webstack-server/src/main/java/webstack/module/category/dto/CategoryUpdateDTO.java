package webstack.module.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateDTO {

    @NotNull(message = "分类 ID 不能为空")
    private Integer id;

    private Integer parentId;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过 50")
    private String title;

    private String enTitle;

    private String icon;

    private Integer sort;

    private Integer visible;
}
