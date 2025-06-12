package org.example.filemanagementsystem03.model;

import java.util.Date;

/**
 * 文件信息实体类
 */
public class FileInfo {
    private int id;            // 文件ID
    private String fileName;   // 文件名
    private String filePath;   // 文件保存路径
    private long fileSize;     // 文件大小(bytes)
    private String fileType;   // 文件类型
    private int userId;        // 上传用户ID
    private String username;   // 上传用户名
    private boolean isPublic;  // 是否公开 (true:公开文件，false:私有文件)
    private int downloadCount; // 下载次数
    private boolean isBlocked; // 是否被封禁 (true:已封禁，false:未封禁)
    private Date uploadTime;   // 上传时间
    private boolean canDownload; // 当前用户是否可以下载此文件

    // 无参构造函数
    public FileInfo() {
    }

    // 带参构造函数
    public FileInfo(String fileName, String filePath, long fileSize, String fileType, int userId, boolean isPublic) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.userId = userId;
        this.isPublic = isPublic;
        this.downloadCount = 0;
        this.isBlocked = false;
        this.uploadTime = new Date();
        this.canDownload = false; // 默认不可下载，由业务逻辑决定
    }
    
    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }
    
    public boolean isCanDownload() {
        return canDownload;
    }

    public void setCanDownload(boolean canDownload) {
        this.canDownload = canDownload;
    }
} 