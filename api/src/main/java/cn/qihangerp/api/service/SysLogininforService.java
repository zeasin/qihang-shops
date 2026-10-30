package cn.qihangerp.api.service;

import cn.qihangerp.api.common.utils.AddressUtils;
import cn.qihangerp.api.common.utils.DateUtils;
import cn.qihangerp.api.common.utils.IpUtils;
import cn.qihangerp.api.domain.SysLogininfor;
import cn.qihangerp.api.mapper.SysLogininforMapper;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 登录/注册日志记录服务
 * <p>
 * 同一条记录三种落点：sys_logininfor 数据表、logs/login.log 专用日志文件、飞书机器人通知。
 * IP、浏览器、操作系统等请求上下文信息在调用线程内采集，落库/地址解析/飞书推送在独立单线程中执行，不阻塞登录主流程。
 *
 * @author qihang
 */
@Slf4j
@Service
public class SysLogininforService
{
    /**
     * 登录日志专用 logger，由 logback 中名为 LOGIN_LOG 的 logger 单独输出到 logs/login.log
     */
    private static final Logger LOGIN_LOG = LoggerFactory.getLogger("LOGIN_LOG");

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "logininfor-record");
        t.setDaemon(true);
        return t;
    });

    @Autowired
    private SysLogininforMapper logininforMapper;

    /**
     * 记录登录日志（成功/失败均记录）
     *
     * @param username 用户账号
     * @param success  是否成功
     * @param message  提示消息
     */
    public void recordLogininfor(String username, boolean success, String message)
    {
        record("用户登录", username, success, message);
    }

    /**
     * 记录注册日志
     *
     * @param username 用户账号
     * @param message  提示消息
     */
    public void recordRegister(String username, String message)
    {
        record("用户注册", username, true, message);
    }

    /**
     * 记录注册失败
     *
     * @param username 用户账号
     * @param message  提示消息
     */
    public void recordRegisterFail(String username, String message)
    {
        record("用户注册", username, false, message);
    }

    private void record(String eventName, String username, boolean success, String message)
    {
        // 请求上下文（IP、User-Agent）只能在当前调用线程内获取
        String ipaddr = currentIpaddr();
        String userAgent = currentUserAgent();
        String browser = parseBrowser(userAgent);
        String os = parseOs(userAgent);
        Date loginTime = DateUtils.getNowDate();

        executor.execute(() -> {
            try
            {
                String location = AddressUtils.getRealAddressByIP(ipaddr);

                // 1、专用登录日志文件
                LOGIN_LOG.info("[{}] 账号:{}, 状态:{}, IP:{}, 地点:{}, 浏览器:{}, 操作系统:{}, 时间:{}, 备注:{}",
                        eventName, username, success ? "成功" : "失败", ipaddr, location, browser, os,
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(loginTime), message);

                // 2、sys_logininfor 数据表
                try
                {
                    SysLogininfor logininfor = new SysLogininfor();
                    logininfor.setUserName(username);
                    logininfor.setIpaddr(ipaddr);
                    logininfor.setLoginLocation(location);
                    logininfor.setBrowser(browser);
                    logininfor.setOs(os);
                    logininfor.setStatus(success ? "0" : "1");
                    logininfor.setMsg(message);
                    logininfor.setLoginTime(loginTime);
                    logininforMapper.insert(logininfor);
                }
                catch (Exception e)
                {
                    log.error("登录日志入库失败: {}", username, e);
                }

            }
            catch (Exception e)
            {
                log.error("登录日志记录失败: {}", username, e);
            }
        });
    }

    /**
     * 当前请求的客户端IP，非请求线程返回 unknown
     */
    private String currentIpaddr()
    {
        HttpServletRequest request = currentRequest();
        return request == null ? "unknown" : IpUtils.getIpAddr(request);
    }

    private String currentUserAgent()
    {
        HttpServletRequest request = currentRequest();
        return request == null ? "" : request.getHeader("User-Agent");
    }

    private HttpServletRequest currentRequest()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes ? ((ServletRequestAttributes) attributes).getRequest() : null;
    }

    private String parseBrowser(String userAgent)
    {
        try
        {
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            return ua.getBrowser() != null ? ua.getBrowser().getName() : "Unknown";
        }
        catch (Exception e)
        {
            return "Unknown";
        }
    }

    private String parseOs(String userAgent)
    {
        try
        {
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            return ua.getOperatingSystem() != null ? ua.getOperatingSystem().getName() : "Unknown";
        }
        catch (Exception e)
        {
            return "Unknown";
        }
    }
}
