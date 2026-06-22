package webstack.module.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.result.Result;
import webstack.module.dashboard.service.DashboardService;
import webstack.module.dashboard.vo.DashboardTrendVO;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/trend")
    public Result<DashboardTrendVO> trend(@RequestParam(defaultValue = "7") int days) {
        return Result.success(dashboardService.getTrend(days));
    }
}
