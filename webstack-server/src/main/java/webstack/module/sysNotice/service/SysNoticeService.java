package webstack.module.sysNotice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import webstack.module.sysNotice.dto.SysNoticeQueryDTO;
import webstack.module.sysNotice.dto.SysNoticeSendDTO;
import webstack.module.sysNotice.entity.SysNotice;
import webstack.module.sysNotice.vo.SysNoticeVO;

import java.util.List;

public interface SysNoticeService extends IService<SysNotice> {

    IPage<SysNoticeVO> pageQuery(SysNoticeQueryDTO dto);

    void broadcast(SysNoticeSendDTO dto);

    void createReplyNotice(Integer receiverId, Integer senderId,
                           String senderName, Integer siteId,
                           Long commentId, String replyContent);

    void markRead(Long id, Long currentUserId);

    void removeNotice(Long id);

    void removeBatch(List<Long> ids);
}
