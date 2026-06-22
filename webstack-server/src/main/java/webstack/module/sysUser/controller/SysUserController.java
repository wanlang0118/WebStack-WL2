package webstack.module.sysUser.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.exception.BizException;
import webstack.common.interceptor.AuthInterceptor;
import webstack.common.result.Result;
import webstack.common.result.ResultCode;
import webstack.module.sysUser.dto.SysUserAddDTO;
import webstack.module.sysUser.dto.SysUserUpdateDTO;
import webstack.module.sysUser.service.SysUserService;
import webstack.module.sysUser.vo.SysUserVO;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@RequireRole(RoleConstant.ADMIN)
public class SysUserController {

    private final SysUserService sysUserService;

    @PostMapping
    public Result<Void> add(@RequestBody @Valid SysUserAddDTO dto) {
        sysUserService.addUser(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated SysUserUpdateDTO dto) {
        dto.setId(id);
        sysUserService.updateUser(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUserUpdateDTO dto = new SysUserUpdateDTO();
        dto.setId(id);
        dto.setStatus(status);
        sysUserService.updateUser(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SysUserVO> detail(@PathVariable Long id) {
        return Result.success(sysUserService.getUserVO(id));
    }

    @GetMapping
    public Result<IPage<SysUserVO>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        return Result.success(sysUserService.pageQuery(keyword, status, page, size));
    }

    /**
     * 获取当前登录用户信息：任意已登录用户均可访问
     */
    @GetMapping("/me")
    @RequireRole
    public Result<SysUserVO> me(HttpServletRequest request) {
        Object userId = request.getAttribute(AuthInterceptor.ATTR_USER_ID);
        if (userId == null) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }
        return Result.success(sysUserService.getUserVO(Long.valueOf(userId.toString())));
    }
}
