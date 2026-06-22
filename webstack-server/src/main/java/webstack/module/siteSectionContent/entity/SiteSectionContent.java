package webstack.module.siteSectionContent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("site_section_content")
public class SiteSectionContent {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer sectionId;

    private String contentType;

    private String textContent;

    private String jsonData;

    private Integer sort;
}
