package com.smartwms.system.log.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartwms.system.log.annotation.OperationLogAnnotation;
import com.smartwms.system.log.entity.OperationLog;
import com.smartwms.system.log.service.OperationLogService;
import com.smartwms.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) && " +
            "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)) && " +
            "!within(com.smartwms.system.user.controller.AuthController) && " +
            "!within(com.smartwms.system.log.controller.LogController)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        OperationLog operationLog = new OperationLog();

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setIp(getIpAddress(request));
                operationLog.setUserAgent(request.getHeader("User-Agent"));
                operationLog.setUrl(request.getRequestURI());
            }

            operationLog.setUserId(SecurityUtils.getCurrentUserId());
            operationLog.setUsername(SecurityUtils.getCurrentUsername());

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            OperationLogAnnotation annotation = method.getAnnotation(OperationLogAnnotation.class);
            if (annotation != null) {
                operationLog.setModule(annotation.module());
                operationLog.setOperation(annotation.operation());
            }
            Tag tag = joinPoint.getTarget().getClass().getAnnotation(Tag.class);
            Operation apiOperation = method.getAnnotation(Operation.class);
            if (tag != null && (operationLog.getModule() == null || operationLog.getModule().isBlank()))
                operationLog.setModule(tag.name());
            if (apiOperation != null) {
                if (operationLog.getOperation() == null || operationLog.getOperation().isBlank())
                    operationLog.setOperation(apiOperation.summary());
                operationLog.setDescription(apiOperation.summary());
            }

            operationLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + method.getName());
            operationLog.setParams(maskSensitiveData(objectMapper.writeValueAsString(joinPoint.getArgs())));

            Object result = joinPoint.proceed();

            operationLog.setStatus(1);
            operationLog.setDuration(System.currentTimeMillis() - startTime);
            operationLogService.saveLog(operationLog);

            return result;
        } catch (Throwable e) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            operationLog.setDuration(System.currentTimeMillis() - startTime);
            operationLogService.saveLog(operationLog);
            throw e;
        }
    }

    private String maskSensitiveData(String json) {
        return json.replaceAll("(?i)(\\\"(?:password|oldPassword|newPassword)\\\"\\s*:\\s*\\\")[^\\\"]*(\\\")", "$1***$2");
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
