package webstack.module.siteLike.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import webstack.module.siteLike.dto.SiteLikeQueryDTO;
import webstack.module.siteLike.entity.SiteLike;
import webstack.module.siteLike.vo.SiteLikeVO;

import java.util.List;

public interface SiteLikeService extends IService<SiteLike> {

    /**
     * 分页查询点赞记录（管理端使用）
     */
    IPage<SiteLikeVO> pageQuery(SiteLikeQueryDTO dto);

    /**
     * 点赞：插入记录 + 原子递增 site.like_count（幂等，重复点赞不报错）
     */
    void like(Integer siteId, HttpServletRequest request);

    /**
     * 取消点赞：删除记录 + 原子递减 site.like_count
     */
    void unlike(Integer siteId, HttpServletRequest request);

    /**
     * 查询当前登录用户是否已点赞某网站
     */
    boolean isLiked(Integer siteId, HttpServletRequest request);

    /**
     * 管理端删除单条点赞记录（同步递减 like_count）
     */
    void removeRecord(Long id);

    /**
     * 管理端批量删除点赞记录（同步递减 like_count）
     */
    void removeBatch(List<Long> ids);
}
