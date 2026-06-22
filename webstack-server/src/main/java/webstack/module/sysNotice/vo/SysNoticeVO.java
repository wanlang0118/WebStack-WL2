package webstack.module.sysNotice.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysNoticeVO {

    private Long id;

    private Integer senderId;

    private String senderName;

    private Integer receiverId;

    private String receiverName;

    private Integer noticeType;

    private Integer sourceSiteId;

    private String siteTitle;

    private Long sourceCommentId;

    private String title;

    private String summary;

    private String content;

    private Integer isRead;

    private LocalDateTime createTime;

    private LocalDateTime readTime;
}
