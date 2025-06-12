package org.example.filemanagementsystem03.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.filemanagementsystem03.dao.UserDao;
import org.example.filemanagementsystem03.model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户注册Servlet
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDao userDao = new UserDao();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        
        Map<String, Object> jsonResponse = new HashMap<>();
        
        // 参数验证
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            jsonResponse.put("status", 400);
            jsonResponse.put("message", "用户名和密码不能为空");
        } else if (username.length() < 3 || username.length() > 20) {
            jsonResponse.put("status", 400);
            jsonResponse.put("message", "用户名长度应为3-20位");
        } else if (password.length() < 6) {
            jsonResponse.put("status", 400);
            jsonResponse.put("message", "密码长度至少为6位");
        } else {
            // 创建用户对象
            User user = new User(username, password, email, 1);  // 默认为普通用户角色(1)
            
            // 调用DAO进行注册
            boolean registered = userDao.register(user);
            
            if (registered) {
                // 注册成功
                jsonResponse.put("status", 200);
                jsonResponse.put("message", "注册成功");
            } else {
                // 注册失败
                jsonResponse.put("status", 409);
                jsonResponse.put("message", "注册失败，用户名可能已被使用");
            }
        }
        
        PrintWriter out = response.getWriter();
        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
    }
} 