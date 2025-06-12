package org.example.filemanagementsystem03.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.filemanagementsystem03.model.Report;
import org.example.filemanagementsystem03.model.User;
import org.example.filemanagementsystem03.service.ReportService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 举报管理Servlet
 */
@WebServlet("/report/manage")
public class ReportManageServlet extends HttpServlet {
    private ReportService reportService = new ReportService();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // 获取会话中的用户信息
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 检查用户是否有管理员权限
        if (user == null || !user.isAdmin()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "无权限访问");
            out.println(objectMapper.writeValueAsString(result));
            return;
        }
        
        try {
            // 获取请求参数
            String action = request.getParameter("action");
            System.out.println("举报管理GET请求: action=" + action);
            
            if ("list".equals(action)) {
                // 获取举报列表
                boolean onlyPending = "true".equals(request.getParameter("pending"));
                boolean onlyProcessed = "true".equals(request.getParameter("processed"));
                String statusParam = request.getParameter("status");
                List<Report> reports = new ArrayList<>();
                
                // 记录请求信息
                System.out.println("举报列表请求: pending=" + onlyPending + ", processed=" + onlyProcessed + ", status=" + statusParam);
                
                try {
                    if (onlyPending) {
                        // 获取待处理举报
                        reports = reportService.getPendingReports();
                        System.out.println("获取待处理举报数量: " + (reports != null ? reports.size() : 0));
                    } else if (onlyProcessed) {
                        // 获取已处理举报，可能按状态筛选
                        if (statusParam != null && !statusParam.isEmpty()) {
                            int status = Integer.parseInt(statusParam);
                            reports = reportService.getReportsByStatus(status);
                            System.out.println("获取状态为" + status + "的举报数量: " + (reports != null ? reports.size() : 0));
                        } else {
                            // 获取所有已处理举报（状态为1或2）
                            reports = reportService.getProcessedReports();
                            System.out.println("获取所有已处理举报数量: " + (reports != null ? reports.size() : 0));
                        }
                    } else {
                        // 获取所有举报
                        reports = reportService.getAllReports();
                        System.out.println("获取所有举报数量: " + (reports != null ? reports.size() : 0));
                    }
                } catch (Exception e) {
                    System.err.println("获取举报列表异常: " + e.getMessage());
                    e.printStackTrace();
                    reports = new ArrayList<>(); // 确保不会返回null
                }
                
                // 确保reports不为null
                if (reports == null) {
                    reports = new ArrayList<>();
                }
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("reports", reports);
                String jsonResult = objectMapper.writeValueAsString(result);
                System.out.println("返回JSON数据长度: " + jsonResult.length());
                out.println(jsonResult);
            } else if ("detail".equals(action)) {
                // 获取举报详情
                try {
                    String idParam = request.getParameter("id");
                    if (idParam == null || idParam.trim().isEmpty()) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("message", "缺少举报ID参数");
                        out.println(objectMapper.writeValueAsString(result));
                        return;
                    }
                    
                    int reportId = Integer.parseInt(idParam);
                    System.out.println("获取举报详情: ID=" + reportId);
                    
                    Report report = reportService.getReportById(reportId);
                    
                    if (report != null) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("report", report);
                        String jsonResult = objectMapper.writeValueAsString(result);
                        System.out.println("返回举报详情JSON数据长度: " + jsonResult.length());
                        out.println(jsonResult);
                    } else {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("message", "举报不存在");
                        out.println(objectMapper.writeValueAsString(result));
                    }
                } catch (NumberFormatException e) {
                    System.err.println("举报ID格式错误: " + request.getParameter("id"));
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "举报ID格式错误");
                    out.println(objectMapper.writeValueAsString(result));
                } catch (Exception e) {
                    System.err.println("获取举报详情异常: " + e.getMessage());
                    e.printStackTrace();
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "获取举报详情失败: " + e.getMessage());
                    out.println(objectMapper.writeValueAsString(result));
                }
            } else {
                System.err.println("无效的举报管理GET操作: " + action);
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "无效的操作");
                out.println(objectMapper.writeValueAsString(result));
            }
        } catch (Exception e) {
            System.err.println("举报管理GET请求处理异常: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
            out.println(objectMapper.writeValueAsString(result));
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // 获取会话中的用户信息
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 检查用户是否有管理员权限
        if (user == null || !user.isAdmin()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "无权限访问");
            out.println(objectMapper.writeValueAsString(result));
            return;
        }
        
        try {
            // 获取请求参数
            String action = request.getParameter("action");
            System.out.println("举报管理POST请求: action=" + action);
            
            if ("handle".equals(action)) {
                try {
                    // 处理举报
                    String idParam = request.getParameter("id");
                    String statusParam = request.getParameter("status");
                    
                    if (idParam == null || idParam.trim().isEmpty()) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("message", "缺少举报ID参数");
                        out.println(objectMapper.writeValueAsString(result));
                        return;
                    }
                    
                    if (statusParam == null || statusParam.trim().isEmpty()) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("message", "缺少处理状态参数");
                        out.println(objectMapper.writeValueAsString(result));
                        return;
                    }
                    
                    int reportId = Integer.parseInt(idParam);
                    int status = Integer.parseInt(statusParam); // 1: 批准, 2: 驳回
                    String note = request.getParameter("note");
                    
                    System.out.println("处理举报: ID=" + reportId + ", 状态=" + status + ", 备注=" + note);
                    
                    // 参数验证
                    if (status != 1 && status != 2) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", false);
                        result.put("message", "无效的处理状态");
                        out.println(objectMapper.writeValueAsString(result));
                        return;
                    }
                    
                    // 处理举报
                    boolean success = reportService.handleReport(reportId, status, user.getId(), note);
                    
                    Map<String, Object> result = new HashMap<>();
                    if (success) {
                        result.put("success", true);
                        result.put("message", "举报处理成功");
                        System.out.println("举报处理成功: ID=" + reportId);
                    } else {
                        result.put("success", false);
                        result.put("message", "举报处理失败");
                        System.err.println("举报处理失败: ID=" + reportId);
                    }
                    out.println(objectMapper.writeValueAsString(result));
                } catch (NumberFormatException e) {
                    System.err.println("举报处理参数格式错误: " + e.getMessage());
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "参数格式错误: " + e.getMessage());
                    out.println(objectMapper.writeValueAsString(result));
                } catch (Exception e) {
                    System.err.println("举报处理异常: " + e.getMessage());
                    e.printStackTrace();
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "举报处理出错: " + e.getMessage());
                    out.println(objectMapper.writeValueAsString(result));
                }
            } else {
                System.err.println("无效的举报管理POST操作: " + action);
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "无效的操作");
                out.println(objectMapper.writeValueAsString(result));
            }
        } catch (Exception e) {
            System.err.println("举报管理POST请求处理异常: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
            out.println(objectMapper.writeValueAsString(result));
        }
    }
} 