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

        // 1. 模拟会话隔离与认证
        // 检查Session中是否存在用户信息
        if (session.getAttribute(USER_SESSION_KEY) == null) {
            // 如果是登录等特定接口，则放行
            if (request.getRequestURI().contains("/login")) {
                return true;
            }
            
            // 模拟一个简单的认证过程：如果session中没有用户信息，则创建一个
            // 在实际项目中，这里会重定向到登录页面或返回未授权的错误
            String userId = "user-" + UUID.randomUUID().toString().substring(0, 8);
            session.setAttribute(USER_SESSION_KEY, userId);
            System.out.println("模拟新用户认证，创建会话，用户ID: " + userId);
        }

        // 2. 模拟滑动续期
        // Spring Session Redis会自动处理会话的续期（滑动窗口），
        // 每次请求都会刷新Redis中session的过期时间，无需手动编码。
        // 这里打印日志以作演示
        String userId = (String) session.getAttribute(USER_SESSION_KEY);
        System.out.println("用户 " + userId + " 的会话已自动续期。");

        return true;
    }
}
