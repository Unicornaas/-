package org.example.filemanagementsystem03.servlet;

import org.example.filemanagementsystem03.model.Report;
import org.example.filemanagementsystem03.model.User;
import org.example.filemanagementsystem03.service.ReportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * 文件举报提交Servlet
 */
@WebServlet("/report/submit")
public class ReportSubmitServlet extends HttpServlet {
    private ReportService reportService = new ReportService();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // 获取会话中的用户信息
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 检查用户是否已登录
        if (user == null) {
            out.println("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        
        try {
            // 获取请求参数
            int fileId = Integer.parseInt(request.getParameter("fileId"));
            String reason = request.getParameter("reason");
            
            // 参数验证
            if (reason == null || reason.trim().isEmpty()) {
                out.println("{\"success\":false,\"message\":\"举报原因不能为空\"}");
                return;
            }
            
            // 创建举报对象
            Report report = new Report(fileId, user.getId(), reason);
            
            // 提交举报
            boolean success = reportService.submitReport(report);
            
            if (success) {
                out.println("{\"success\":true,\"message\":\"举报提交成功\"}");
            } else {
                out.println("{\"success\":false,\"message\":\"举报提交失败\"}");
            }
        } catch (NumberFormatException e) {
            out.println("{\"success\":false,\"message\":\"参数格式错误\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"系统错误\"}");
        }
    }
} 