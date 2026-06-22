package webstack.module.siteClickLog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.module.siteClickLog.dto.SiteClickLogQueryDTO;
import webstack.module.siteClickLog.service.SiteClickLogService;
import webstack.module.siteClickLog.vo.SiteClickLogVO;

@RestController
@RequestMapping("/api/site/click")
@RequiredArgsConstructor
public class SiteClickLogController {

    private final SiteClickLogService siteClickLogService;

    /**
     * 分页查询点击记录（管理端，需要管理员）
     */
    @GetMapping("/page")
    @RequireRole(RoleConstant.ADMIN)
    public Result<IPage<SiteClickLogVO>> page(SiteClickLogQueryDTO dto) {
        return Result.success(siteClickLogService.pageQuery(dto));
    }

    /**
     * 记录一次网站点击（匿名可访问）
     */
    @PostMapping("/{siteId}")
    public Result<Void> click(@PathVariable Integer siteId, HttpServletRequest request) {
        siteClickLogService.recordClick(siteId, request);
        return Result.success();
    }
}
