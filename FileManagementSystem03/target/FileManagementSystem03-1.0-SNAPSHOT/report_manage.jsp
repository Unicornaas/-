<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.filemanagementsystem03.model.User" %>
<%
    // 获取会话中的用户信息
    User user = (User) session.getAttribute("user");
    // 检查用户是否有管理员权限
    if (user == null || !user.isAdmin()) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>举报管理 - 文件管理系统</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            padding-top: 20px;
            background-color: #f5f5f5;
        }
        .card {
            margin-bottom: 20px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .table th, .table td {
            vertical-align: middle;
        }
        .badge {
            font-size: 0.9em;
        }
        .action-buttons .btn {
            margin-right: 5px;
        }
        .modal-body {
            max-height: 400px;
            overflow-y: auto;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">
                            <i class="fas fa-flag"></i> 举报管理
                            <a href="index.jsp" class="btn btn-sm btn-light float-end">
                                <i class="fas fa-home"></i> 返回首页
                            </a>
                        </h4>
                    </div>
                    <div class="card-body">
                        <ul class="nav nav-tabs" id="reportTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="pending-tab" data-bs-toggle="tab" data-bs-target="#pending" type="button" role="tab" aria-controls="pending" aria-selected="true">
                                    待处理举报 <span id="pendingCount" class="badge bg-danger">0</span>
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="processed-tab" data-bs-toggle="tab" data-bs-target="#processed" type="button" role="tab" aria-controls="processed" aria-selected="false">
                                    已处理举报
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="all-tab" data-bs-toggle="tab" data-bs-target="#all" type="button" role="tab" aria-controls="all" aria-selected="false">
                                    所有举报
                                </button>
                            </li>
                        </ul>
                        <div class="tab-content mt-3" id="reportTabsContent">
                            <div class="tab-pane fade show active" id="pending" role="tabpanel" aria-labelledby="pending-tab">
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover" id="pendingReportsTable">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>ID</th>
                                                <th>被举报文件</th>
                                                <th>举报人</th>
                                                <th>举报原因</th>
                                                <th>举报时间</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody id="pendingReportsList">
                                            <tr>
                                                <td colspan="6" class="text-center">正在加载数据...</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="processed" role="tabpanel" aria-labelledby="processed-tab">
                                <div class="d-flex justify-content-end mb-3">
                                    <div class="btn-group" role="group">
                                        <button type="button" class="btn btn-outline-success btn-sm" id="btnApproved">已批准</button>
                                        <button type="button" class="btn btn-outline-danger btn-sm" id="btnRejected">已驳回</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm active" id="btnAllProcessed">全部</button>
                                    </div>
                                </div>
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover" id="processedReportsTable">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>ID</th>
                                                <th>被举报文件</th>
                                                <th>举报人</th>
                                                <th>状态</th>
                                                <th>举报时间</th>
                                                <th>处理人</th>
                                                <th>处理时间</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody id="processedReportsList">
                                            <tr>
                                                <td colspan="8" class="text-center">正在加载数据...</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="all" role="tabpanel" aria-labelledby="all-tab">
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover" id="allReportsTable">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>ID</th>
                                                <th>被举报文件</th>
                                                <th>举报人</th>
                                                <th>状态</th>
                                                <th>举报时间</th>
                                                <th>处理人</th>
                                                <th>处理时间</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody id="allReportsList">
                                            <tr>
                                                <td colspan="8" class="text-center">正在加载数据...</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 举报详情模态框 -->
    <div class="modal fade" id="reportDetailModal" tabindex="-1" aria-labelledby="reportDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title" id="reportDetailModalLabel">举报详情</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" id="reportDetailContent">
                    <div class="text-center">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 处理举报模态框 -->
    <div class="modal fade" id="handleReportModal" tabindex="-1" aria-labelledby="handleReportModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title" id="handleReportModalLabel">处理举报</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="handleReportForm">
                        <input type="hidden" id="reportId" name="reportId">
                        <div class="mb-3">
                            <label class="form-label">文件名称:</label>
                            <div id="reportFileName" class="form-control-plaintext"></div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">举报原因:</label>
                            <div id="reportReason" class="form-control-plaintext"></div>
                        </div>
                        <div class="mb-3">
                            <label for="handleNote" class="form-label">处理备注:</label>
                            <textarea class="form-control" id="handleNote" name="handleNote" rows="3" placeholder="请输入处理备注"></textarea>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">处理结果:</label>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="status" id="statusApprove" value="1" checked>
                                <label class="form-check-label" for="statusApprove">
                                    批准 (确认违规，封禁文件)
                                </label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="status" id="statusReject" value="2">
                                <label class="form-check-label" for="statusReject">
                                    驳回 (未违规，忽略举报)
                                </label>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="submitHandleReport">提交</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
    <script>
        // 当文档加载完成后执行
        $(document).ready(function() {
            console.log("举报管理页面初始化...");
            
            // 添加全局Ajax错误处理
            $(document).ajaxError(function(event, jqxhr, settings, thrownError) {
                console.error('Ajax请求失败:', settings.url, thrownError);
                console.log('状态码:', jqxhr.status);
                console.log('响应文本:', jqxhr.responseText);
            });
            
            // 加载待处理举报
            loadPendingReports();
            
            // 切换到"已处理举报"标签页时加载已处理举报
            $('#processed-tab').on('shown.bs.tab', function (e) {
                loadProcessedReports();
            });
            
            // 切换到"所有举报"标签页时加载所有举报
            $('#all-tab').on('shown.bs.tab', function (e) {
                loadAllReports();
            });
            
            // 已处理举报筛选按钮事件
            $('#btnApproved').click(function() {
                $(this).addClass('active').siblings().removeClass('active');
                loadProcessedReports(1); // 1表示已批准
            });
            
            $('#btnRejected').click(function() {
                $(this).addClass('active').siblings().removeClass('active');
                loadProcessedReports(2); // 2表示已驳回
            });
            
            $('#btnAllProcessed').click(function() {
                $(this).addClass('active').siblings().removeClass('active');
                loadProcessedReports(); // 不传参数表示所有已处理
            });
            
            // 处理举报表单提交
            $('#submitHandleReport').click(function() {
                handleReport();
            });
        });
        
        // 加载待处理举报
        function loadPendingReports() {
            console.log("正在加载待处理举报...");
            $.ajax({
                url: 'report/manage',
                type: 'GET',
                dataType: 'json',
                data: {
                    action: 'list',
                    pending: true
                },
                success: function(response) {
                    console.log("待处理举报加载结果:", response);
                    if (response.success) {
                        renderPendingReports(response.reports);
                    } else {
                        $('#pendingReportsList').html('<tr><td colspan="6" class="text-center">加载失败: ' + response.message + '</td></tr>');
                        console.error('加载待处理举报失败:', response.message);
                    }
                },
                error: function(xhr, status, error) {
                    $('#pendingReportsList').html('<tr><td colspan="6" class="text-center">服务器错误，请稍后重试</td></tr>');
                    console.error('加载待处理举报异常:', status, error);
                    console.error('响应文本:', xhr.responseText);
                    try {
                        var resp = JSON.parse(xhr.responseText);
                        console.error('解析后的错误响应:', resp);
                    } catch(e) {
                        console.error('响应不是有效JSON');
                    }
                }
            });
        }
        
        // 加载已处理举报
        function loadProcessedReports(statusFilter) {
            var url = 'report/manage';
            var data = {
                action: 'list',
                processed: true
            };
            
            // 如果指定了状态筛选
            if (statusFilter) {
                data.status = statusFilter;
            }
            
            $('#processedReportsList').html('<tr><td colspan="8" class="text-center">正在加载数据...</td></tr>');
            
            $.ajax({
                url: url,
                type: 'GET',
                dataType: 'json',
                data: data,
                success: function(response) {
                    console.log("已处理举报加载结果:", response);
                    if (response.success) {
                        renderProcessedReports(response.reports);
                    } else {
                        $('#processedReportsList').html('<tr><td colspan="8" class="text-center">加载失败: ' + response.message + '</td></tr>');
                        console.error('加载已处理举报失败:', response.message);
                    }
                },
                error: function(xhr, status, error) {
                    $('#processedReportsList').html('<tr><td colspan="8" class="text-center">服务器错误，请稍后重试</td></tr>');
                    console.error('加载已处理举报异常:', status, error, xhr.responseText);
                }
            });
        }
        
        // 加载所有举报
        function loadAllReports() {
            console.log("正在加载所有举报...");
            $.ajax({
                url: 'report/manage',
                type: 'GET',
                dataType: 'json',
                data: {
                    action: 'list'
                },
                success: function(response) {
                    console.log("所有举报加载结果:", response);
                    if (response.success) {
                        renderAllReports(response.reports);
                    } else {
                        $('#allReportsList').html('<tr><td colspan="8" class="text-center">加载失败: ' + response.message + '</td></tr>');
                        console.error('加载所有举报失败:', response.message);
                    }
                },
                error: function(xhr, status, error) {
                    $('#allReportsList').html('<tr><td colspan="8" class="text-center">服务器错误，请稍后重试</td></tr>');
                    console.error('加载所有举报异常:', status, error, xhr.responseText);
                }
            });
        }
        
        // 渲染待处理举报列表
        function renderPendingReports(reports) {
            var html = '';
            
            if (!reports || reports.length === 0) {
                html = '<tr><td colspan="6" class="text-center">暂无待处理举报</td></tr>';
                $('#pendingCount').text('0');
            } else {
                $('#pendingCount').text(reports.length);
                
                for (var i = 0; i < reports.length; i++) {
                    var report = reports[i];
                    html += '<tr>';
                    html += '<td>' + (report.id || '') + '</td>';
                    html += '<td>' + (report.fileName || '未知文件') + '</td>';
                    html += '<td>' + (report.reporterName || '未知用户') + '</td>';
                    html += '<td>' + (report.reason ? (report.reason.length > 30 ? report.reason.substring(0, 30) + '...' : report.reason) : '') + '</td>';
                    html += '<td>' + (report.reportTime ? formatDate(report.reportTime) : '') + '</td>';
                    html += '<td class="action-buttons">';
                    html += '<button class="btn btn-sm btn-info" onclick="viewReportDetail(' + report.id + ')"><i class="fas fa-eye"></i> 查看</button>';
                    html += '<button class="btn btn-sm btn-primary" onclick="showHandleDialog(' + report.id + ', \'' + (report.fileName ? report.fileName.replace(/'/g, "\\'") : '未知文件') + '\', \'' + (report.reason ? report.reason.replace(/'/g, "\\'") : '') + '\')"><i class="fas fa-gavel"></i> 处理</button>';
                    html += '</td>';
                    html += '</tr>';
                }
            }
            
            $('#pendingReportsList').html(html);
        }
        
        // 渲染已处理举报列表
        function renderProcessedReports(reports) {
            var html = '';
            
            if (!reports || reports.length === 0) {
                html = '<tr><td colspan="8" class="text-center">暂无已处理举报</td></tr>';
            } else {
                for (var i = 0; i < reports.length; i++) {
                    var report = reports[i];
                    html += '<tr>';
                    html += '<td>' + (report.id || '') + '</td>';
                    html += '<td>' + (report.fileName || '未知文件') + '</td>';
                    html += '<td>' + (report.reporterName || '未知用户') + '</td>';
                    html += '<td>' + formatStatus(report.status) + '</td>';
                    html += '<td>' + (report.reportTime ? formatDate(report.reportTime) : '') + '</td>';
                    html += '<td>' + (report.handlerName || '-') + '</td>';
                    html += '<td>' + (report.handleTime ? formatDate(report.handleTime) : '-') + '</td>';
                    html += '<td><button class="btn btn-sm btn-info" onclick="viewReportDetail(' + report.id + ')"><i class="fas fa-eye"></i> 查看</button></td>';
                    html += '</tr>';
                }
            }
            
            $('#processedReportsList').html(html);
        }
        
        // 渲染所有举报列表
        function renderAllReports(reports) {
            var html = '';
            
            if (!reports || reports.length === 0) {
                html = '<tr><td colspan="8" class="text-center">暂无举报记录</td></tr>';
            } else {
                for (var i = 0; i < reports.length; i++) {
                    var report = reports[i];
                    html += '<tr>';
                    html += '<td>' + (report.id || '') + '</td>';
                    html += '<td>' + (report.fileName || '未知文件') + '</td>';
                    html += '<td>' + (report.reporterName || '未知用户') + '</td>';
                    html += '<td>' + formatStatus(report.status) + '</td>';
                    html += '<td>' + (report.reportTime ? formatDate(report.reportTime) : '') + '</td>';
                    html += '<td>' + (report.handlerName || '-') + '</td>';
                    html += '<td>' + (report.handleTime ? formatDate(report.handleTime) : '-') + '</td>';
                    html += '<td><button class="btn btn-sm btn-info" onclick="viewReportDetail(' + report.id + ')"><i class="fas fa-eye"></i> 查看</button></td>';
                    html += '</tr>';
                }
            }
            
            $('#allReportsList').html(html);
        }
        
        // 查看举报详情
        function viewReportDetail(reportId) {
            $('#reportDetailModal').modal('show');
            $('#reportDetailContent').html('<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></div>');
            
            $.ajax({
                url: 'report/manage',
                type: 'GET',
                dataType: 'json',
                data: {
                    action: 'detail',
                    id: reportId
                },
                success: function(response) {
                    if (response.success) {
                        renderReportDetail(response.report);
                    } else {
                        $('#reportDetailContent').html('<div class="alert alert-danger">' + response.message + '</div>');
                    }
                },
                error: function() {
                    $('#reportDetailContent').html('<div class="alert alert-danger">服务器错误，请稍后重试</div>');
                }
            });
        }
        
        // 显示处理举报对话框
        function showHandleDialog(reportId, fileName, reason) {
            $('#reportId').val(reportId);
            $('#reportFileName').text(fileName);
            $('#reportReason').text(reason);
            $('#handleNote').val('');
            $('#statusApprove').prop('checked', true);
            $('#handleReportModal').modal('show');
        }
        
        // 处理举报
        function handleReport() {
            var reportId = $('#reportId').val();
            var status = $('input[name="status"]:checked').val();
            var note = $('#handleNote').val();
            
            $.ajax({
                url: 'report/manage',
                type: 'POST',
                dataType: 'json',
                data: {
                    action: 'handle',
                    id: reportId,
                    status: status,
                    note: note
                },
                success: function(response) {
                    if (response.success) {
                        $('#handleReportModal').modal('hide');
                        loadPendingReports();
                        loadAllReports();
                        alert('举报处理成功');
                    } else {
                        alert('举报处理失败: ' + response.message);
                    }
                },
                error: function() {
                    alert('服务器错误，请稍后重试');
                }
            });
        }
        
        // 渲染举报详情
        function renderReportDetail(report) {
            if (!report) {
                $('#reportDetailContent').html('<div class="alert alert-danger">无法加载举报详情</div>');
                return;
            }
            
            var html = '<div class="card mb-3">';
            html += '<div class="card-header"><strong>举报基本信息</strong></div>';
            html += '<div class="card-body">';
            html += '<div class="row mb-2"><div class="col-md-3 fw-bold">举报ID:</div><div class="col-md-9">' + (report.id || '') + '</div></div>';
            html += '<div class="row mb-2"><div class="col-md-3 fw-bold">被举报文件:</div><div class="col-md-9">' + (report.fileName || '未知文件') + '</div></div>';
            html += '<div class="row mb-2"><div class="col-md-3 fw-bold">举报人:</div><div class="col-md-9">' + (report.reporterName || '未知用户') + '</div></div>';
            html += '<div class="row mb-2"><div class="col-md-3 fw-bold">举报时间:</div><div class="col-md-9">' + (report.reportTime ? formatDate(report.reportTime) : '') + '</div></div>';
            html += '<div class="row mb-2"><div class="col-md-3 fw-bold">举报状态:</div><div class="col-md-9">' + formatStatus(report.status) + '</div></div>';
            html += '</div></div>';
            
            html += '<div class="card mb-3">';
            html += '<div class="card-header"><strong>举报原因</strong></div>';
            html += '<div class="card-body"><p>' + (report.reason || '无') + '</p></div>';
            html += '</div>';
            
            if (report.status !== 0) {
                html += '<div class="card">';
                html += '<div class="card-header"><strong>处理信息</strong></div>';
                html += '<div class="card-body">';
                html += '<div class="row mb-2"><div class="col-md-3 fw-bold">处理人:</div><div class="col-md-9">' + (report.handlerName || '-') + '</div></div>';
                html += '<div class="row mb-2"><div class="col-md-3 fw-bold">处理时间:</div><div class="col-md-9">' + (report.handleTime ? formatDate(report.handleTime) : '-') + '</div></div>';
                html += '<div class="row mb-2"><div class="col-md-3 fw-bold">处理备注:</div><div class="col-md-9">' + (report.handleNote || '无') + '</div></div>';
                html += '</div></div>';
            } else if (report.status === 0) {
                html += '<div class="d-grid gap-2">';
                html += '<button class="btn btn-primary" onclick="showHandleDialog(' + report.id + ', \'' + (report.fileName ? report.fileName.replace(/'/g, "\\'") : '未知文件') + '\', \'' + (report.reason ? report.reason.replace(/'/g, "\\'") : '') + '\')"><i class="fas fa-gavel"></i> 处理此举报</button>';
                html += '</div>';
            }
            
            $('#reportDetailContent').html(html);
        }
        
        // 格式化日期
        function formatDate(dateStr) {
            if (!dateStr) return '';
            try {
                var date = new Date(dateStr);
                return date.getFullYear() + '-' + 
                       padZero(date.getMonth() + 1) + '-' + 
                       padZero(date.getDate()) + ' ' + 
                       padZero(date.getHours()) + ':' + 
                       padZero(date.getMinutes()) + ':' + 
                       padZero(date.getSeconds());
            } catch (e) {
                console.error('日期格式化错误:', e);
                return '';
            }
        }
        
        // 数字前补零
        function padZero(num) {
            return num < 10 ? '0' + num : num;
        }
        
        // 格式化状态
        function formatStatus(status) {
            if (status === 0) {
                return '<span class="badge bg-warning">待处理</span>';
            } else if (status === 1) {
                return '<span class="badge bg-success">已批准封禁</span>';
            } else if (status === 2) {
                return '<span class="badge bg-danger">已驳回忽略</span>';
            } else {
                return '<span class="badge bg-secondary">未知</span>';
            }
        }
    </script>
</body>
</html> 