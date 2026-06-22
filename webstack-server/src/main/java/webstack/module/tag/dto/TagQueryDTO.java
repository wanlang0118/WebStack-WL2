package webstack.module.tag.dto;

import lombok.Data;

@Data
public class TagQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private String tagName;
}
