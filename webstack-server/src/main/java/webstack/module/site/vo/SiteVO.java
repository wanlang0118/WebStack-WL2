package webstack.module.site.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SiteVO {

    private Integer id;

    private Integer categoryId;

    private String title;

    private String thumb;

    private String description;

    private String url;

    private Integer likeCount;

    private Integer commentCount;

    private Integer clickCount;

    private Integer isRecommended;

    private Integer sort;

    private Integer visible;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<Integer> tagIds;

    private List<String> tagNameList;
}
