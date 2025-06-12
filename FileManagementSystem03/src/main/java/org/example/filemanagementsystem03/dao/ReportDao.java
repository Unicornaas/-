package org.example.filemanagementsystem03.dao;

import org.example.filemanagementsystem03.model.Report;
import org.example.filemanagementsystem03.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 举报数据访问对象
 */
public class ReportDao {
    
    // Add FileDao instance
    private final FileDao fileDao = new FileDao(); 

    /**
     * 提交举报
     */
    public boolean submitReport(Report report) {
        String sql = "INSERT INTO reports (file_id, reporter_id, reason, report_time, status) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, report.getFileId());
            pstmt.setInt(2, report.getReporterId());
            pstmt.setString(3, report.getReason());
            pstmt.setTimestamp(4, new Timestamp(report.getReportTime().getTime()));
            pstmt.setInt(5, report.getStatus());
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    report.setId(rs.getInt(1));
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
     * 获取所有举报
     */
    public List<Report> getAllReports() {
        String sql = "SELECT r.*, " +
                     "COALESCE(f.file_name, '未知文件') as file_name, " +
                     "COALESCE(u1.username, '未知用户') as reporter_name, " +
                     "COALESCE(u2.username, '') as handler_name " +
                     "FROM reports r " +
                     "LEFT JOIN files f ON r.file_id = f.id " +
                     "LEFT JOIN users u1 ON r.reporter_id = u1.id " +
                     "LEFT JOIN users u2 ON r.handler_id = u2.id " +
                     "ORDER BY r.report_time DESC";
        
        return executeReportQuery(sql);
    }
    
    /**
     * 获取待处理的举报
     */
    public List<Report> getPendingReports() {
        String sql = "SELECT r.*, " +
                     "COALESCE(f.file_name, '未知文件') as file_name, " +
                     "COALESCE(u1.username, '未知用户') as reporter_name, " +
                     "COALESCE(u2.username, '') as handler_name " +
                     "FROM reports r " +
                     "LEFT JOIN files f ON r.file_id = f.id " +
                     "LEFT JOIN users u1 ON r.reporter_id = u1.id " +
                     "LEFT JOIN users u2 ON r.handler_id = u2.id " +
                     "WHERE r.status = 0 " +
                     "ORDER BY r.report_time DESC";
        
        return executeReportQuery(sql);
    }
    
    /**
     * 处理举报
     */
    public boolean handleReport(int reportId, int status, int handlerId, String handleNote) {
        Connection conn = null;
        PreparedStatement pstmtUpdateReport = null;
        PreparedStatement pstmtGetFileId = null; // To get file_id
        ResultSet rs = null;
        boolean success = false;

        String getFileIdSql = "SELECT file_id FROM reports WHERE id = ?";
        String updateReportSql = "UPDATE reports SET status = ?, handler_id = ?, handle_time = ?, handle_note = ? WHERE id = ?";

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Get file_id from the report
            int fileId = -1;
            pstmtGetFileId = conn.prepareStatement(getFileIdSql);
            pstmtGetFileId.setInt(1, reportId);
            rs = pstmtGetFileId.executeQuery();
            if (rs.next()) {
                fileId = rs.getInt("file_id");
            } else {
                System.err.println("处理举报失败：未找到举报ID " + reportId);
                conn.rollback(); // Rollback if report not found
                return false;
            }
            rs.close();
            pstmtGetFileId.close();

            if (fileId == -1) {
                 System.err.println("处理举报失败：无法获取举报关联的文件ID " + reportId);
                 conn.rollback();
                 return false;
            }


            // 2. Update the report status
            pstmtUpdateReport = conn.prepareStatement(updateReportSql);
            pstmtUpdateReport.setInt(1, status);
            pstmtUpdateReport.setInt(2, handlerId);
            pstmtUpdateReport.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmtUpdateReport.setString(4, handleNote);
            pstmtUpdateReport.setInt(5, reportId);

            int reportRows = pstmtUpdateReport.executeUpdate();

            // 3. If status is 1 (approved/ban), update file block status
            boolean fileBlockUpdated = true; // Assume success if not needed
            if (status == 1 && reportRows > 0) {
                // Use the injected FileDao instance and the overloaded method with the connection
                fileBlockUpdated = fileDao.updateBlockStatus(conn, fileId, true); 
                if (!fileBlockUpdated) {
                    System.err.println("处理举报时更新文件封禁状态失败：文件ID " + fileId);
                }
            }

            // 4. Commit or rollback based on success of both operations
            if (reportRows > 0 && fileBlockUpdated) {
                conn.commit(); // Commit transaction
                success = true;
            } else {
                System.err.println("处理举报失败，回滚事务：举报更新行数=" + reportRows + ", 文件封禁更新成功=" + fileBlockUpdated);
                conn.rollback(); // Rollback transaction
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException se) {
                se.printStackTrace();
            }
            success = false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtGetFileId != null) pstmtGetFileId.close(); // Close this PreparedStatement too
                if (pstmtUpdateReport != null) pstmtUpdateReport.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore default auto-commit behavior
                    DatabaseUtil.closeConnection(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
    
    /**
     * 获取举报详情
     */
    public Report getReportById(int reportId) {
        String sql = "SELECT r.*, " +
                     "COALESCE(f.file_name, '未知文件') as file_name, " +
                     "COALESCE(u1.username, '未知用户') as reporter_name, " +
                     "COALESCE(u2.username, '') as handler_name " +
                     "FROM reports r " +
                     "LEFT JOIN files f ON r.file_id = f.id " +
                     "LEFT JOIN users u1 ON r.reporter_id = u1.id " +
                     "LEFT JOIN users u2 ON r.handler_id = u2.id " +
                     "WHERE r.id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reportId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractReport(rs);
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
     * 执行查询并提取举报列表
     */
    private List<Report> executeReportQuery(String sql) {
        List<Report> reports = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // 检查reports表是否存在
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "reports", null);
            if (!tables.next()) {
                System.err.println("警告: reports表不存在，请先执行SQL脚本创建表");
                return reports;
            }
            tables.close();
            
            pstmt = conn.prepareStatement(sql);
            
            System.out.println("执行SQL查询: " + sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                try {
                    Report report = extractReport(rs);
                    reports.add(report);
                    System.out.println("加载到举报: ID=" + report.getId() + ", 文件=" + report.getFileName() + ", 举报者=" + report.getReporterName());
                } catch (SQLException e) {
                    System.err.println("处理举报记录时出错: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("查询到 " + reports.size() + " 条举报记录");
        } catch (Exception e) {
            System.err.println("查询举报信息时发生错误: " + e.getMessage());
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
        
        return reports;
    }
    
    /**
     * 从结果集提取举报对象
     */
    private Report extractReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        try {
            report.setId(rs.getInt("id"));
            report.setFileId(rs.getInt("file_id"));
            report.setReporterId(rs.getInt("reporter_id"));
            report.setReason(rs.getString("reason"));
            report.setReportTime(rs.getTimestamp("report_time"));
            report.setStatus(rs.getInt("status"));
            report.setFileName(rs.getString("file_name"));
            report.setReporterName(rs.getString("reporter_name"));
            
            // 处理可空字段
            try {
                if (rs.getObject("handler_id") != null) {
                    report.setHandlerId(rs.getInt("handler_id"));
                }
            } catch (SQLException e) {
                System.err.println("获取handler_id出错: " + e.getMessage());
            }
            
            try {
                report.setHandlerName(rs.getString("handler_name"));
            } catch (SQLException e) {
                System.err.println("获取handler_name出错: " + e.getMessage());
            }
            
            try {
                Timestamp handleTime = rs.getTimestamp("handle_time");
                if (handleTime != null) {
                    report.setHandleTime(handleTime);
                }
            } catch (SQLException e) {
                System.err.println("获取handle_time出错: " + e.getMessage());
            }
            
            try {
                report.setHandleNote(rs.getString("handle_note"));
            } catch (SQLException e) {
                System.err.println("获取handle_note出错: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("处理举报记录字段时出错: " + e.getMessage());
            throw e;
        }
        
        return report;
    }
    
    /**
     * 获取已处理的举报(状态为1或2)
     */
    public List<Report> getProcessedReports() {
        String sql = "SELECT r.*, " +
                     "COALESCE(f.file_name, '未知文件') as file_name, " +
                     "COALESCE(u1.username, '未知用户') as reporter_name, " +
                     "COALESCE(u2.username, '') as handler_name " +
                     "FROM reports r " +
                     "LEFT JOIN files f ON r.file_id = f.id " +
                     "LEFT JOIN users u1 ON r.reporter_id = u1.id " +
                     "LEFT JOIN users u2 ON r.handler_id = u2.id " +
                     "WHERE r.status > 0 " +  
                     "ORDER BY r.report_time DESC";
        
        return executeReportQuery(sql);
    }
    
    /**
     * 按状态获取举报
     * @param status 举报状态(0: 待处理, 1: 已批准, 2: 已驳回)
     */
    public List<Report> getReportsByStatus(int status) {
        String sql = "SELECT r.*, " +
                     "COALESCE(f.file_name, '未知文件') as file_name, " +
                     "COALESCE(u1.username, '未知用户') as reporter_name, " +
                     "COALESCE(u2.username, '') as handler_name " +
                     "FROM reports r " +
                     "LEFT JOIN files f ON r.file_id = f.id " +
                     "LEFT JOIN users u1 ON r.reporter_id = u1.id " +
                     "LEFT JOIN users u2 ON r.handler_id = u2.id " +
                     "WHERE r.status = ? " +
                     "ORDER BY r.report_time DESC";
        
        List<Report> reports = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, status);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reports.add(extractReport(rs));
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
        
        return reports;
    }
} 