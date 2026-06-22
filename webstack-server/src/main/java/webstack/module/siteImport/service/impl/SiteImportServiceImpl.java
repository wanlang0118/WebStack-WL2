package webstack.module.siteImport.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import webstack.common.constant.ContentType;
import webstack.common.exception.BizException;
import webstack.module.category.service.CategoryService;
import webstack.module.site.entity.Site;
import webstack.module.site.service.SiteService;
import webstack.module.site.service.SiteTagRelationService;
import webstack.module.siteImport.dto.AiParseDTO;
import webstack.module.siteImport.dto.SiteImportItemDTO;
import webstack.module.siteImport.dto.SiteImportItemDTO.ImportListItemDTO;
import webstack.module.siteImport.dto.SiteImportItemDTO.ImportSectionDTO;
import webstack.module.siteImport.service.SiteImportService;
import webstack.module.siteImport.vo.SiteImportResultVO;
import webstack.module.siteSection.entity.SiteSection;
import webstack.module.siteSection.service.SiteSectionService;
import webstack.module.siteSectionContent.entity.SiteSectionContent;
import webstack.module.siteSectionContent.service.SiteSectionContentService;
import webstack.module.tag.service.TagService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SiteImportServiceImpl implements SiteImportService {

    @Autowired private CategoryService categoryService;
    @Autowired private TagService tagService;
    @Autowired private SiteService siteService;
    @Autowired private SiteTagRelationService siteTagRelationService;
    @Autowired private SiteSectionService siteSectionService;
    @Autowired private SiteSectionContentService siteSectionContentService;

    @Lazy @Autowired private SiteImportServiceImpl self;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("${deepseek.base-url:https://api.deepseek.com}")
    private String deepseekBaseUrl;

    @Override
    public SiteImportResultVO importSites(List<SiteImportItemDTO> items) {
        SiteImportResultVO result = new SiteImportResultVO();
        result.setTotal(items.size());

        for (int i = 0; i < items.size(); i++) {
            SiteImportItemDTO item = items.get(i);
            String itemName = resolveTitle(item);
            try {
                self.importSingleSite(item);
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                log.warn("导入第 {} 条网站 [{}] 失败: {}", i + 1, itemName, e.getMessage());
                result.setFailCount(result.getFailCount() + 1);
                result.getErrors().add(new SiteImportResultVO.ImportError(i, itemName, e.getMessage()));
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void importSingleSite(SiteImportItemDTO item) {
        String title = resolveTitle(item);
        String url = resolveUrl(item);
        if (!StringUtils.hasText(title)) {
            throw new BizException("网站名称不能为空");
        }
        if (!StringUtils.hasText(url)) {
            throw new BizException("网站地址不能为空");
        }

        Integer categoryId = null;
        if (StringUtils.hasText(item.getCategory())) {
            categoryId = categoryService.getOrCreateByTitle(item.getCategory());
        }
        if (categoryId == null) {
            throw new BizException("分类不能为空");
        }

        List<Integer> tagIds = new ArrayList<>();
        if (item.getTags() != null) {
            for (String tagName : item.getTags()) {
                if (StringUtils.hasText(tagName)) {
                    tagIds.add(tagService.getOrCreateByTagName(tagName.trim()));
                }
            }
        }

        Site site = new Site();
        site.setCategoryId(categoryId);
        site.setTitle(title);
        site.setThumb(item.getLogo());
        site.setDescription(item.getShortDescription());
        site.setUrl(url);
        site.setVisible(1);
        site.setSort(0);
        site.setIsRecommended(0);
        siteService.save(site);

        if (!tagIds.isEmpty()) {
            siteTagRelationService.saveRelations(site.getId(), tagIds);
        }

        if (item.getSections() != null && !item.getSections().isEmpty()) {
            saveSections(site.getId(), item.getSections());
        }
    }

    private void saveSections(Integer siteId, List<ImportSectionDTO> sections) {
        for (int i = 0; i < sections.size(); i++) {
            ImportSectionDTO sec = sections.get(i);
            if (!StringUtils.hasText(sec.getTitle())) {
                continue;
            }

            SiteSection section = new SiteSection();
            section.setSiteId(siteId);
            section.setTitle(sec.getTitle());
            section.setSort(i);
            siteSectionService.save(section);

            int contentSort = 0;

            if (sec.getParagraphs() != null) {
                for (String para : sec.getParagraphs()) {
                    if (!StringUtils.hasText(para)) continue;
                    SiteSectionContent content = new SiteSectionContent();
                    content.setSectionId(section.getId());
                    content.setContentType(ContentType.PARAGRAPH);
                    content.setTextContent(para);
                    content.setSort(contentSort++);
                    siteSectionContentService.save(content);
                }
            }

            if (sec.getImages() != null) {
                for (String imgUrl : sec.getImages()) {
                    if (!StringUtils.hasText(imgUrl)) continue;
                    SiteSectionContent content = new SiteSectionContent();
                    content.setSectionId(section.getId());
                    content.setContentType(ContentType.IMAGE);
                    try {
                        content.setJsonData(objectMapper.writeValueAsString(Map.of("url", imgUrl, "alt", "")));
                    } catch (JsonProcessingException e) {
                        content.setJsonData("{\"url\":\"" + imgUrl + "\",\"alt\":\"\"}");
                    }
                    content.setSort(contentSort++);
                    siteSectionContentService.save(content);
                }
            }

            if (sec.getListItems() != null && !sec.getListItems().isEmpty()) {
                List<String> items = sec.getListItems().stream()
                        .map(ImportListItemDTO::getText)
                        .filter(StringUtils::hasText)
                        .toList();
                if (!items.isEmpty()) {
                    SiteSectionContent content = new SiteSectionContent();
                    content.setSectionId(section.getId());
                    content.setContentType(ContentType.LIST_ITEM);
                    try {
                        content.setJsonData(objectMapper.writeValueAsString(Map.of("items", items)));
                    } catch (JsonProcessingException e) {
                        log.warn("序列化 list_item 失败", e);
                    }
                    content.setSort(contentSort++);
                    siteSectionContentService.save(content);
                }
            }

            if (sec.getTable() != null && sec.getTable().size() >= 2) {
                List<String> headers = sec.getTable().get(0);
                List<List<String>> rows = sec.getTable().subList(1, sec.getTable().size());
                SiteSectionContent content = new SiteSectionContent();
                content.setSectionId(section.getId());
                content.setContentType(ContentType.TABLE);
                try {
                    content.setJsonData(objectMapper.writeValueAsString(Map.of("headers", headers, "rows", rows)));
                } catch (JsonProcessingException e) {
                    log.warn("序列化 table 失败", e);
                }
                content.setSort(contentSort++);
                siteSectionContentService.save(content);
            }
        }
    }

    @Override
    public List<SiteImportItemDTO> aiParse(AiParseDTO dto) {
        String model = StringUtils.hasText(dto.getModel()) ? dto.getModel() : "deepseek-chat";

        String systemPrompt = """
                你是一个数据格式转换助手。用户会给你一段描述网站信息的文本（可能是JSON、CSV、Markdown、纯文本或其他格式）。
                你需要将其转换为以下标准JSON数组格式，仅输出JSON数组，不要输出其他内容：
                [
                  {
                    "name": "网站名称",
                    "logo": "Logo图片URL",
                    "shortDescription": "一句话简介",
                    "officialUrl": "官方网址",
                    "category": "所属分类名称",
                    "tags": ["标签1", "标签2"],
                    "sections": [
                      {
                        "title": "小节标题",
                        "paragraphs": ["段落文本1"],
                        "images": ["图片URL1"],
                        "listItems": [{"text": "列表项文本"}],
                        "table": [["表头1","表头2"],["行1列1","行1列2"]]
                      }
                    ]
                  }
                ]
                规则：
                1. 如果原始数据缺少某些字段，对应填空字符串或空数组
                2. sections 中如果没有内容，填空数组
                3. table 如果没有数据，填空数组
                4. 仅输出合法JSON数组，不要包含 markdown 代码块标记或解释文字
                """;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", dto.getRawText())
                ),
                "temperature", 0.1
        );

        RestClient restClient = RestClient.builder()
                .baseUrl(deepseekBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + dto.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String responseBody;
        try {
            responseBody = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败", e);
            throw new BizException("调用 AI 接口失败: " + e.getMessage());
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").path(0).path("message").path("content").asText();
            content = content.strip();
            if (content.startsWith("```")) {
                content = content.replaceFirst("^```[a-zA-Z]*\\n?", "");
                content = content.replaceFirst("\\n?```$", "");
                content = content.strip();
            }
            return objectMapper.readValue(content, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("解析 AI 响应失败", e);
            throw new BizException("AI 返回的数据格式异常，无法解析: " + e.getMessage());
        }
    }

    private String resolveTitle(SiteImportItemDTO item) {
        return StringUtils.hasText(item.getName()) ? item.getName() : null;
    }

    private String resolveUrl(SiteImportItemDTO item) {
        return StringUtils.hasText(item.getOfficialUrl()) ? item.getOfficialUrl() : null;
    }
}
