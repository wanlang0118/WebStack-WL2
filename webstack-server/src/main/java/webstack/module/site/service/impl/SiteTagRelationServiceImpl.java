package webstack.module.site.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webstack.module.site.entity.SiteTagRelation;
import webstack.module.site.mapper.SiteTagRelationMapper;
import webstack.module.site.service.SiteTagRelationService;

import java.util.List;

@Service
public class SiteTagRelationServiceImpl extends ServiceImpl<SiteTagRelationMapper, SiteTagRelation> implements SiteTagRelationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRelations(Integer siteId, List<Integer> tagIds) {
        removeBySiteId(siteId);
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        List<SiteTagRelation> list = tagIds.stream()
                .distinct()
                .map(tagId -> {
                    SiteTagRelation rel = new SiteTagRelation();
                    rel.setSiteId(siteId);
                    rel.setTagId(tagId);
                    return rel;
                }).toList();
        saveBatch(list);
    }

    @Override
    public void removeBySiteId(Integer siteId) {
        LambdaQueryWrapper<SiteTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteTagRelation::getSiteId, siteId);
        remove(wrapper);
    }
}
