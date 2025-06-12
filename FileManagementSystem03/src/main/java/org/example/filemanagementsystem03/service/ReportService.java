package org.example.filemanagementsystem03.service;

import org.example.filemanagementsystem03.dao.ReportDao;
import org.example.filemanagementsystem03.model.Report;

import java.util.List;

/**
 * 举报服务类
 */
public class ReportService {
    private ReportDao reportDao;
    
    public ReportService() {
        this.reportDao = new ReportDao();
    }
    
    /**
     * 提交举报
     */
    public boolean submitReport(Report report) {
        return reportDao.submitReport(report);
    }
    
    /**
     * 获取所有举报
     */
    public List<Report> getAllReports() {
        return reportDao.getAllReports();
    }
    
    /**
     * 获取待处理的举报
     */
    public List<Report> getPendingReports() {
        return reportDao.getPendingReports();
    }
    
    /**
     * 处理举报
     * @param reportId 举报ID
     * @param status 处理状态(1: 批准-封禁, 2: 驳回-忽略)
     * @param handlerId 处理人ID
     * @param handleNote 处理备注
     */
    public boolean handleReport(int reportId, int status, int handlerId, String handleNote) {
        return reportDao.handleReport(reportId, status, handlerId, handleNote);
    }
    
    /**
     * 获取举报详情
     */
    public Report getReportById(int reportId) {
        return reportDao.getReportById(reportId);
    }
    
    /**
     * 获取已处理的举报(状态为1或2)
     */
    public List<Report> getProcessedReports() {
        return reportDao.getProcessedReports();
    }
    
    /**
     * 按状态获取举报
     * @param status 举报状态(0: 待处理, 1: 已批准, 2: 已驳回)
     */
    public List<Report> getReportsByStatus(int status) {
        return reportDao.getReportsByStatus(status);
    }
} 