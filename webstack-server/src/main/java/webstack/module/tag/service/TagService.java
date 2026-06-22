package webstack.module.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import webstack.module.tag.dto.TagQueryDTO;
import webstack.module.tag.dto.TagSaveDTO;
import webstack.module.tag.dto.TagUpdateDTO;
import webstack.module.tag.entity.Tag;
import webstack.module.tag.vo.TagVO;

import java.util.List;

public interface TagService extends IService<Tag> {

    IPage<TagVO> pageQuery(TagQueryDTO dto);

    List<TagVO> listAll();

    TagVO getDetail(Integer id);

    TagVO saveTag(TagSaveDTO dto);

    void updateTag(TagUpdateDTO dto);

    /**
     * 删除单个标签：同时清理标签与网站的关联关系（单一事务）
     */
    void removeTagWithRelations(Integer id);

    /**
     * 批量删除标签：同时清理标签与网站的关联关系（单一事务）
     */
    void removeTagsWithRelations(List<Integer> ids);

    /**
     * 按标签名称查找，不存在则自动创建并返回 ID
     */
    Integer getOrCreateByTagName(String tagName);
}
