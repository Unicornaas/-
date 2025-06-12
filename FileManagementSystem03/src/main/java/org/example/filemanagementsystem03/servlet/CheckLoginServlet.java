package org.example.filemanagementsystem03.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.filemanagementsystem03.model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 检查用户登录状态的Servlet
 */
@WebServlet("/checkLogin")
public class CheckLoginServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> jsonResponse = new HashMap<>();
        
        if (currentUser != null) {
            // 用户已登录
            jsonResponse.put("status", 200);
            jsonResponse.put("message", "已登录");
            
            // 创建用户信息对象（不包含密码等敏感信息）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", currentUser.getId());
            userInfo.put("username", currentUser.getUsername());
            userInfo.put("email", currentUser.getEmail());
            userInfo.put("role", currentUser.getRole());
            userInfo.put("isAdmin", currentUser.isAdmin());
            
            jsonResponse.put("user", userInfo);
        } else {
            // 用户未登录
            jsonResponse.put("status", 401);
            jsonResponse.put("message", "未登录");
        }
        
        PrintWriter out = response.getWriter();
        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
    }
} 