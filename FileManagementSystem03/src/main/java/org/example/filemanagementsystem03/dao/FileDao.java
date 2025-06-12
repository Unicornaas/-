package org.example.filemanagementsystem03.dao;

import org.example.filemanagementsystem03.model.FileInfo;
import org.example.filemanagementsystem03.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件数据访问对象
 */
public class FileDao {
    
    /**
     * 保存文件信息到数据库
     */
    public boolean saveFile(FileInfo fileInfo) {
        String sql = "INSERT INTO files (file_name, file_path, file_size, file_type, user_id, is_public, download_count, is_blocked, upload_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, fileInfo.getFileName());
            pstmt.setString(2, fileInfo.getFilePath());
            pstmt.setLong(3, fileInfo.getFileSize());
            pstmt.setString(4, fileInfo.getFileType());
            pstmt.setInt(5, fileInfo.getUserId());
            pstmt.setBoolean(6, fileInfo.isPublic());
            pstmt.setInt(7, fileInfo.getDownloadCount());
            pstmt.setBoolean(8, fileInfo.isBlocked());
            pstmt.setTimestamp(9, new Timestamp(fileInfo.getUploadTime().getTime()));
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    fileInfo.setId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
            
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                DatabaseUtil.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 获取公共文件列表
     */
    public List<FileInfo> getPublicFiles(int page, int pageSize) {
        List<FileInfo> files = new ArrayList<>();
        String sql = "SELECT f.*, u.username FROM files f JOIN users u ON f.user_id = u.id " +
                     "WHERE f.is_public = true AND f.is_blocked = false ORDER BY f.upload_time DESC LIMIT ?, ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (page - 1) * pageSize);
            pstmt.setInt(2, pageSize);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                files.add(mapFileFromResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DatabaseUtil.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return files;
    }
    
    /**
     * 获取指定用户的私有文件列表
     */
    public List<FileInfo> getUserPrivateFiles(int userId, int page, int pageSize) {
        List<FileInfo> files = new ArrayList<>();
        String sql = "SELECT f.*, u.username FROM files f JOIN users u ON f.user_id = u.id " +
                     "WHERE f.user_id = ? AND f.is_public = false AND f.is_blocked = false " +
                     "ORDER BY f.upload_time DESC LIMIT ?, ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, (page - 1) * pageSize);
            pstmt.setInt(3, pageSize);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                files.add(mapFileFromResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DatabaseUtil.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return files;
    }
    
    /**
     * 获取管理员可见的所有文件列表（包括被封禁的文件）
     */
    public List<FileInfo> getAllFilesForAdmin(int page, int pageSize) {
        List<FileInfo> files = new ArrayList<>();
        String sql = "SELECT f.*, u.username FROM files f JOIN users u ON f.user_id = u.id " +
                     "ORDER BY f.upload_time DESC LIMIT ?, ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (page - 1) * pageSize);
            pstmt.setInt(2, pageSize);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                files.add(mapFileFromResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DatabaseUtil.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return files;
    }
    
    /**
     * 获取文件总数（用于分页）
     */
    public int getFileCount(boolean isPublic, Integer userId) {
        String sql;
        if (userId != null) {
            sql = "SELECT COUNT(*) FROM files WHERE is_public = ? AND user_id = ? AND is_blocked = false";
        } else {
            sql = "SELECT COUNT(*) FROM files WHERE is_public = ? AND is_blocked = false";
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, isPublic);
            
            if (userId != null) {
                pstmt.setInt(2, userId);
            }
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DatabaseUtil.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return 0;
    }
    
    /**
     * 获取所有文件的总数（管理员用）
     */
    public int getAllFilesCount() {
        String sql = "SELECT COUNT(*) FROM files";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DatabaseUtil.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return 0;
    }
    
    /**
     * 通过ID获取文件信息
     */
    public FileInfo getFileById(int fileId) {
        String sql = "SELECT f.*, u.username FROM files f JOIN users u ON f.user_id = u.id WHERE f.id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fileId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapFileFromResultSet(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DatabaseUtil.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     * 更新文件下载次数
     */
    public boolean updateDownloadCount(int fileId) {
        String sql = "UPDATE files SET download_count = download_count + 1 WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fileId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                DatabaseUtil.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 更新文件封禁状态
     */
    public boolean updateBlockStatus(int fileId, boolean isBlocked) {
        Connection conn = null;
        boolean success = false;
        try {
            conn = DatabaseUtil.getConnection();
            // Call the overloaded method with the obtained connection
            success = updateBlockStatus(conn, fileId, isBlocked);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            // Close the connection only if it was created in this method
            DatabaseUtil.closeConnection(conn); 
        }
        return success;
    }

    /**
     * 更新文件封禁状态 (使用现有数据库连接)
     */
    public boolean updateBlockStatus(Connection conn, int fileId, boolean isBlocked) {
        String sql = "UPDATE files SET is_blocked = ? WHERE id = ?";
        PreparedStatement pstmt = null;
        boolean success = false;
        boolean needsConnectionManagement = false; // Flag to track if connection management needed here
        
        try {
            // If conn is null, get a new connection (should ideally not happen if called correctly)
            if (conn == null) {
                conn = DatabaseUtil.getConnection();
                needsConnectionManagement = true; 
                if (conn == null) { // Check if connection failed
                     System.err.println("获取数据库连接失败 in updateBlockStatus");
                     return false;
                }
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, isBlocked);
            pstmt.setInt(2, fileId);

            int rows = pstmt.executeUpdate();
            success = (rows > 0);
            
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                // Only close the connection if it was created within this specific method call
                if (needsConnectionManagement) {
                    DatabaseUtil.closeConnection(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
    
    /**
     * 从ResultSet映射到FileInfo对象
     */
    private FileInfo mapFileFromResultSet(ResultSet rs) throws SQLException {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(rs.getInt("id"));
        fileInfo.setFileName(rs.getString("file_name"));
        fileInfo.setFilePath(rs.getString("file_path"));
        fileInfo.setFileSize(rs.getLong("file_size"));
        fileInfo.setFileType(rs.getString("file_type"));
        fileInfo.setUserId(rs.getInt("user_id"));
        fileInfo.setUsername(rs.getString("username"));
        fileInfo.setPublic(rs.getBoolean("is_public"));
        fileInfo.setDownloadCount(rs.getInt("download_count"));
        fileInfo.setBlocked(rs.getBoolean("is_blocked"));
        fileInfo.setUploadTime(rs.getTimestamp("upload_time"));
        return fileInfo;
    }
} 