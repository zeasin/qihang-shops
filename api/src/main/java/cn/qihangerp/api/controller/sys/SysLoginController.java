package cn.qihangerp.api.controller.sys;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cn.qihangerp.api.common.AjaxResult;
import cn.qihangerp.api.common.constant.Constants;
import cn.qihangerp.api.common.security.SecurityUtils;
import cn.qihangerp.api.domain.SysMenu;
import cn.qihangerp.api.domain.SysUser;
import cn.qihangerp.api.domain.bo.LoginBody;
import cn.qihangerp.api.service.ISysMenuService;
import cn.qihangerp.api.service.SysLoginService;
import cn.qihangerp.api.service.SysPermissionService;

import java.util.List;
import java.util.Set;

/**
 * 登录验证
 * 
 * @author qihang
 */
@AllArgsConstructor
@RestController
public class SysLoginController
{
    private final SysLoginService loginService;

    private final ISysMenuService menuService;

    private final SysPermissionService permissionService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody, HttpServletRequest request) {
        try {
            AjaxResult ajax = AjaxResult.success();
            String token = loginService.login(loginBody.getUsername(), loginBody.getPassword());
            ajax.put(Constants.TOKEN, token);
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
