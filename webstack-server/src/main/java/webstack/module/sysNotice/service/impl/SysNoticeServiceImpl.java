package webstack.module.sysNotice.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webstack.common.constant.NoticeType;
import webstack.common.constant.ReadStatus;
import webstack.common.exception.BizException;
import webstack.common.result.ResultCode;
import webstack.module.sysNotice.dto.SysNoticeQueryDTO;
import webstack.module.sysNotice.dto.SysNoticeSendDTO;
import webstack.module.sysNotice.entity.SysNotice;
import webstack.module.sysNotice.mapper.SysNoticeMapper;
import webstack.module.sysNotice.service.SysNoticeService;
import webstack.module.sysNotice.vo.SysNoticeVO;
import webstack.module.sysUser.entity.SysUser;
import webstack.module.sysUser.service.SysUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice>
        implements SysNoticeService {

    private static final int SUMMARY_MAX_LENGTH = 200;

    private final SysUserService sysUserService;

    @Override
    public IPage<SysNoticeVO> pageQuery(SysNoticeQueryDTO dto) {
        return baseMapper.selectNoticePage(new Page<>(dto.getPage(), dto.getSize()), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void broadcast(SysNoticeSendDTO dto) {
        List<SysUser> users = sysUserService.list();
        if (users.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<SysNotice> notices = users.stream().map(user -> {
            SysNotice notice = new SysNotice();
            notice.setSenderId(NoticeType.SYSTEM_SENDER_ID);
            notice.setSenderName(NoticeType.SYSTEM_SENDER_NAME);
            notice.setReceiverId(user.getId().intValue());
            notice.setNoticeType(NoticeType.SYSTEM);
            notice.setTitle(dto.getTitle());
            notice.setSummary(dto.getSummary());
            notice.setContent(dto.getContent());
            notice.setIsRead(ReadStatus.UNREAD);
            notice.setCreateTime(now);
            return notice;
        }).collect(Collectors.toList());

        saveBatch(notices);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReplyNotice(Integer receiverId, Integer senderId,
                                  String senderName, Integer siteId,
                                  Long commentId, String replyContent) {
        SysNotice notice = new SysNotice();
        notice.setSenderId(senderId);
        notice.setSenderName(senderName != null ? senderName : "管理员");
        notice.setReceiverId(receiverId);
        notice.setNoticeType(NoticeType.REPLY);
        notice.setSourceSiteId(siteId);
        notice.setSourceCommentId(commentId);
        notice.setTitle("您的评论收到新回复");
        notice.setSummary(replyContent.length() > SUMMARY_MAX_LENGTH
                ? replyContent.substring(0, SUMMARY_MAX_LENGTH) : replyContent);
        notice.setContent(replyContent);
        notice.setIsRead(ReadStatus.UNREAD);
        notice.setCreateTime(LocalDateTime.now());
        save(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id, Long currentUserId) {
        SysNotice notice = getById(id);
        if (notice == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        // 归属校验：只能操作自己收到的通知，防止越权
        if (currentUserId == null || notice.getReceiverId() == null
                || !currentUserId.equals(notice.getReceiverId().longValue())) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        notice.setIsRead(ReadStatus.READ);
        notice.setReadTime(LocalDateTime.now());
        updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeNotice(Long id) {
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }
}
