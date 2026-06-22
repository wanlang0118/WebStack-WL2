package webstack.module.siteComment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.interceptor.AuthInterceptor;
import webstack.common.result.Result;
import webstack.module.siteComment.dto.SiteCommentCreateDTO;
import webstack.module.siteComment.dto.SiteCommentQueryDTO;
import webstack.module.siteComment.dto.SiteCommentReplyDTO;
import webstack.module.siteComment.service.SiteCommentService;
import webstack.module.siteComment.vo.SiteCommentVO;

import java.util.List;

@RestController
@RequestMapping("/api/site/comment")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class SiteCommentController {

    private final SiteCommentService siteCommentService;

    @GetMapping("/page")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<IPage<SiteCommentVO>> page(SiteCommentQueryDTO dto) {
        return Result.success(siteCommentService.pageQuery(dto));
    }

    @PostMapping("/submit")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.USER})
    public Result<Void> submit(@RequestBody @Valid SiteCommentCreateDTO dto,
                               HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute(AuthInterceptor.ATTR_USER_ID);
        siteCommentService.createComment(dto, userId);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        siteCommentService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/reply")
    public Result<Void> reply(@RequestBody @Valid SiteCommentReplyDTO dto) {
        siteCommentService.reply(dto);
        return Result.success();
    }

    @PutMapping("/content/{id}")
    public Result<Void> updateContent(@PathVariable Long id, @RequestParam String content) {
        siteCommentService.updateContent(id, content);
        return Result.success();
    }

    @DeleteMapping("/record/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        siteCommentService.removeComment(id);
        return Result.success();
    }

    @PostMapping("/batch-delete")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的数据");
        }
        siteCommentService.removeBatch(ids);
        return Result.success();
    }
}
