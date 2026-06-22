package webstack.module.siteClickLog.service.impl;

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
import webstack.module.siteClickLog.dto.SiteClickLogQueryDTO;
import webstack.module.siteClickLog.entity.SiteClickLog;
import webstack.module.siteClickLog.mapper.SiteClickLogMapper;
import webstack.module.siteClickLog.service.SiteClickLogService;
import webstack.module.siteClickLog.vo.SiteClickLogVO;

import java.time.LocalDateTime;

/**
 * 点击记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SiteClickLogServiceImpl extends ServiceImpl<SiteClickLogMapper, SiteClickLog>
        implements SiteClickLogService {

    /**
     * 跨模块依赖走 Service 层，不直接依赖 SiteMapper，保持分层隔离
     */
    private final SiteService siteService;

    @Override
    public IPage<SiteClickLogVO> pageQuery(SiteClickLogQueryDTO dto) {
        return baseMapper.selectClickLogPage(new Page<>(dto.getPage(), dto.getSize()), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordClick(Integer siteId, HttpServletRequest request) {

        // 1. 校验 siteId 存在性，避免外键异常直接爆 500
        Site site = siteService.getById(siteId);
        if (site == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }

        // 2. 提取 IP（处理多级反向代理）
        String ip = IpUtil.getClientIp(request);

        // 3. 提取 userId（AuthInterceptor 存入的是 Long，匿名则 attribute 为 null）
        Long rawUserId = (Long) request.getAttribute("userId");
        Integer userId = rawUserId != null ? rawUserId.intValue() : null;

        // 4. 构建点击记录
        SiteClickLog log = new SiteClickLog();
        log.setSiteId(siteId);
        log.setUserId(userId);
        log.setIp(ip);
        log.setCreateTime(LocalDateTime.now());

        // 5. 插入点击日志
        save(log);

        // 6. 原子递增 click_count（InnoDB 行锁保证原子性）
        siteService.incrementClickCount(siteId);

        // TODO: 防刷限流 — 后续可基于 Redis 实现同一 IP + siteId 短时窗口去重
    }
}
