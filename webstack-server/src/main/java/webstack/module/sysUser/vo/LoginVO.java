package webstack.module.sysUser.vo;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginVO {

    private String token;

    private SysUserVO userInfo;
}
