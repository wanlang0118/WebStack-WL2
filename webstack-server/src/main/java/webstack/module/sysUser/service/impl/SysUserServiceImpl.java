package webstack.module.sysUser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import webstack.common.constant.RoleConstant;
import webstack.common.constant.UserStatus;
import webstack.common.exception.BizException;
import webstack.common.result.ResultCode;
import webstack.common.util.JwtUtil;
import webstack.module.sysUser.dto.LoginDTO;
import webstack.module.sysUser.dto.SysUserAddDTO;
import webstack.module.sysUser.dto.SysUserUpdateDTO;
import webstack.module.sysUser.entity.SysUser;
import webstack.module.sysUser.mapper.SysUserMapper;
import webstack.module.sysUser.service.SysUserService;
import webstack.module.sysUser.vo.LoginVO;
import webstack.module.sysUser.vo.SysUserVO;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void addUser(SysUserAddDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BizException("用户名已存在");
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(StringUtils.hasText(dto.getRole()) ? dto.getRole() : RoleConstant.USER);
        user.setStatus(UserStatus.ENABLED);
        baseMapper.insert(user);
    }

    @Override
    public void updateUser(SysUserUpdateDTO dto) {
        SysUser user = baseMapper.selectById(dto.getId());
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        user.setName(dto.getName());
        user.setAvatar(dto.getAvatar());
        user.setSex(dto.getSex());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        baseMapper.updateById(user);
    }

    @Override
    public SysUserVO getUserVO(Long id) {
        SysUser user = baseMapper.selectById(id);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public IPage<SysUserVO> pageQuery(String keyword, Integer status, Long page, Long size) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getName, keyword));
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> userPage = baseMapper.selectPage(new Page<>(page, size), wrapper);
        return userPage.convert(u -> {
            SysUserVO vo = new SysUserVO();
            BeanUtils.copyProperties(u, vo);
            return vo;
        });
    }

    @Override
    public LoginVO login(LoginDTO dto, String ip) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser user = baseMapper.selectOne(wrapper);
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == UserStatus.DISABLED) {
            throw new BizException("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        baseMapper.updateById(user);

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(user, vo);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(vo);
        return loginVO;
    }
}
