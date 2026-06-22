package webstack.common.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("app.storage.local")
public class StorageProperties {
    private String uploadDir;
    private List<String> allowedExtensions;
    private long maxFileSize = 5 * 1024 * 1024;
}
