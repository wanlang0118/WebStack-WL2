package webstack.module.siteComment.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("site_comment")
public class SiteComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer siteId;

    private Integer userId;

    private String guestName;

    private String guestEmail;

    private String guestUrl;

    private String content;

    private Long parentId;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
