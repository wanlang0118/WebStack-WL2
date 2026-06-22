package webstack.module.category.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryTreeVO {

    private Integer id;
    private Integer parentId;
    private String title;
    private String enTitle;
    private String icon;
    private Integer sort;
    private Integer visible;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<CategoryTreeVO> children = new ArrayList<>();
}
