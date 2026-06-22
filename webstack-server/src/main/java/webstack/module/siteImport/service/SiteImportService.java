package webstack.module.siteImport.service;

import webstack.module.siteImport.dto.AiParseDTO;
import webstack.module.siteImport.dto.SiteImportItemDTO;
import webstack.module.siteImport.vo.SiteImportResultVO;

import java.util.List;

public interface SiteImportService {

    SiteImportResultVO importSites(List<SiteImportItemDTO> items);

    List<SiteImportItemDTO> aiParse(AiParseDTO dto);
}
