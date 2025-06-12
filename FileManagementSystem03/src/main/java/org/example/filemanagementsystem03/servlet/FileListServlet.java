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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件列表Servlet - 处理文件列表请求
 */
@WebServlet(urlPatterns = {"/files/public", "/files/private"})
public class FileListServlet extends HttpServlet {
    private FileDao fileDao = new FileDao();
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final int PAGE_SIZE = 5; // 每页显示的文件数量

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        // 获取请求的页码，默认为第1页
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
        
        if ("/files/public".equals(path)) {
            // 处理公共文件列表请求
            int totalFiles = fileDao.getFileCount(true, null);
            int totalPages = (int) Math.ceil((double) totalFiles / PAGE_SIZE);
            List<FileInfo> files = fileDao.getPublicFiles(page, PAGE_SIZE);
            processFileDownloadPermissions(files, currentUser);

            // 判断是否为AJAX请求
            String accept = request.getHeader("Accept");
            if (accept != null && accept.contains("application/json")) {
                sendJsonFileListResponse(response, files, page, totalPages);
            } else {
                // 页面渲染
                request.setAttribute("files", files);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.getRequestDispatcher("/public_files.jsp").forward(request, response);
                return;
            }
            return;
        } else if ("/files/private".equals(path)) {
            // 处理私有文件列表请求 - 需要用户登录
            if (currentUser != null) {
                handlePrivateFilesList(response, currentUser.getId(), page, currentUser);
            } else {
                sendJsonErrorResponse(response, 401, "请先登录");
            }
        }
    }
    
    /**
     * 处理公共文件列表请求
     */
    private void handlePublicFilesList(HttpServletResponse response, int page, User currentUser) throws IOException {
        // 获取总文件数和总页数
        int totalFiles = fileDao.getFileCount(true, null);
        int totalPages = (int) Math.ceil((double) totalFiles / PAGE_SIZE);
        
        // 获取当前页的文件列表
        List<FileInfo> files = fileDao.getPublicFiles(page, PAGE_SIZE);
        
        // 处理文件可下载状态
        processFileDownloadPermissions(files, currentUser);
        
        // 发送响应
        sendJsonFileListResponse(response, files, page, totalPages);
    }
    
    /**
     * 处理私有文件列表请求
     */
    private void handlePrivateFilesList(HttpServletResponse response, int userId, int page, User currentUser) throws IOException {
        // 获取总文件数和总页数
        int totalFiles = fileDao.getFileCount(false, userId);
        int totalPages = (int) Math.ceil((double) totalFiles / PAGE_SIZE);
        
        // 获取当前页的文件列表
        List<FileInfo> files = fileDao.getUserPrivateFiles(userId, page, PAGE_SIZE);
        
        // 处理文件可下载状态
        processFileDownloadPermissions(files, currentUser);
        
        // 发送响应
        sendJsonFileListResponse(response, files, page, totalPages);
    }
    
    /**
     * 处理文件的下载权限
     */
    private void processFileDownloadPermissions(List<FileInfo> files, User currentUser) {
        for (FileInfo file : files) {
            boolean canDownload = false;
            
            // 已被封禁的文件不能下载
            if (file.isBlocked()) {
                file.setCanDownload(false);
                continue;
            }
            
            // 需要登录才能下载任何文件
            if (currentUser != null) {
                // 公共文件任何登录用户都可下载
                if (file.isPublic()) {
                    canDownload = true;
                } 
                // 私有文件只有创建者和管理员可下载
                else {
                    boolean isOwner = currentUser.getId() == file.getUserId();
                    boolean isAdmin = currentUser.isAdmin();
                    canDownload = isOwner || isAdmin;
                }
            } else {
                // 游客不能下载任何文件
                canDownload = false;
            }
            
            // 设置canDownload属性
            file.setCanDownload(canDownload);
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