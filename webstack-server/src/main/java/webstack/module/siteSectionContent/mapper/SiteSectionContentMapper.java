package webstack.module.siteSectionContent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import webstack.module.siteSectionContent.entity.SiteSectionContent;

import java.util.List;

public interface SiteSectionContentMapper extends BaseMapper<SiteSectionContent> {

    List<SiteSectionContent> selectBySectionId(@Param("sectionId") Integer sectionId);
}
