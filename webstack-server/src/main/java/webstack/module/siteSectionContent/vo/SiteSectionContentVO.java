package webstack.module.siteSectionContent.vo;

import lombok.Data;

@Data
public class SiteSectionContentVO {

    private Integer id;

    private Integer sectionId;

    private String contentType;

    private String textContent;

    private String jsonData;

    /** 由 jsonData 按 contentType 解析后的结构化数据，方便前端直接使用 */
    private Object parsedData;

    private Integer sort;
}
