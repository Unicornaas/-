package org.example.filemanagementsystem03.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.filemanagementsystem03.dao.UserDao;
import org.example.filemanagementsystem03.model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录Servlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDao userDao = new UserDao();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> jsonResponse = new HashMap<>();
        
        // 参数验证
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            jsonResponse.put("status", 400);
            jsonResponse.put("message", "用户名和密码不能为空");
        } else {
            // 尝试登录
            User user = userDao.login(username, password);
            
            if (user != null) {
                // 登录成功，保存用户信息到会话
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                jsonResponse.put("status", 200);
                jsonResponse.put("message", "登录成功");
                
                // 创建用户信息对象（不包含密码等敏感信息）
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("role", user.getRole());
                userInfo.put("isAdmin", user.isAdmin());
                
                jsonResponse.put("user", userInfo);
            } else {
                // 登录失败
                jsonResponse.put("status", 401);
                jsonResponse.put("message", "用户名或密码错误");
            }
        }
        
        PrintWriter out = response.getWriter();
        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
    }
} 