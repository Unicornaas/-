package org.example.filemanagementsystem03.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.filemanagementsystem03.dao.UserDao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 检查用户名是否存在的Servlet
 */
@WebServlet("/checkUsername")
public class CheckUsernameServlet extends HttpServlet {
    private UserDao userDao = new UserDao();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String username = request.getParameter("username");
        
        Map<String, Object> jsonResponse = new HashMap<>();
        
        if (username != null && !username.trim().isEmpty()) {
            boolean exists = userDao.isUsernameExists(username);
            
            if (exists) {
                jsonResponse.put("status", 409);
                jsonResponse.put("message", "用户名已存在");
            } else {
                jsonResponse.put("status", 200);
                jsonResponse.put("message", "用户名可用");
            }
        } else {
            jsonResponse.put("status", 400);
            jsonResponse.put("message", "用户名不能为空");
        }
        
        PrintWriter out = response.getWriter();
        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
    }
} 