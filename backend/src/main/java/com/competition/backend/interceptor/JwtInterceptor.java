package com.competition.backend.interceptor;

import com.competition.backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.competition.backend.common.Result;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("JWT拦截器开始处理请求: {} {}", request.getMethod(), request.getRequestURI());
        
        // 获取请求头中的token
        String authHeader = request.getHeader(jwtUtil.getTokenHeader());
        log.info("Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith(jwtUtil.getTokenHead())) {
            log.warn("Token不存在或格式错误");
            handleError(response, "Token不存在或格式错误");
            return false;
        }

        // 提取token
        String token = authHeader.substring(jwtUtil.getTokenHead().length()).trim();
        log.info("提取的token: {}", token);

        try {
            // 验证token
            if (!jwtUtil.validateToken(token)) {
                log.warn("Token验证失败");
                handleError(response, "Token验证失败");
                return false;
            }

            // 获取用户信息
            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            Integer userId = jwtUtil.getUserIdFromToken(token);
            String name = jwtUtil.getNameFromToken(token);
            Integer departmentId = jwtUtil.getDepartmentIdFromToken(token);

            log.info("Token验证成功，用户信息: email={}, role={}, userId={}, name={}, departmentId={}", 
                    email, role, userId, name, departmentId);

            // 将用户信息存储在请求属性中
            request.setAttribute("email", email);
            request.setAttribute("role", role);
            request.setAttribute("userId", userId);
            request.setAttribute("name", name);
            request.setAttribute("departmentId", departmentId);

            return true;
        } catch (Exception e) {
            log.error("Token处理过程中发生错误", e);
            handleError(response, "Token处理失败: " + e.getMessage());
            return false;
        }
    }

    private void handleError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, message)));
    }
} 