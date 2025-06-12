package org.example.filemanagementsystem03.service;

import org.example.filemanagementsystem03.dao.FileDao;
import org.example.filemanagementsystem03.model.FileInfo;
import org.example.filemanagementsystem03.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件服务类，处理文件上传和下载的业务逻辑
 */
public class FileService {
    private static final String UPLOAD_DIRECTORY = "uploads";
    private static final String PHYSICAL_UPLOAD_PATH = "D:\\FileManagementSystem-files";
    private FileDao fileDao;

    public FileService() {
        this.fileDao = new FileDao();
    }

    /**
     * 上传文件并保存到数据库
     */
    public boolean uploadFile(String fileName, InputStream fileInputStream, long fileSize, boolean isPublic, int userId) throws Exception {
        // 创建目录（如果不存在）
        File uploadDir = new File(PHYSICAL_UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 获取文件扩展名
        String fileType = getFileExtension(fileName);
        
        // 生成唯一文件名避免冲突
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        String filePath = PHYSICAL_UPLOAD_PATH + File.separator + uniqueFileName;
        
        // 保存文件到磁盘
        Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        
        // 保存到数据库的相对路径
        String savedFilePath = UPLOAD_DIRECTORY + File.separator + uniqueFileName;
        
        // 保存文件信息到数据库
        FileInfo fileInfo = new FileInfo(fileName, savedFilePath, fileSize, fileType, userId, isPublic);
        return fileDao.saveFile(fileInfo);
    }

    /**
     * 获取文件用于下载，并检查权限
     */
    public FileInfo getFileForDownload(int fileId, User currentUser) throws Exception {
        FileInfo fileInfo = fileDao.getFileById(fileId);
        
        if (fileInfo == null) {
            return null;
        }
        
        // 检查文件是否被封禁
        if (fileInfo.isBlocked()) {
            throw new SecurityException("此文件已被管理员封禁，无法下载");
        }
        
        // 检查文件访问权限
        if (!fileInfo.isPublic()) {
            // 未登录用户不能下载私有文件
            if (currentUser == null) {
                throw new SecurityException("您需要登录后才能下载此文件");
            }
            
            // 检查是否是文件拥有者或管理员
            boolean isOwner = currentUser.getId() == fileInfo.getUserId();
            boolean isAdmin = currentUser.isAdmin();
            
            if (!isOwner && !isAdmin) {
                throw new SecurityException("您没有权限下载此文件");
            }
        } else {
            // 公共文件，但需要用户登录才能下载
            if (currentUser == null) {
                throw new SecurityException("您需要登录后才能下载此文件");
            }
        }
        
        // 更新下载次数
        fileDao.updateDownloadCount(fileId);
        
        return fileInfo;
    }

    /**
     * 封禁或解除封禁文件（仅管理员可操作）
     */
    public boolean toggleFileBlockStatus(int fileId, boolean blockStatus, User currentUser) throws Exception {
        // 检查是否是管理员
        if (currentUser == null || !currentUser.isAdmin()) {
            throw new SecurityException("只有管理员才能封禁/解封文件");
        }
        
        // 检查文件是否存在
        FileInfo fileInfo = fileDao.getFileById(fileId);
        if (fileInfo == null) {
            throw new Exception("文件不存在");
        }
        
        // 更新封禁状态
        return fileDao.updateBlockStatus(fileId, blockStatus);
    }

    /**
     * 获取文件的实际物理路径
     */
    public String getPhysicalFilePath(String relativePath) {
        String actualFilePath;
        if (relativePath.startsWith(UPLOAD_DIRECTORY)) {
            String relativePathWithoutUploadDir = relativePath.substring(UPLOAD_DIRECTORY.length());
            // 确保路径分隔符一致
            if (relativePathWithoutUploadDir.startsWith("/") || relativePathWithoutUploadDir.startsWith("\\")) {
                relativePathWithoutUploadDir = relativePathWithoutUploadDir.substring(1);
            }
            actualFilePath = PHYSICAL_UPLOAD_PATH + File.separator + relativePathWithoutUploadDir;
        } else {
            // 如果路径格式不符合预期，直接使用
            actualFilePath = PHYSICAL_UPLOAD_PATH + File.separator + relativePath;
        }
        return actualFilePath;
    }

    /**
     * 将文件发送到输出流
     */
    public void sendFileToOutputStream(File file, OutputStream outputStream) throws Exception {
        try (InputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
} 