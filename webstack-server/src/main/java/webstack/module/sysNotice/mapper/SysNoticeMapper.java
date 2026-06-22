package webstack.module.sysNotice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import webstack.module.sysNotice.dto.SysNoticeQueryDTO;
import webstack.module.sysNotice.entity.SysNotice;
import webstack.module.sysNotice.vo.SysNoticeVO;

public interface SysNoticeMapper extends BaseMapper<SysNotice> {

    IPage<SysNoticeVO> selectNoticePage(IPage<SysNotice> page, @Param("dto") SysNoticeQueryDTO dto);
}
