package webstack.module.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webstack.common.result.Result;
import webstack.common.util.IpUtil;
import webstack.module.sysUser.dto.LoginDTO;
import webstack.module.sysUser.service.SysUserService;
import webstack.module.sysUser.vo.LoginVO;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto, HttpServletRequest request) {
        String ip = IpUtil.getClientIp(request);
        return Result.success(sysUserService.login(dto, ip));
    }
}
