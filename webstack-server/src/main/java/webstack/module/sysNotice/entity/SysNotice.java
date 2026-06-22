package webstack.module.sysNotice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_notice")
public class SysNotice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer senderId;

    private String senderName;

    private Integer receiverId;

    private Integer noticeType;

    private Integer sourceSiteId;

    private Long sourceCommentId;

    private String title;

    private String summary;

    private String content;

    private Integer isRead;

    private LocalDateTime createTime;

    private LocalDateTime readTime;
}
