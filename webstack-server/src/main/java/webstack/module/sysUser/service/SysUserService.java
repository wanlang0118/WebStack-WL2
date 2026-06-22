package webstack.module.sysUser.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import webstack.module.sysUser.dto.LoginDTO;
import webstack.module.sysUser.dto.SysUserAddDTO;
import webstack.module.sysUser.dto.SysUserUpdateDTO;
import webstack.module.sysUser.entity.SysUser;
import webstack.module.sysUser.vo.LoginVO;
import webstack.module.sysUser.vo.SysUserVO;

public interface SysUserService extends IService<SysUser> {

    void addUser(SysUserAddDTO dto);

    void updateUser(SysUserUpdateDTO dto);

    SysUserVO getUserVO(Long id);

    IPage<SysUserVO> pageQuery(String keyword, Integer status, Long page, Long size);

    LoginVO login(LoginDTO dto, String ip);
}
