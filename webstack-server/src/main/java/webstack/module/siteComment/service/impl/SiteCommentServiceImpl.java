package webstack.module.siteComment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webstack.common.constant.CommentStatus;
import webstack.common.exception.BizException;
import webstack.common.result.ResultCode;
import webstack.module.site.service.SiteService;
import webstack.module.siteComment.dto.SiteCommentCreateDTO;
import webstack.module.siteComment.dto.SiteCommentQueryDTO;
import webstack.module.siteComment.dto.SiteCommentReplyDTO;
import webstack.module.siteComment.entity.SiteComment;
import webstack.module.siteComment.mapper.SiteCommentMapper;
import webstack.module.siteComment.service.SiteCommentService;
import webstack.module.siteComment.vo.SiteCommentVO;
import webstack.module.sysNotice.service.SysNoticeService;
import webstack.module.sysUser.entity.SysUser;
import webstack.module.sysUser.service.SysUserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteCommentServiceImpl extends ServiceImpl<SiteCommentMapper, SiteComment>
        implements SiteCommentService {

    private final SiteService siteService;

    private final SysNoticeService sysNoticeService;

    private final SysUserService sysUserService;

    @Override
    public IPage<SiteCommentVO> pageQuery(SiteCommentQueryDTO dto) {
        IPage<SiteCommentVO> topPage = baseMapper.selectCommentPage(
                new Page<>(dto.getPage(), dto.getSize()), dto);

        List<SiteCommentVO> topComments = topPage.getRecords();
        if (topComments.isEmpty()) {
            return topPage;
        }

        List<Long> topIds = topComments.stream()
                .map(SiteCommentVO::getId)
                .collect(Collectors.toList());

        List<SiteCommentVO> children = baseMapper.selectChildrenByParentIds(topIds);

        Map<Long, List<SiteCommentVO>> childrenMap = children.stream()
                .collect(Collectors.groupingBy(SiteCommentVO::getParentId));

        topComments.forEach(parent ->
                parent.setChildren(childrenMap.getOrDefault(parent.getId(), new ArrayList<>()))
        );

        return topPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(SiteCommentCreateDTO dto, Integer userId) {
        SiteComment comment = new SiteComment();
        comment.setSiteId(dto.getSiteId());
        comment.setContent(dto.getContent());
        comment.setParentId(resolveTopLevelParentId(dto.getParentId()));
        comment.setGuestName(dto.getGuestName());
        comment.setGuestEmail(dto.getGuestEmail());
        comment.setGuestUrl(dto.getGuestUrl());
        comment.setUserId(userId);
        comment.setStatus(CommentStatus.NORMAL);
        comment.setCreateTime(LocalDateTime.now());
        save(comment);
        siteService.incrementCommentCount(dto.getSiteId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        SiteComment comment = getById(id);
        if (comment == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        comment.setStatus(status);
        updateById(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reply(SiteCommentReplyDTO dto) {
        SiteComment comment = new SiteComment();
        BeanUtils.copyProperties(dto, comment);
        comment.setParentId(resolveTopLevelParentId(dto.getParentId()));
        comment.setStatus(CommentStatus.NORMAL);
        comment.setCreateTime(LocalDateTime.now());
        save(comment);

        if (comment.getParentId() != null) {
            SiteComment parent = getById(comment.getParentId());
            if (parent != null && parent.getUserId() != null) {
                String senderName = "管理员";
                if (comment.getUserId() != null) {
                    SysUser sender = sysUserService.getById(comment.getUserId());
                    if (sender != null) {
                        senderName = sender.getName() != null ? sender.getName() : sender.getUsername();
                    }
                }
                sysNoticeService.createReplyNotice(
                        parent.getUserId(),
                        comment.getUserId(),
                        senderName,
                        comment.getSiteId(),
                        comment.getId(),
                        comment.getContent()
                );
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContent(Long id, String content) {
        SiteComment comment = getById(id);
        if (comment == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        comment.setContent(content);
        updateById(comment);
    }

    /**
     * 抖音式两层展示：所有回复都挂在顶级评论下。
     * 若 parentId 指向子评论，则归并到其顶级父评论下，保证可一直回复但只显示两层。
     */
    private Long resolveTopLevelParentId(Long parentId) {
        if (parentId == null) {
            return null;
        }
        SiteComment parent = getById(parentId);
        if (parent == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        return parent.getParentId() != null ? parent.getParentId() : parentId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeComment(Long id) {
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
