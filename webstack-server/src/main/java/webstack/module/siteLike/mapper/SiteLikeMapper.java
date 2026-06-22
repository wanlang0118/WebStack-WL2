package webstack.module.siteLike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import webstack.module.dashboard.vo.DailyCountVO;
import webstack.module.siteLike.dto.SiteLikeQueryDTO;
import webstack.module.siteLike.entity.SiteLike;
import webstack.module.siteLike.vo.SiteLikeVO;

import java.time.LocalDateTime;
import java.util.List;

public interface SiteLikeMapper extends BaseMapper<SiteLike> {

    IPage<SiteLikeVO> selectLikePage(IPage<SiteLike> page, @Param("dto") SiteLikeQueryDTO dto);

    List<DailyCountVO> countDailyBetween(@Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);
}
