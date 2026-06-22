package webstack.module.sysNotice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysNoticeSendDTO {

    @NotBlank(message = "通知标题不能为空")
    private String title;

    private String summary;

    @NotBlank(message = "通知内容不能为空")
    private String content;
}
