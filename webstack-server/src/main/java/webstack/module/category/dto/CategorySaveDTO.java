package webstack.module.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategorySaveDTO {

    /** 父级分类 ID，顶级分类为 0 或 null */
    private Integer parentId;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过 50")
    private String title;

    private String enTitle;

    private String icon;

    private Integer sort;

    private Integer visible;
}
