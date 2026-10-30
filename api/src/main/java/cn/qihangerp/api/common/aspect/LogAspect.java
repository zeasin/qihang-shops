package cn.qihangerp.api.common.aspect;

import cn.qihangerp.api.common.annotation.Log;
import cn.qihangerp.api.common.security.LoginUser;
import cn.qihangerp.api.common.security.SecurityUtils;
import cn.qihangerp.api.common.utils.IpUtils;
import cn.qihangerp.api.common.utils.StringUtils;
import cn.qihangerp.api.domain.SysOperLog;
import cn.qihangerp.api.service.SysOperLogService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.filter.PropertyFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 操作日志切面
 * <p>
 * 拦截所有标注 {@link Log @Log} 的 Controller 方法，记录操作人、请求参数、
 * 返回结果、异常信息与耗时，经 SysOperLogService 异步写入 sys_oper_log。
 * 任何记录失败都不影响业务方法本身的执行。
 *
 * @author qihang
 */
@Aspect
@Component
public class LogAspect
{
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 参数/返回值最长保留字符数，与 sys_oper_log 字段长度对应
     */
    private static final int MAX_CONTENT_LENGTH = 2000;

    /**
     * 敏感字段名（小写比对），序列化请求参数时剔除
     */
    private static final String[] SENSITIVE_FIELDS = {"password", "oldpassword", "newpassword", "confirmpassword", "token"};

    @Autowired
    private SysOperLogService operLogService;

    /**
     * 环绕通知：正常返回记 0（正常），抛异常记 1（异常），异常原样上抛
     */
    @Around("@annotation(controllerLog)")
    public Object around(ProceedingJoinPoint joinPoint, Log controllerLog) throws Throwable
    {
        long beginTime = System.currentTimeMillis();
        try
        {
            Object result = joinPoint.proceed();
            recordOperLog(joinPoint, controllerLog, SysOperLogStatus.SUCCESS,
                    buildResultJson(result, controllerLog), null,
                    System.currentTimeMillis() - beginTime);
            return result;
        }
        catch (Throwable e)
        {
            recordOperLog(joinPoint, controllerLog, SysOperLogStatus.ERROR, null,
                    StringUtils.substring(e.getMessage(), 0, MAX_CONTENT_LENGTH),
                    System.currentTimeMillis() - beginTime);
            throw e;
        }
    }

    private void recordOperLog(ProceedingJoinPoint joinPoint, Log controllerLog,
                               SysOperLogStatus status, String jsonResult, String errorMsg, long costTime)
    {
        try
        {
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(status.value);
            operLog.setErrorMsg(errorMsg);
            operLog.setJsonResult(jsonResult);
            operLog.setCostTime(costTime);
            operLog.setOperTime(new Date());

            operLog.setTitle(controllerLog.title());
            operLog.setBusinessType(controllerLog.businessType().ordinal());
            operLog.setOperatorType(controllerLog.operatorType().ordinal());

            // 方法名（全限定类名.方法名）
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");

            // 请求上下文只能在当前线程内采集
            HttpServletRequest request = currentRequest();
            if (request != null)
            {
                operLog.setRequestMethod(request.getMethod());
                operLog.setOperUrl(StringUtils.substring(request.getRequestURI(), 0, 255));
                operLog.setOperIp(IpUtils.getIpAddr(request));
            }

            // 操作人（登录信息缺失时容错跳过）
            try
            {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null)
                {
                    operLog.setOperName(loginUser.getUsername());
                }
            }
            catch (Exception ignored)
            {
            }

            if (controllerLog.isSaveRequestData())
            {
                operLog.setOperParam(buildRequestJson(joinPoint, controllerLog));
            }

            operLogService.recordOperLog(operLog);
        }
        catch (Exception e)
        {
            log.error("操作日志记录异常: {}", controllerLog.title(), e);
        }
    }

    /**
     * 序列化请求参数：剔除 Servlet 对象与文件上传，敏感字段脱敏，超长截断
     */
    private String buildRequestJson(ProceedingJoinPoint joinPoint, Log controllerLog)
    {
        try
        {
            List<Object> args = new ArrayList<>();
            for (Object arg : joinPoint.getArgs())
            {
                if (arg == null || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse
                        || arg instanceof MultipartFile || arg instanceof MultipartFile[])
                {
                    continue;
                }
                args.add(arg);
            }
            if (args.isEmpty())
            {
                return "{}";
            }
            return truncate(JSON.toJSONString(args, sensitiveFilter(controllerLog.excludeParamNames())));
        }
        catch (Exception e)
        {
            log.warn("操作日志请求参数序列化失败: {}", controllerLog.title());
            return "{}";
        }
    }

    /**
     * 序列化返回结果，isSaveResponseData=false 或空结果时不记录
     */
    private String buildResultJson(Object result, Log controllerLog)
    {
        if (!controllerLog.isSaveResponseData() || result == null)
        {
            return null;
        }
        try
        {
            return truncate(JSON.toJSONString(result));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private PropertyFilter sensitiveFilter(String[] excludeParamNames)
    {
        return (object, name, value) -> {
            if (name == null)
            {
                return true;
            }
            String lowerName = name.toLowerCase();
            for (String sensitive : SENSITIVE_FIELDS)
            {
                if (lowerName.equals(sensitive))
                {
                    return false;
                }
            }
            return Arrays.stream(excludeParamNames).noneMatch(exclude -> exclude.equalsIgnoreCase(name));
        };
    }

    private String truncate(String content)
    {
        return StringUtils.substring(content, 0, MAX_CONTENT_LENGTH);
    }

    private HttpServletRequest currentRequest()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes ? ((ServletRequestAttributes) attributes).getRequest() : null;
    }

    /**
     * 操作状态枚举，对应 sys_oper_log.status 字段
     */
    private enum SysOperLogStatus
    {
        SUCCESS(0), ERROR(1);

        private final int value;

        SysOperLogStatus(int value)
        {
            this.value = value;
        }
    }
}
