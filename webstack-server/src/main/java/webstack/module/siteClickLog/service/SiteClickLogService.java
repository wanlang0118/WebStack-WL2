package webstack.module.siteClickLog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import webstack.module.siteClickLog.dto.SiteClickLogQueryDTO;
import webstack.module.siteClickLog.entity.SiteClickLog;
import webstack.module.siteClickLog.vo.SiteClickLogVO;

public interface SiteClickLogService extends IService<SiteClickLog> {

    /**
     * 分页查询点击记录（管理端使用）
     */
    IPage<SiteClickLogVO> pageQuery(SiteClickLogQueryDTO dto);

    /**
     * 记录一次点击：插入日志 + 原子递增 site.click_count
     */
    void recordClick(Integer siteId, HttpServletRequest request);
}

