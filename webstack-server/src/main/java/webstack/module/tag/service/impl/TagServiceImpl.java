package webstack.module.tag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webstack.module.site.entity.SiteTagRelation;
import webstack.module.site.service.SiteTagRelationService;
import webstack.module.tag.dto.TagQueryDTO;
import webstack.module.tag.dto.TagSaveDTO;
import webstack.module.tag.dto.TagUpdateDTO;
import webstack.module.tag.entity.Tag;
import webstack.module.tag.mapper.TagMapper;
import webstack.module.tag.service.TagService;
import webstack.module.tag.vo.TagVO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final SiteTagRelationService siteTagRelationService;

    @Override
    public IPage<TagVO> pageQuery(TagQueryDTO dto) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getTagName())) {
            wrapper.like(Tag::getTagName, dto.getTagName());
        }
        wrapper.orderByDesc(Tag::getCreateTime);
        return page(new Page<>(dto.getPage(), dto.getSize()), wrapper).convert(this::toVO);
    }

    @Override
    public List<TagVO> listAll() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Tag::getCreateTime);
        return list(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    public TagVO getDetail(Integer id) {
        Tag tag = getById(id);
        return tag == null ? null : toVO(tag);
    }

    @Override
    public TagVO saveTag(TagSaveDTO dto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(dto, tag);
        save(tag);
        return toVO(tag);
    }

    @Override
    public void updateTag(TagUpdateDTO dto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(dto, tag);
        updateById(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTagWithRelations(Integer id) {
        removeTagsWithRelations(List.of(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTagsWithRelations(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<SiteTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SiteTagRelation::getTagId, ids);
        siteTagRelationService.remove(wrapper);
        removeByIds(ids);
    }

    @Override
    public Integer getOrCreateByTagName(String tagName) {
        Tag existing = lambdaQuery()
                .eq(Tag::getTagName, tagName)
                .last("LIMIT 1")
                .one();
        if (existing != null) {
            return existing.getId();
        }
        Tag tag = new Tag();
        tag.setTagName(tagName);
        save(tag);
        return tag.getId();
    }

    private TagVO toVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
}
