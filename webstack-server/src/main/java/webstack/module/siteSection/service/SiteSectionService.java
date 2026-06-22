package webstack.module.siteSection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import webstack.module.siteSection.dto.SiteSectionSaveDTO;
import webstack.module.siteSection.dto.SiteSectionUpdateDTO;
import webstack.module.siteSection.entity.SiteSection;
import webstack.module.siteSection.vo.SiteSectionVO;

import java.util.List;

public interface SiteSectionService extends IService<SiteSection> {

    List<SiteSectionVO> listBySiteId(Integer siteId);

    SiteSectionVO getDetail(Integer id);

    SiteSectionVO saveSection(SiteSectionSaveDTO dto);

    void updateSection(SiteSectionUpdateDTO dto);
}
