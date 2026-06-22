package webstack.module.site.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import webstack.module.site.dto.SiteQueryDTO;
import webstack.module.site.entity.Site;

public interface SiteMapper extends BaseMapper<Site> {

    IPage<Site> selectSitePage(IPage<Site> page, @Param("dto") SiteQueryDTO dto);

    @Update("UPDATE site SET click_count = click_count + 1 WHERE id = #{siteId}")
    void incrementClickCount(@Param("siteId") Integer siteId);

    @Update("UPDATE site SET like_count = like_count + 1 WHERE id = #{siteId}")
    void incrementLikeCount(@Param("siteId") Integer siteId);

    @Update("UPDATE site SET like_count = like_count - 1 WHERE id = #{siteId} AND like_count > 0")
    void decrementLikeCount(@Param("siteId") Integer siteId);

    @Update("UPDATE site SET like_count = GREATEST(like_count - #{count}, 0) WHERE id = #{siteId}")
    void decrementLikeCountBy(@Param("siteId") Integer siteId, @Param("count") long count);

    @Update("UPDATE site SET comment_count = comment_count + 1 WHERE id = #{siteId}")
    void incrementCommentCount(@Param("siteId") Integer siteId);
}
