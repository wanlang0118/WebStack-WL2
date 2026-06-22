package webstack.module.sysNotice.dto;

import lombok.Data;

@Data
public class SysNoticeQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private Integer receiverId;

    private Integer noticeType;

    private Integer isRead;

    private String keyword;
}
