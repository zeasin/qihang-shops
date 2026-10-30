package cn.qihangerp.api.service;

import cn.qihangerp.api.common.security.LoginUser;
import cn.qihangerp.api.common.security.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 权限校验服务。
 *
 * <p>Bean 名为 {@code ss}，供 {@code @PreAuthorize("@ss.hasPermi('xxx')")} 使用。
 * Spring Boot 3.0 默认不开启方法级安全，需在 {@code SecurityConfig} 上显式
 * {@code @EnableMethodSecurity}，否则上述注解不会生效。</p>
 *
 * @author qihang
 */
@Service("ss")
public class PermissionService {

    /**
     * 超级管理员拥有的通配权限
     */
    public static final String ALL_PERMISSION = "*:*:*";

    /**
     * 判断当前登录用户是否拥有指定权限标识
     *
     * @param permission 权限标识，如 {@code system:menu:list}
     * @return true 表示放行
     */
    public boolean hasPermi(String permission) {
        if (StringUtils.isBlank(permission)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        Set<String> permissions = loginUser.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        if (permissions.contains(ALL_PERMISSION)) {
            return true;
        }
        for (String perm : permissions) {
            if (permission.equals(perm)) {
                return true;
            }
        }
        return false;
    }
}
