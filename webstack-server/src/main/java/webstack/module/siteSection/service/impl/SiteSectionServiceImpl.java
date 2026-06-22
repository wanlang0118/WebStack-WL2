package webstack.module.siteSection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import webstack.module.siteSection.dto.SiteSectionSaveDTO;
import webstack.module.siteSection.dto.SiteSectionUpdateDTO;
import webstack.module.siteSection.entity.SiteSection;
import webstack.module.siteSection.mapper.SiteSectionMapper;
import webstack.module.siteSection.service.SiteSectionService;
import webstack.module.siteSection.vo.SiteSectionVO;

import java.util.List;

@Service
public class SiteSectionServiceImpl extends ServiceImpl<SiteSectionMapper, SiteSection> implements SiteSectionService {

    @Override
    public List<SiteSectionVO> listBySiteId(Integer siteId) {
        LambdaQueryWrapper<SiteSection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteSection::getSiteId, siteId)
               .orderByAsc(SiteSection::getSort)
               .orderByAsc(SiteSection::getId);
        return baseMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    public SiteSectionVO getDetail(Integer id) {
        SiteSection section = getById(id);
        return section == null ? null : toVO(section);
    }

    @Override
    public SiteSectionVO saveSection(SiteSectionSaveDTO dto) {
        SiteSection section = new SiteSection();
        BeanUtils.copyProperties(dto, section);
        save(section);
        return toVO(section);
    }

    @Override
    public void updateSection(SiteSectionUpdateDTO dto) {
        SiteSection section = new SiteSection();
        BeanUtils.copyProperties(dto, section);
        updateById(section);
    }

    private SiteSectionVO toVO(SiteSection section) {
        SiteSectionVO vo = new SiteSectionVO();
        BeanUtils.copyProperties(section, vo);
        return vo;
    }
}
