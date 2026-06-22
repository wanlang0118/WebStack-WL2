package webstack.module.sysUser.dto;

import lombok.Data;

@Data
public class SysUserUpdateDTO {

    private Long id;

    private String password;

    private String name;

    private String avatar;

    private Integer sex;

    private String email;

    private String phone;

    private Integer status;
}
