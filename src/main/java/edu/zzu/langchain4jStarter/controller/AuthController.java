package edu.zzu.langchain4jStarter.controller;

import edu.zzu.langchain4jStarter.bean.LoginRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        // 实际项目中，这里会验证用户名和密码
        // 为演示，我们直接创建一个用户并存入session
        String userId = "user-" + loginRequest.getUsername();
        session.setAttribute("user", userId);
        return "登录成功，用户ID: " + userId;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "已退出登录";
    }
}
