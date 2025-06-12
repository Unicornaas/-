package org.example.filemanagementsystem03.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.filemanagementsystem03.dao.FileDao;
import org.example.filemanagementsystem03.model.FileInfo;
import org.example.filemanagementsystem03.model.User;
import org.example.filemanagementsystem03.service.FileService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件管理Servlet - 提供管理员相关功能
 */
@WebServlet(urlPatterns = {"/admin/files", "/admin/block"})
public class FileManageServlet extends HttpServlet {
    private FileDao fileDao = new FileDao();
    private FileService fileService = new FileService();
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final int PAGE_SIZE = 5; // 每页显示的文件数量

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        // 检查是否为管理员
        if (currentUser == null || !currentUser.isAdmin()) {
            sendJsonErrorResponse(response, 403, "只有管理员才能访问此功能");
            return;
        }
        
        String path = request.getServletPath();
        
        if ("/admin/files".equals(path)) {
            // 获取管理员可见的所有文件列表
            int page = getPageParam(request);
            
            // 获取总文件数和总页数
            int totalFiles = fileDao.getAllFilesCount();
            int totalPages = (int) Math.ceil((double) totalFiles / PAGE_SIZE);
            
            // 获取文件列表
            List<FileInfo> files = fileDao.getAllFilesForAdmin(page, PAGE_SIZE);
            
            // 处理文件可下载状态
            processFileDownloadPermissions(files, currentUser);
            
            // 发送响应
            sendJsonFileListResponse(response, files, page, totalPages);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        // 检查是否为管理员
        if (currentUser == null || !currentUser.isAdmin()) {
            sendJsonErrorResponse(response, 403, "只有管理员才能访问此功能");
            return;
        }
        
        String path = request.getServletPath();
        
        if ("/admin/block".equals(path)) {
            // 处理文件封禁/解封请求
            String fileIdParam = request.getParameter("fileId");
            String blockStatusParam = request.getParameter("block");
            
            if (fileIdParam == null || blockStatusParam == null) {
                sendJsonErrorResponse(response, 400, "请求参数不完整");
                return;
            }
            
            try {
                int fileId = Integer.parseInt(fileIdParam);
                boolean blockStatus = Boolean.parseBoolean(blockStatusParam);
                
                boolean result = fileService.toggleFileBlockStatus(fileId, blockStatus, currentUser);
                
                if (result) {
                    Map<String, Object> jsonResponse = new HashMap<>();
                    jsonResponse.put("status", 200);
                    jsonResponse.put("message", blockStatus ? "文件已封禁" : "文件已解封");
                    
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print(objectMapper.writeValueAsString(jsonResponse));
                    out.flush();
                } else {
                    sendJsonErrorResponse(response, 500, "操作失败");
                }
            } catch (NumberFormatException e) {
                sendJsonErrorResponse(response, 400, "无效的文件ID");
            } catch (Exception e) {
                sendJsonErrorResponse(response, 500, e.getMessage());
            }
        }
    }
    
    /**
     * 获取请求的页码参数
     */
    private int getPageParam(HttpServletRequest request) {
        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            }
        } catch (NumberFormatException e) {
            // 忽略格式错误，使用默认页码
        }
        return page;
    }
    
    /**
     * 处理文件的下载权限
     */
    private void processFileDownloadPermissions(List<FileInfo> files, User currentUser) {
        for (FileInfo file : files) {
            // 管理员始终可以下载所有文件
            file.setCanDownload(true);
        }
    }
    
    /**
     * 发送JSON格式的文件列表响应
     */
    private void sendJsonFileListResponse(HttpServletResponse response, List<FileInfo> files, int currentPage, int totalPages) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("status", 200);
        jsonResponse.put("files", files);
        jsonResponse.put("currentPage", currentPage);
        jsonResponse.put("totalPages", totalPages);
        
        PrintWriter out = response.getWriter();
        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
    }
    
    /**
     * 发送JSON格式的错误响应
     */
    private void sendJsonErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("status", status);
        jsonResponse.put("message", message);
        
        PrintWriter out = response.getWriter();
        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
    }
} 