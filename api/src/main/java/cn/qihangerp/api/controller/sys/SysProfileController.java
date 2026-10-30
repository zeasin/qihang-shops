package cn.qihangerp.api.controller.sys;

import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.AjaxResult;
import cn.qihangerp.api.common.BaseController;
import cn.qihangerp.api.common.security.LoginUser;
import cn.qihangerp.api.common.security.SecurityUtils;
import cn.qihangerp.api.common.security.TokenService;
import cn.qihangerp.api.common.utils.IdUtils;
import cn.qihangerp.api.common.utils.StringUtils;
import cn.qihangerp.api.domain.SysUser;
import cn.qihangerp.api.service.ISysUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * 个人信息 业务处理
 *
 * @author qihang
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController
{
    /**
     * 允许上传的头像扩展名
     */
    private static final Set<String> ALLOWED_AVATAR_EXT = Set.of("png", "jpg", "jpeg", "gif");

    /**
     * 头像存储目录
     */
    @Value("${qihang.avatar-path:./upload/avatar}")
    private String avatarPath;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 个人信息
     */
    @GetMapping
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success(user);
//        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
//        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateProfile(@RequestBody SysUser user)
    {
        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();
        user.setUserName(sysUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUserId(sysUser.getUserId());
        user.setPassword(null);
        user.setAvatar(null);
        user.setDeptId(null);
        if (userService.updateUserProfile(user) > 0)
        {
            // 更新缓存用户信息
            sysUser.setNickName(user.getNickName());
            sysUser.setPhonenumber(user.getPhonenumber());
            sysUser.setEmail(user.getEmail());
            sysUser.setSex(user.getSex());
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE, isSaveRequestData = false)
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(String oldPassword, String newPassword)
    {
        LoginUser loginUser = getLoginUser();
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return error("新密码不能与旧密码相同");
        }
        if (userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)) > 0)
        {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        if (file == null || file.isEmpty())
        {
            return error("上传图片异常，请联系管理员");
        }
        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".")
                ? original.substring(original.lastIndexOf('.') + 1).toLowerCase()
                : "png";
        if (!ALLOWED_AVATAR_EXT.contains(ext))
        {
            return error("只允许上传 png/jpg/jpeg/gif 格式的头像");
        }
        LoginUser loginUser = getLoginUser();
        Path dir = Paths.get(avatarPath).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        String fileName = IdUtils.randomUUID() + "." + ext;
        Path target = dir.resolve(fileName).normalize();
        if (!target.startsWith(dir))
        {
            return error("上传图片异常，请联系管理员");
        }
        file.transferTo(target.toFile());

        String avatar = "/system/user/profile/avatar/" + fileName;
        if (userService.updateUserAvatar(loginUser.getUsername(), avatar))
        {
            AjaxResult ajax = AjaxResult.success();
            ajax.put("imgUrl", avatar);
            // 更新缓存用户头像
            loginUser.getUser().setAvatar(avatar);
            tokenService.setLoginUser(loginUser);
            return ajax;
        }
        return error("上传图片异常，请联系管理员");
    }

    /**
     * 读取头像文件（图片标签无法携带请求头，故匿名可访问）
     */
    @GetMapping("/avatar/{fileName}")
    public void avatarFile(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException
    {
        Path dir = Paths.get(avatarPath).toAbsolutePath().normalize();
        Path target = dir.resolve(fileName).normalize();
        if (!target.startsWith(dir) || !Files.exists(target))
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(Files.probeContentType(target));
        Files.copy(target, response.getOutputStream());
    }
}
