package webstack.module.site.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import webstack.module.site.dto.SiteQueryDTO;
import webstack.module.site.dto.SiteSaveDTO;
import webstack.module.site.dto.SiteUpdateDTO;
import webstack.module.site.entity.Site;
import webstack.module.site.vo.SiteVO;

import java.util.List;

public interface SiteService extends IService<Site> {

    IPage<SiteVO> pageQuery(SiteQueryDTO dto);

    SiteVO getSiteDetail(Integer id);

    void saveSite(SiteSaveDTO dto);

    void updateSite(SiteUpdateDTO dto);

    /**
     * 查询指定分类（含子分类）下的网站标题列表，供分类级联删除校验使用，避免其他模块直接依赖 SiteMapper
     *
     * @param categoryIds 分类 ID 集合
     * @return 网站标题列表
     */
    List<String> listTitlesByCategoryIds(List<Integer> categoryIds);

    /**
     * 原子递增网站点击数
     * 由 SiteClickLogService 跨模块调用，保持分层隔离
     *
     * @param siteId 网站 ID
     */
    void incrementClickCount(Integer siteId);

    /**
     * 原子递增网站点赞数
     * 由 SiteLikeService 跨模块调用，保持分层隔离
     *
     * @param siteId 网站 ID
     */
    void incrementLikeCount(Integer siteId);

    /**
     * 原子递减网站点赞数（不会减为负数）
     * 由 SiteLikeService 跨模块调用，保持分层隔离
     *
     * @param siteId 网站 ID
     */
    void decrementLikeCount(Integer siteId);

    /**
     * 批量原子递减网站点赞数（不会减为负数）
     *
     * @param siteId 网站 ID
     * @param count  递减数量
     */
    void decrementLikeCountBy(Integer siteId, long count);

    void incrementCommentCount(Integer siteId);
}

