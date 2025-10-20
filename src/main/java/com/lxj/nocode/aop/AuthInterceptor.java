package com.lxj.nocode.aop;

import com.lxj.nocode.annotation.AuthCheck;
import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.model.entity.User;
import com.lxj.nocode.model.enums.UserRoleEnum;
import com.lxj.nocode.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect // 声明这是一个切面类，用于实现AOP（面向切面编程）功能
@Component // 将该类声明为Spring的一个组件，让Spring容器能够管理它
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 权限校验方法
     *
     * @param joinPoint 连接点对象
     * @param authCheck 注解对象
     */
    @Around("@annotation(authCheck)") // 环绕通知，拦截带有@AuthCheck注解的方法
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        String mustRole = authCheck.MustRole();
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //不需要权限，返回
        if (userRoleEnum == null) {
            return joinPoint.proceed();
        }
        //鉴定权限
        //获取权限
        User loginUser = userService.getLoginUser(request);
        String userRole = loginUser.getUserRole();
        UserRoleEnum loginUserRoleEnum = UserRoleEnum.getEnumByValue(userRole);
        //没有权限拒绝
        if (loginUserRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //校验管理员
        if (loginUserRoleEnum != UserRoleEnum.ADMIN && userRoleEnum == UserRoleEnum.ADMIN) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //放行
        return joinPoint.proceed();
    }
}
