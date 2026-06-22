package webstack.module.site.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SiteUpdateDTO {

    @NotNull(message = "网站 ID 不能为空")
    private Integer id;

    @NotNull(message = "所属分类不能为空")
    private Integer categoryId;

    @NotBlank(message = "网站名称不能为空")
    @Size(max = 100, message = "网站名称长度不能超过 100")
    private String title;

    private String thumb;

    private String description;

    @NotBlank(message = "网站地址不能为空")
    private String url;

    private Integer isRecommended;

    private Integer sort;

    private Integer visible;

    private List<Integer> tagIds;
}
