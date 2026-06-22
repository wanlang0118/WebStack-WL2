package webstack.module.dashboard.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardTrendVO {

    private List<String> dates;

    private List<Long> clicks;

    private List<Long> likes;
}
