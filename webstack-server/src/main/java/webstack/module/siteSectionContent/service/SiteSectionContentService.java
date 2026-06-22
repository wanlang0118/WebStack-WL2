package webstack.module.siteSectionContent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import webstack.module.siteSectionContent.dto.SiteSectionContentSaveDTO;
import webstack.module.siteSectionContent.dto.SiteSectionContentUpdateDTO;
import webstack.module.siteSectionContent.entity.SiteSectionContent;
import webstack.module.siteSectionContent.vo.SiteSectionContentVO;

import java.util.List;

public interface SiteSectionContentService extends IService<SiteSectionContent> {

    List<SiteSectionContentVO> listBySectionId(Integer sectionId);

    SiteSectionContentVO getDetail(Integer id);

    SiteSectionContentVO saveContent(SiteSectionContentSaveDTO dto);

    void updateContent(SiteSectionContentUpdateDTO dto);

    Object parseJsonData(String contentType, String jsonData);
}
