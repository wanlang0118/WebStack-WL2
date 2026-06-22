package webstack.module.siteImport.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.module.siteImport.dto.AiParseDTO;
import webstack.module.siteImport.dto.SiteImportItemDTO;
import webstack.module.siteImport.service.SiteImportService;
import webstack.module.siteImport.vo.SiteImportResultVO;

import java.util.List;

@RestController
@RequestMapping("/api/site/import")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class SiteImportController {

    private final SiteImportService siteImportService;

    @PostMapping
    public Result<SiteImportResultVO> importSites(@RequestBody List<SiteImportItemDTO> items) {
        return Result.success(siteImportService.importSites(items));
    }

    @PostMapping("/ai-parse")
    public Result<List<SiteImportItemDTO>> aiParse(@RequestBody @Valid AiParseDTO dto) {
        return Result.success(siteImportService.aiParse(dto));
    }
}
