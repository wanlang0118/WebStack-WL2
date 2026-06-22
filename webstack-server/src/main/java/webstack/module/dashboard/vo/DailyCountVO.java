package webstack.module.dashboard.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyCountVO {

    private LocalDate statDate;

    private Long count;
}
