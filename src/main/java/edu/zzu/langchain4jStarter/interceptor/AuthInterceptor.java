package edu.zzu.langchain4jStarter.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String USER_SESSION_KEY = "user";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        // 检查Session中是否存在用户信息
        if (session.getAttribute(USER_SESSION_KEY) == null) {
            // 如果是登录接口，则放行
            if (request.getRequestURI().contains("/api/auth/login")) {
                return true;
            }
            
            // 如果未登录，返回401未授权错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Please login first.");
            return false;
        }

        // 2. 模拟滑动续期
        // Spring Session Redis会自动处理会话的续期（滑动窗口），
        // 每次请求都会刷新Redis中session的过期时间，无需手动编码。
        String userId = (String) session.getAttribute(USER_SESSION_KEY);
        System.out.println("User " + userId + " accessed " + request.getRequestURI() + ". Session renewed.");


        return true;
    }
}
