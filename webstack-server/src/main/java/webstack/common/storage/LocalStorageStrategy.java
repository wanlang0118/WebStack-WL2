package webstack.common.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import webstack.common.exception.BizException;
import webstack.common.result.ResultCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class LocalStorageStrategy implements StorageStrategy {

    private final StorageProperties properties;

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "上传文件不能为空");
        }

        if (file.getSize() > properties.getMaxFileSize()) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(),
                    "文件大小不能超过 " + (properties.getMaxFileSize() / 1024 / 1024) + "MB");
        }

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String ext = getExtension(originalFilename);
        if (ext == null || !properties.getAllowedExtensions().contains(ext.toLowerCase())) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(),
                    "仅支持 " + String.join(",", properties.getAllowedExtensions()) + " 格式");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "仅支持上传图片文件");
        }

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + ext.toLowerCase();
        String relativePath = dateDir + "/" + newFilename;

        Path targetDir = Paths.get(properties.getUploadDir(), dateDir);
        Path targetFile = targetDir.resolve(newFilename);

        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            file.transferTo(targetFile);
            log.info("文件上传成功: {}", targetFile.toAbsolutePath());
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BizException(ResultCode.ERROR.getCode(), "文件保存失败，请稍后重试");
        }

        return relativePath;
    }

    private String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(dotIndex + 1);
    }
}
