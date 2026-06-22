package webstack.module.siteLike.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webstack.common.exception.BizException;
import webstack.common.result.ResultCode;
import webstack.common.util.IpUtil;
import webstack.module.site.entity.Site;
import webstack.module.site.service.SiteService;
import webstack.module.siteLike.dto.SiteLikeQueryDTO;
import webstack.module.siteLike.entity.SiteLike;
import webstack.module.siteLike.mapper.SiteLikeMapper;
import webstack.module.siteLike.service.SiteLikeService;
import webstack.module.siteLike.vo.SiteLikeVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteLikeServiceImpl extends ServiceImpl<SiteLikeMapper, SiteLike>
        implements SiteLikeService {

    private final SiteService siteService;

    @Override
    public IPage<SiteLikeVO> pageQuery(SiteLikeQueryDTO dto) {
        return baseMapper.selectLikePage(new Page<>(dto.getPage(), dto.getSize()), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(Integer siteId, HttpServletRequest request) {
        Site site = siteService.getById(siteId);
        if (site == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }

        Integer userId = optionalUserId(request);
        String ip = IpUtil.getClientIp(request);

        // 幂等：已点赞则直接返回
        if (count(buildDeduplicationWrapper(siteId, userId, ip)) > 0) {
            return;
        }

        SiteLike like = new SiteLike();
        like.setSiteId(siteId);
        like.setUserId(userId);
        like.setIp(ip);
        like.setCreateTime(LocalDateTime.now());
        save(like);

        siteService.incrementLikeCount(siteId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlike(Integer siteId, HttpServletRequest request) {
        Integer userId = optionalUserId(request);
        String ip = IpUtil.getClientIp(request);

        if (remove(buildDeduplicationWrapper(siteId, userId, ip))) {
            siteService.decrementLikeCount(siteId);
        }
    }

    @Override
    public boolean isLiked(Integer siteId, HttpServletRequest request) {
        Integer userId = optionalUserId(request);
        String ip = IpUtil.getClientIp(request);
        return count(buildDeduplicationWrapper(siteId, userId, ip)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRecord(Long id) {
        SiteLike like = getById(id);
        if (like == null) {
            return;
        }
        if (removeById(id)) {
            siteService.decrementLikeCount(like.getSiteId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<SiteLike> likes = listByIds(ids);
        if (likes.isEmpty()) {
            return;
        }
        removeByIds(ids);
        Map<Integer, Long> siteIdToCount = likes.stream()
                .collect(Collectors.groupingBy(SiteLike::getSiteId, Collectors.counting()));
        // 每个网站只发一条按量递减的 UPDATE，避免循环内逐条写库
        siteIdToCount.forEach(siteService::decrementLikeCountBy);
    }

    /**
     * 构造去重查询条件：登录用户按 (site_id, user_id)，游客按 (site_id, ip, user_id IS NULL)
     */
    private LambdaQueryWrapper<SiteLike> buildDeduplicationWrapper(Integer siteId, Integer userId, String ip) {
        LambdaQueryWrapper<SiteLike> wrapper = new LambdaQueryWrapper<SiteLike>()
                .eq(SiteLike::getSiteId, siteId);
        if (userId != null) {
            wrapper.eq(SiteLike::getUserId, userId);
        } else {
            wrapper.isNull(SiteLike::getUserId)
                   .eq(SiteLike::getIp, ip);
        }
        return wrapper;
    }

    /**
     * 从请求中提取用户 ID，未登录返回 null（不抛异常，允许游客操作）
     */
    private Integer optionalUserId(HttpServletRequest request) {
        Long rawUserId = (Long) request.getAttribute("userId");
        return rawUserId != null ? rawUserId.intValue() : null;
    }
}
