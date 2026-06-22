package webstack.module.site.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webstack.common.exception.BizException;
import webstack.common.result.ResultCode;
import webstack.module.site.dto.SiteQueryDTO;
import webstack.module.site.dto.SiteSaveDTO;
import webstack.module.site.dto.SiteUpdateDTO;
import webstack.module.site.entity.Site;
import webstack.module.site.entity.SiteTagRelation;
import webstack.module.site.mapper.SiteMapper;
import webstack.module.site.service.SiteService;
import webstack.module.site.service.SiteTagRelationService;
import webstack.module.site.vo.SiteVO;
import webstack.module.tag.entity.Tag;
import webstack.module.tag.mapper.TagMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl extends ServiceImpl<SiteMapper, Site> implements SiteService {

    private final SiteTagRelationService siteTagRelationService;

    private final TagMapper tagMapper;

    @Override
    public IPage<SiteVO> pageQuery(SiteQueryDTO dto) {
        IPage<Site> sitePage = baseMapper.selectSitePage(new Page<>(dto.getPage(), dto.getSize()), dto);
        List<SiteVO> voList = toVOList(sitePage.getRecords());

        IPage<SiteVO> voPage = new Page<>(sitePage.getCurrent(), sitePage.getSize(), sitePage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public SiteVO getSiteDetail(Integer id) {
        Site site = getById(id);
        if (site == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        return toVO(site);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSite(SiteSaveDTO dto) {
        Site site = new Site();
        BeanUtils.copyProperties(dto, site);
        save(site);
        siteTagRelationService.saveRelations(site.getId(), dto.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSite(SiteUpdateDTO dto) {
        Site site = new Site();
        BeanUtils.copyProperties(dto, site);
        updateById(site);
        siteTagRelationService.saveRelations(site.getId(), dto.getTagIds());
    }

    @Override
    public List<String> listTitlesByCategoryIds(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyList();
        }
        return lambdaQuery()
                .in(Site::getCategoryId, categoryIds)
                .list()
                .stream()
                .map(Site::getTitle)
                .toList();
    }

    private List<SiteVO> toVOList(List<Site> sites) {
        if (sites == null || sites.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> siteIds = sites.stream().map(Site::getId).toList();
        Map<Integer, List<SiteTagRelation>> relMap = siteTagRelationService.list(
                        new LambdaQueryWrapper<SiteTagRelation>()
                                .in(SiteTagRelation::getSiteId, siteIds))
                .stream()
                .collect(Collectors.groupingBy(SiteTagRelation::getSiteId));

        Set<Integer> allTagIds = relMap.values().stream()
                .flatMap(List::stream)
                .map(SiteTagRelation::getTagId)
                .collect(Collectors.toSet());

        Map<Integer, String> tagNameMap = allTagIds.isEmpty() ? Collections.emptyMap()
                : tagMapper.selectBatchIds(allTagIds).stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getTagName));

        return sites.stream().map(site -> {
            SiteVO vo = new SiteVO();
            BeanUtils.copyProperties(site, vo);
            List<SiteTagRelation> rels = relMap.getOrDefault(site.getId(), Collections.emptyList());
            List<Integer> tagIds = rels.stream().map(SiteTagRelation::getTagId).toList();
            List<String> tagNames = tagIds.stream()
                    .map(tagNameMap::get)
                    .filter(Objects::nonNull)
                    .toList();
            vo.setTagIds(tagIds);
            vo.setTagNameList(tagNames);
            return vo;
        }).toList();
    }

    private SiteVO toVO(Site site) {
        return toVOList(List.of(site)).get(0);
    }

    @Override
    public void incrementClickCount(Integer siteId) {
        baseMapper.incrementClickCount(siteId);
    }

    @Override
    public void incrementLikeCount(Integer siteId) {
        baseMapper.incrementLikeCount(siteId);
    }

    @Override
    public void decrementLikeCount(Integer siteId) {
        baseMapper.decrementLikeCount(siteId);
    }

    @Override
    public void decrementLikeCountBy(Integer siteId, long count) {
        baseMapper.decrementLikeCountBy(siteId, count);
    }

    @Override
    public void incrementCommentCount(Integer siteId) {
        baseMapper.incrementCommentCount(siteId);
    }
}
