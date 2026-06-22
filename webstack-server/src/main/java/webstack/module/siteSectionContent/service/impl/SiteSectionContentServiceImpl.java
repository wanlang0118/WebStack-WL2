package webstack.module.siteSectionContent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import webstack.common.constant.ContentType;
import webstack.module.siteSectionContent.dto.SiteSectionContentSaveDTO;
import webstack.module.siteSectionContent.dto.SiteSectionContentUpdateDTO;
import webstack.module.siteSectionContent.entity.SiteSectionContent;
import webstack.module.siteSectionContent.mapper.SiteSectionContentMapper;
import webstack.module.siteSectionContent.service.SiteSectionContentService;
import webstack.module.siteSectionContent.vo.SiteSectionContentVO;
import webstack.module.siteSectionContent.vo.contentdata.ImageData;
import webstack.module.siteSectionContent.vo.contentdata.ListItemData;
import webstack.module.siteSectionContent.vo.contentdata.ParagraphData;
import webstack.module.siteSectionContent.vo.contentdata.TableData;

import java.util.List;

@Slf4j
@Service
public class SiteSectionContentServiceImpl extends ServiceImpl<SiteSectionContentMapper, SiteSectionContent> implements SiteSectionContentService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<SiteSectionContentVO> listBySectionId(Integer sectionId) {
        LambdaQueryWrapper<SiteSectionContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteSectionContent::getSectionId, sectionId)
               .orderByAsc(SiteSectionContent::getSort)
               .orderByAsc(SiteSectionContent::getId);
        return baseMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    public SiteSectionContentVO getDetail(Integer id) {
        SiteSectionContent content = getById(id);
        return content == null ? null : toVO(content);
    }

    @Override
    public SiteSectionContentVO saveContent(SiteSectionContentSaveDTO dto) {
        SiteSectionContent content = new SiteSectionContent();
        BeanUtils.copyProperties(dto, content);
        save(content);
        return toVO(content);
    }

    @Override
    public void updateContent(SiteSectionContentUpdateDTO dto) {
        SiteSectionContent content = new SiteSectionContent();
        BeanUtils.copyProperties(dto, content);
        updateById(content);
    }

    @Override
    public Object parseJsonData(String contentType, String jsonData) {
        if (jsonData == null || jsonData.isBlank()) {
            return null;
        }
        try {
            return switch (contentType) {
                case ContentType.IMAGE -> objectMapper.readValue(jsonData, ImageData.class);
                case ContentType.LIST_ITEM -> objectMapper.readValue(jsonData, ListItemData.class);
                case ContentType.TABLE -> objectMapper.readValue(jsonData, TableData.class);
                case ContentType.PARAGRAPH -> objectMapper.readValue(jsonData, ParagraphData.class);
                default -> jsonData;
            };
        } catch (JsonProcessingException e) {
            log.warn("解析分段内容 JSON 失败, contentType={}, 错误={}", contentType, e.getOriginalMessage());
            return jsonData;
        }
    }

    private SiteSectionContentVO toVO(SiteSectionContent content) {
        SiteSectionContentVO vo = new SiteSectionContentVO();
        BeanUtils.copyProperties(content, vo);
        vo.setParsedData(parseJsonData(content.getContentType(), content.getJsonData()));
        return vo;
    }
}
