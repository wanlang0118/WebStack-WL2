package webstack.module.siteSection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import webstack.module.siteSection.entity.SiteSection;

import java.util.List;

public interface SiteSectionMapper extends BaseMapper<SiteSection> {

    List<SiteSection> selectBySiteId(@Param("siteId") Integer siteId);
}
