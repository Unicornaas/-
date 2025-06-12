package org.example.filemanagementsystem03.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.filemanagementsystem03.model.FileInfo;
import org.example.filemanagementsystem03.model.User;
import org.example.filemanagementsystem03.service.FileService;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 处理文件下载的Servlet
 */
@WebServlet("/download")
public class FileDownloadServlet extends HttpServlet {
    private FileService fileService = new FileService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        // 检查用户是否登录
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请先登录后再下载文件");
            return;
        }
        
        // 获取请求的文件ID
        String fileIdParam = request.getParameter("id");
        if (fileIdParam == null || fileIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少文件ID参数");
            return;
        }
        
        try {
            int fileId = Integer.parseInt(fileIdParam);
            
            // 使用FileService获取文件信息和验证权限
            FileInfo fileInfo = fileService.getFileForDownload(fileId, currentUser);
            
            if (fileInfo == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
                return;
            }
            
            // 检查文件是否被封禁
            if (fileInfo.isBlocked()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "此文件已被管理员封禁，不可下载");
                return;
            }
            
            // 获取文件的物理路径
            String actualFilePath = fileService.getPhysicalFilePath(fileInfo.getFilePath());
            
            // 检查文件是否存在
            File file = new File(actualFilePath);
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件已被删除或移动");
                return;
            }
            
            // 设置响应头
            String fileName = fileInfo.getFileName();
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            
            // 设置适合下载的Content-Type
            String contentType = getServletContext().getMimeType(fileName);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            
            // 使用FileService发送文件到客户端
            fileService.sendFileToOutputStream(file, response.getOutputStream());
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的文件ID");
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "下载文件时发生错误: " + e.getMessage());
        }
    }
} 