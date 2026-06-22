package webstack.module.siteComment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import webstack.module.siteComment.dto.SiteCommentQueryDTO;
import webstack.module.siteComment.entity.SiteComment;
import webstack.module.siteComment.vo.SiteCommentVO;

import java.util.List;

public interface SiteCommentMapper extends BaseMapper<SiteComment> {

    IPage<SiteCommentVO> selectCommentPage(IPage<SiteComment> page, @Param("dto") SiteCommentQueryDTO dto);

    List<SiteCommentVO> selectChildrenByParentIds(@Param("parentIds") List<Long> parentIds);
}
