package webstack.module.sysUser.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserVO {

    private Long id;
    private String username;
    private String name;
    private String avatar;
    private Integer sex;
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastLoginTime;
}
