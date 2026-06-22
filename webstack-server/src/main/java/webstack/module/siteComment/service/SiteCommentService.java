package webstack.module.siteComment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import webstack.module.siteComment.dto.SiteCommentCreateDTO;
import webstack.module.siteComment.dto.SiteCommentQueryDTO;
import webstack.module.siteComment.dto.SiteCommentReplyDTO;
import webstack.module.siteComment.entity.SiteComment;
import webstack.module.siteComment.vo.SiteCommentVO;

import java.util.List;

public interface SiteCommentService extends IService<SiteComment> {

    IPage<SiteCommentVO> pageQuery(SiteCommentQueryDTO dto);

    void createComment(SiteCommentCreateDTO dto, Integer userId);

    void updateStatus(Long id, Integer status);

    void reply(SiteCommentReplyDTO dto);

    void updateContent(Long id, String content);

    void removeComment(Long id);

    void removeBatch(List<Long> ids);
}
