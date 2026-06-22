package webstack.module.tag.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagVO {

    private Integer id;

    private String tagName;

    private LocalDateTime createTime;
}
