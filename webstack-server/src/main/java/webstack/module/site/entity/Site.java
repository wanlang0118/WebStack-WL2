package webstack.module.site.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("site")
public class Site {

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
