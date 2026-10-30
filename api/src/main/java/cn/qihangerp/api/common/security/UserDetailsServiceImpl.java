package cn.qihangerp.api.common.security;

import cn.qihangerp.api.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import cn.qihangerp.api.common.enums.UserStatus;
import cn.qihangerp.api.common.exception.ServiceException;
import cn.qihangerp.api.common.utils.MessageUtils;
import cn.qihangerp.api.common.utils.StringUtils;
import cn.qihangerp.api.domain.SysUser;
import cn.qihangerp.api.service.ISysUserService;
import cn.qihangerp.api.service.SysPermissionService;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户验证处理
 *
 * @author qihang
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        SysUser user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user))
        {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }
        else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException(MessageUtils.message("user.blocked"));
        }
//        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
//        String password = usernamePasswordAuthenticationToken.getCredentials().toString();
//        if(!password.equals(user.getPassword())) {
            passwordService.validate(user);
//        }

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user)
    {
        // 权限取自菜单表：超级管理员(user_id=1)拥有 *:*:*，其他用户按所分配菜单的 perms 汇总
        Set<String> perms = permissionService.getMenuPermission(user);
        return new LoginUser(user.getUserId(), user.getDeptId(), user, perms);
    }
}
