package webstack.module.siteImport.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiParseDTO {

    @NotBlank(message = "API Key 不能为空")
    private String apiKey;

    /** 模型名称，默认 deepseek-chat */
    private String model = "deepseek-chat";

    @NotBlank(message = "原始文本不能为空")
    private String rawText;
}
