package webstack.module.siteClickLog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import webstack.module.dashboard.vo.DailyCountVO;
import webstack.module.siteClickLog.dto.SiteClickLogQueryDTO;
import webstack.module.siteClickLog.entity.SiteClickLog;
import webstack.module.siteClickLog.vo.SiteClickLogVO;

import java.time.LocalDateTime;
import java.util.List;

public interface SiteClickLogMapper extends BaseMapper<SiteClickLog> {

    IPage<SiteClickLogVO> selectClickLogPage(IPage<SiteClickLog> page, @Param("dto") SiteClickLogQueryDTO dto);

    List<DailyCountVO> countDailyBetween(@Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);
}
