package webstack.module.upload.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.common.storage.StorageStrategy;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class FileUploadController {

    private final StorageStrategy storageStrategy;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String fileKey = storageStrategy.upload(file);
        return Result.success(fileKey);
    }
}
