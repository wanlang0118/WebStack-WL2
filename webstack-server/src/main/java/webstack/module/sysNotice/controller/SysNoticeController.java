package webstack.module.sysNotice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.exception.BizException;
import webstack.common.interceptor.AuthInterceptor;
import webstack.common.result.Result;
import webstack.common.result.ResultCode;
import webstack.module.sysNotice.dto.SysNoticeQueryDTO;
import webstack.module.sysNotice.dto.SysNoticeSendDTO;
import webstack.module.sysNotice.service.SysNoticeService;
import webstack.module.sysNotice.vo.SysNoticeVO;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class SysNoticeController {

    private final SysNoticeService sysNoticeService;

    @GetMapping("/page")
    public Result<IPage<SysNoticeVO>> page(SysNoticeQueryDTO dto) {
        return Result.success(sysNoticeService.pageQuery(dto));
    }

    @PostMapping("/broadcast")
    public Result<Void> broadcast(@RequestBody @Valid SysNoticeSendDTO dto) {
        sysNoticeService.broadcast(dto);
        return Result.success();
    }

    /**
     * 将通知标记为已读：任意登录用户可调用，但只能操作属于自己的通知
     */
    @PutMapping("/read/{id}")
    @RequireRole
    public Result<Void> markRead(@PathVariable Long id, HttpServletRequest request) {
        Object userId = request.getAttribute(AuthInterceptor.ATTR_USER_ID);
        if (userId == null) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }
        sysNoticeService.markRead(id, Long.valueOf(userId.toString()));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysNoticeService.removeNotice(id);
        return Result.success();
    }

    @PostMapping("/batch-delete")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的数据");
        }
        sysNoticeService.removeBatch(ids);
        return Result.success();
    }
}
