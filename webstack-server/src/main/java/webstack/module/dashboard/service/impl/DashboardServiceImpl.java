package webstack.module.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webstack.common.exception.BizException;
import webstack.module.dashboard.service.DashboardService;
import webstack.module.dashboard.vo.DailyCountVO;
import webstack.module.dashboard.vo.DashboardTrendVO;
import webstack.module.siteClickLog.mapper.SiteClickLogMapper;
import webstack.module.siteLike.mapper.SiteLikeMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final String[] WEEKDAY_LABELS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    private final SiteClickLogMapper siteClickLogMapper;
    private final SiteLikeMapper siteLikeMapper;

    @Override
    public DashboardTrendVO getTrend(int days) {
        if (days < 1 || days > 30) {
            throw new BizException("统计天数须在 1~30 之间");
        }

        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = endDate.minusDays(days);
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atStartOfDay();

        Map<LocalDate, Long> clickMap = toDateCountMap(siteClickLogMapper.countDailyBetween(startTime, endTime));
        Map<LocalDate, Long> likeMap = toDateCountMap(siteLikeMapper.countDailyBetween(startTime, endTime));

        List<String> dates = new ArrayList<>(days);
        List<Long> clicks = new ArrayList<>(days);
        List<Long> likes = new ArrayList<>(days);

        for (LocalDate d = startDate; d.isBefore(endDate); d = d.plusDays(1)) {
            dates.add(formatWeekday(d));
            clicks.add(clickMap.getOrDefault(d, 0L));
            likes.add(likeMap.getOrDefault(d, 0L));
        }

        DashboardTrendVO vo = new DashboardTrendVO();
        vo.setDates(dates);
        vo.setClicks(clicks);
        vo.setLikes(likes);
        return vo;
    }

    private Map<LocalDate, Long> toDateCountMap(List<DailyCountVO> rows) {
        return rows.stream()
                .collect(Collectors.toMap(DailyCountVO::getStatDate, DailyCountVO::getCount));
    }

    private String formatWeekday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return WEEKDAY_LABELS[dayOfWeek.getValue() - 1];
    }
}
