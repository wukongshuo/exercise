package com.xxw.security;

import com.xxw.annotation.HasRole;
import com.xxw.exception.BusinessException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class AccessInterceptor extends HandlerInterceptorAdapter {

    private String secret;

    public AccessInterceptor(String secret) {
        this.secret = secret;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        String authorization = request.getHeader("Authorization");

        if (authorization == null || "".equals(authorization)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            throw BusinessException.error("iam-4003", "请首先登陆！");
        }

        // 处理handler;
        if (handler instanceof HandlerMethod) {
            Identity identity = null;
            try {
                identity = Identity.buildFromJwtToken(authorization, secret);

            } catch (SignatureException sigException) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                throw BusinessException.error("iam-4002", "解码错误，该用户的授权码错误！");
            } catch (ExpiredJwtException expiredJwtException) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                throw BusinessException.error("iam-4001", "Token 过期请重新登录！");
            }

            HandlerMethod handlerMethod = (HandlerMethod) handler;

            HasRole hasRole = handlerMethod.getMethodAnnotation(HasRole.class);
            if (hasRole != null) {
                String[] roles = hasRole.value();
                if (!CollectionUtils.containsInstance(Arrays.asList(roles), identity.getUserRoles())) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    throw BusinessException.error("401", "该用户没有权限访问此接口！");
                }
            }

            SecurityContextHolder.getContext().setAuthentication(identity);
            return true;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
