package webstack.module.site.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("site_tag_relation")
public class SiteTagRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer siteId;

    private Integer tagId;
}
