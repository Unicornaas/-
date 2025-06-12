package org.example.filemanagementsystem03.model;

import java.util.Date;

/**
 * 文件举报实体类
 */
public class Report {
    private int id;             // 举报ID
    private int fileId;         // 被举报的文件ID
    private int reporterId;     // 举报者ID
    private String reporterName; // 举报者用户名
    private String reason;      // 举报原因
    private Date reportTime;    // 举报时间
    private int status;         // 举报状态(0: 待处理, 1: 已处理-封禁, 2: 已处理-忽略)
    private String fileName;    // 被举报的文件名(冗余字段，方便显示)
    private Integer handlerId;  // 处理人ID(可为空)
    private String handlerName; // 处理人用户名(可为空)
    private Date handleTime;    // 处理时间(可为空)
    private String handleNote;  // 处理备注(可为空)

    // 无参构造
    public Report() {
    }

    // 带参构造
    public Report(int fileId, int reporterId, String reason) {
        this.fileId = fileId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.reportTime = new Date();
        this.status = 0;  // 默认为待处理状态
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Integer handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public String getHandleNote() {
        return handleNote;
    }

    public void setHandleNote(String handleNote) {
        this.handleNote = handleNote;
    }
} 