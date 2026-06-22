package webstack.module.dashboard.service;

import webstack.module.dashboard.vo.DashboardTrendVO;

public interface DashboardService {

    DashboardTrendVO getTrend(int days);
}
