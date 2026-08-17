package com.atguigu.lease.web.admin.custom.interceptor;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    //preHandle表示在处理请求之前去校验token
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {
        //preHandle的三个参数：request可以获取到用户所发请求的全部信息
        //response表示可以在拦截器里对响应内容进行修改
        //handler指所拦截的controller方法
        //返回值是布尔类型，如果是true，拦截器放行请求，false则不放行
        String accessToken = request.getHeader("access-token");//前端一般会把token放在请求头中
        // Header请求头都是kv键值对形式，获取到key（即access_token）即可得到请求携带的token

        Claims claims = JwtUtil.parseToken(accessToken);//校验token

        Long userId = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);

        LoginUserHolder.setLoginUser(new LoginUser(userId, username));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws
            Exception {
        LoginUserHolder.clear();
    }
}
