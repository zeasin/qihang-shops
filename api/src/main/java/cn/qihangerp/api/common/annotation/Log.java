package cn.qihangerp.api.common.annotation;

import cn.qihangerp.api.common.enums.BusinessType;
import cn.qihangerp.api.common.enums.OperatorType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义操作日志记录注解
 * <p>
 * 标注在 Controller 的写操作方法上，由 LogAspect 切面拦截，
 * 将操作人、请求参数、返回结果、异常等信息异步写入 sys_oper_log。
 *
 * @author qihang
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * 模块名称（如：用户管理、采购订单）
     */
    public String title() default "";

    /**
     * 业务操作类型（如：INSERT、UPDATE、DELETE）
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数（GET/POST 参数+请求体）
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存返回的响应数据
     */
    public boolean isSaveResponseData() default true;

    /**
     * 排除指定的请求参数字段（支持 password 等敏感字段脱敏）
     */
    public String[] excludeParamNames() default {};
}
