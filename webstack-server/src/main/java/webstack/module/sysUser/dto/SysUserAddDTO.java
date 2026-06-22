package webstack.module.sysUser.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SysUserAddDTO {

    @NotBlank(message = "登录账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "密码至少8位，且包含大小写字母和数字")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String name;

    private String avatar;

    private Integer sex;

    private String email;

    private String phone;

    /** 角色：ADMIN / USER，不传默认普通用户 */
    @Pattern(regexp = "ADMIN|USER", message = "角色只能是 ADMIN 或 USER")
    private String role;
}
