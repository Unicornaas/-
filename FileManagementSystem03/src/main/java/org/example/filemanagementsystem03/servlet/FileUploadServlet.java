package org.example.filemanagementsystem03.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.filemanagementsystem03.model.User;
import org.example.filemanagementsystem03.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传Servlet
 */
@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,   // 1MB
        maxFileSize = 1024 * 1024 * 10,    // 10MB
        maxRequestSize = 1024 * 1024 * 50  // 50MB
)
public class FileUploadServlet extends HttpServlet {

    private FileService fileService = new FileService();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查是否是已登录用户
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            sendJsonResponse(response, 401, "请先登录后再上传文件");
            return;
        }
        
        try {
            boolean isPublic = false;
            String fileName = "";
            long fileSize = 0;
            
            // 使用Jakarta Servlet API的Part接口处理文件上传
            // 获取是否公开的参数
            String isPublicParam = request.getParameter("isPublic");
            if (isPublicParam != null && "true".equals(isPublicParam)) {
                isPublic = true;
            }
            
            // 获取上传的文件部分
            Part filePart = request.getPart("file");
            if (filePart != null) {
                // 获取文件名
                fileName = getSubmittedFileName(filePart);
                fileSize = filePart.getSize();
                
                // 通过FileService上传文件
                try (InputStream inputStream = filePart.getInputStream()) {
                    boolean saved = fileService.uploadFile(fileName, inputStream, fileSize, isPublic, currentUser.getId());
                    
                    if (saved) {
                        sendJsonResponse(response, 200, "文件上传成功");
                    } else {
                        sendJsonResponse(response, 500, "文件信息保存失败");
                    }
                }
            } else {
                sendJsonResponse(response, 400, "没有接收到文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, 500, "文件上传过程中出现错误: " + e.getMessage());
        }
    }
    
    /**
     * 从Part中获取提交的文件名
     */
    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                String fileName = item.substring(item.indexOf('=') + 1).trim();
                // 移除引号（如果有）
                if (fileName.startsWith("\"") && fileName.endsWith("\"")) {
                    fileName = fileName.substring(1, fileName.length() - 1);
                }
                // 从文件路径中获取文件名
                if (fileName.contains("\\")) {
                    return fileName.substring(fileName.lastIndexOf('\\') + 1);
                } else if (fileName.contains("/")) {
                    return fileName.substring(fileName.lastIndexOf('/') + 1);
                } else {
                    return fileName;
                }
            }
        }
        return "未知文件";
    }
    
    /**
     * 发送JSON响应
     */
    private void sendJsonResponse(HttpServletResponse response, int status, String message) throws IOException {
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