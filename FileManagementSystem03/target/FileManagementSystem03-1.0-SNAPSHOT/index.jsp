<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>文件管理系统</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.bootcdn.net/ajax/libs/bootstrap/5.2.3/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%);
            background-attachment: fixed;
        }
        .main-container {
            max-width: 950px;
            margin: 0 auto;
            padding: 30px 10px 30px 10px;
        }
        .card {
            margin-bottom: 24px;
            border-radius: 18px;
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.13);
            background: rgba(255,255,255,0.88);
            backdrop-filter: blur(6px);
            border: none;
        }
        .card-header {
            border-radius: 18px 18px 0 0;
            padding: 1.1rem 1.5rem;
        }
        .top-navbar {
            background: linear-gradient(90deg, #74ebd5 0%, #ACB6E5 100%);
            padding: 14px 0 12px 0;
            color: white;
            margin-bottom: 28px;
            box-shadow: 0 4px 16px rgba(31,38,135,0.08);
        }
        .top-navbar h3 {
            font-weight: 700;
            letter-spacing: 0.08em;
            font-size: 2rem;
            margin-bottom: 0;
        }
        .user-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .user-avatar {
            width: 44px;
            height: 44px;
            border-radius: 50%;
            background: linear-gradient(135deg, #ACB6E5 0%, #74ebd5 100%);
            color: #0d6efd;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 20px;
            box-shadow: 0 2px 8px rgba(31,38,135,0.10);
        }
        .dropdown-menu {
            border-radius: 1rem;
            min-width: 160px;
        }
        .btn-outline-light, .btn-outline-success, .btn-outline-info, .btn-outline-warning, .btn-outline-danger, .btn-outline-secondary, .btn-primary, .btn-secondary {
            border-radius: 2rem !important;
            font-weight: 600;
            letter-spacing: 0.03em;
            transition: all 0.18s;
        }
        .btn-outline-light:hover {
            color: #0d6efd;
            background: #fff;
        }
        .admin-badge {
            background-color: #dc3545;
            color: white;
            padding: 2px 8px;
            border-radius: 10px;
            font-size: 0.8rem;
            margin-left: 7px;
        }
        .blocked-file {
            background-color: rgba(255, 0, 0, 0.08);
        }
        .table-responsive {
            border-radius: 10px;
            overflow: hidden;
        }
        .table {
            background: rgba(255,255,255,0.98);
        }
        .file-icon {
            font-size: 1.5rem;
            margin-right: 8px;
            vertical-align: middle;
        }
        .pagination {
            margin-top: 24px;
            margin-bottom: 0;
        }
        .pagination .page-link {
            border-radius: 1.5rem !important;
            margin: 0 2px;
            font-weight: 500;
        }
        .progress {
            border-radius: 1.2rem;
            height: 1.2rem;
        }
        .progress-bar {
            font-size: 0.95rem;
        }
        .alert {
            border-radius: 1.2rem;
            font-size: 1rem;
        }
        .modal-content {
            border-radius: 1.2rem;
        }
        .hidden {
            display: none !important;
        }
        @media (max-width: 768px) {
            .main-container { padding: 10px 2px; }
            .top-navbar h3 { font-size: 1.2rem; }
            .user-avatar { width: 36px; height: 36px; font-size: 15px; }
        }
    </style>
</head>
<body>
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
        <div class="container">
            <div class="d-flex justify-content-between align-items-center">
                <h3 class="m-0">文件管理系统</h3>
                <div id="userSection" class="user-info align-items-center" style="gap: 0;">
                    <!-- 用户未登录时显示 -->
                    <div id="guestSection">
                        <a href="login.jsp" class="btn btn-outline-light">
                            <i class="fas fa-sign-in-alt"></i> 登录/注册
                        </a>
                    </div>
                    
                    <!-- 用户已登录时显示 -->
                    <div id="userInfoSection" class="hidden d-flex align-items-center" style="gap: 0;">
                        <span id="welcomeUser" class="me-2">欢迎您,</span>
                        <div class="user-avatar me-2" id="userAvatar">U</div>
                        <div class="dropdown d-inline-block">
                            <button class="btn btn-outline-light dropdown-toggle d-flex align-items-center px-3 py-1" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false" style="height: 38px;">
                                <span id="loggedInUsername" class="fw-bold">用户</span>
                                <span id="adminBadge" class="admin-badge hidden ms-2">管理员</span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                                <li><a class="dropdown-item" href="#" id="btnMyAccount">
                                    <i class="fas fa-user-cog"></i> 我的账户
                                </a></li>
                                <li id="adminMenuSection" class="hidden"><a class="dropdown-item" href="#" id="btnAdminPanel">
                                    <i class="fas fa-shield-alt"></i> 管理面板
                                </a></li>
                                <li id="reportManageSection" class="hidden"><a class="dropdown-item" href="report_manage.jsp">
                                    <i class="fas fa-flag"></i> 举报管理
                                </a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="#" id="btnLogout">
                                    <i class="fas fa-sign-out-alt"></i> 退出登录
                                </a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <div class="container main-container">
        <!-- 功能导航卡片 -->
        <div class="row mb-4">
            <div class="col">
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-compass"></i> 功能导航
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-3 mb-3">
                                <button id="btnUpload" class="btn btn-outline-success w-100 hidden">
                                    <i class="fas fa-upload"></i> 上传文件
                                </button>
                            </div>
                            <div class="col-md-3 mb-3">
                                <button id="btnViewPublic" class="btn btn-outline-info w-100">
                                    <i class="fas fa-globe"></i> 公共文件
                                </button>
                            </div>
                            <div class="col-md-3 mb-3">
                                <button id="btnViewPrivate" class="btn btn-outline-warning w-100 hidden">
                                    <i class="fas fa-lock"></i> 我的私有文件
                                </button>
                            </div>
                            <div class="col-md-3 mb-3">
                                <button id="btnAdminFiles" class="btn btn-outline-danger w-100 hidden">
                                    <i class="fas fa-shield-alt"></i> 管理文件
                                </button>
                            </div>
                        </div>
                        <div class="row" id="adminActions">
                            <div class="col-md-3 mb-3">
                                <a href="report_manage.jsp" class="btn btn-outline-secondary w-100 hidden" id="btnReportManage">
                                    <i class="fas fa-flag"></i> 举报管理
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 文件上传表单 -->
        <div id="uploadForm" class="card hidden">
            <div class="card-header bg-success text-white">
                <h5 class="card-title mb-0">
                    <i class="fas fa-file-upload"></i> 上传文件
                </h5>
            </div>
            <div class="card-body">
                <form id="fileUploadForm" enctype="multipart/form-data">
                    <div class="mb-3">
                        <label for="file" class="form-label">选择文件:</label>
                        <input type="file" class="form-control" id="file" name="file" required>
                    </div>
                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" id="isPublic" name="isPublic" value="true">
                        <label class="form-check-label" for="isPublic">公开文件（所有登录用户可下载）</label>
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary" id="cancelUpload">
                            <i class="fas fa-times"></i> 取消
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-upload"></i> 上传
                        </button>
                    </div>
                </form>
                <div id="uploadProgress" class="progress mt-3 hidden">
                    <div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" style="width: 0%"></div>
                </div>
                <div id="uploadMessage" class="alert mt-3 hidden"></div>
            </div>
        </div>
        
        <!-- 文件列表展示区域 -->
        <div id="fileList" class="card">
            <div class="card-header bg-info text-white">
                <h5 class="card-title mb-0">
                    <i class="fas fa-folder-open"></i> <span id="fileListTitle">公共文件列表</span>
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>文件名</th>
                                <th>大小</th>
                                <th>类型</th>
                                <th>上传者</th>
                                <th>上传时间</th>
                                <th>下载次数</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody id="fileTableBody">
                            <tr>
                                <td colspan="7" class="text-center">加载中...</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <!-- 分页控件 -->
                <nav aria-label="文件列表分页">
                    <ul class="pagination justify-content-center" id="pagination">
                        <!-- 分页链接将通过JS动态生成 -->
                    </ul>
                </nav>
            </div>
        </div>
    </div>
    
    <!-- 举报模态框 -->
    <div class="modal fade" id="reportModal" tabindex="-1" aria-labelledby="reportModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title" id="reportModalLabel">举报文件</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="reportForm">
                        <input type="hidden" id="reportFileId" name="fileId">
                        <div class="mb-3">
                            <label for="reportFileName" class="form-label">文件名称:</label>
                            <input type="text" class="form-control-plaintext" id="reportFileName" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="reason" class="form-label">举报原因:</label>
                            <textarea class="form-control" id="reason" name="reason" rows="4" placeholder="请详细描述举报原因..." required></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="submitReport">提交举报</button>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/5.2.3/js/bootstrap.bundle.min.js"></script>
    <script>
        $(document).ready(function() {
            // 检查用户登录状态
            checkLoginStatus();
            
            // 加载公共文件列表
            loadPublicFiles(1);
            
            // 按钮事件监听
            $("#btnUpload").click(function() {
                $("#uploadForm").removeClass("hidden");
            });
            
            $("#cancelUpload").click(function() {
                $("#uploadForm").addClass("hidden");
                clearFileInput();
            });
            
            // 查看公共文件
            $("#btnViewPublic").click(function() {
                // 更新标题
                $("#fileListTitle").text("公共文件列表");
                // 加载公共文件列表
                loadPublicFiles(1);
            });
            
            // 查看私有文件
            $("#btnViewPrivate").click(function() {
                // 更新标题
                $("#fileListTitle").text("我的私有文件");
                // 加载私有文件列表
                loadPrivateFiles(1);
            });
            
            // 管理员文件列表
            $("#btnAdminFiles, #btnAdminPanel").click(function() {
                // 更新标题
                $("#fileListTitle").text("所有文件（管理员视图）");
                // 加载管理员文件列表
                loadAdminFiles(1);
            });
            
            // 退出登录
            $("#btnLogout").click(function() {
                logout();
            });
            
            // 文件上传处理
            $("#fileUploadForm").submit(function(e) {
                e.preventDefault();
                uploadFile();
            });
            
            // 举报表单提交
            $('#submitReport').click(function() {
                submitReport();
            });
        });
        
        // 检查用户登录状态
        function checkLoginStatus() {
            $.ajax({
                url: 'checkLogin',
                type: 'GET',
                dataType: 'json',
                success: function(response) {
                    if (response.status === 200 && response.user) {
                        // 用户已登录
                        $("#guestSection").addClass("hidden");
                        $("#userInfoSection").removeClass("hidden");
                        $("#btnUpload").removeClass("hidden");
                        $("#btnViewPrivate").removeClass("hidden");
                        
                        // 设置用户信息
                        var username = response.user.username;
                        $("#loggedInUsername").text(username);
                        $("#userAvatar").text(username.charAt(0).toUpperCase());
                        
                        // 检查是否为管理员
                        if (response.user.role === 0) {  // 0表示管理员
                            $("#adminBadge").removeClass("hidden");
                            $("#adminMenuSection").removeClass("hidden");
                            $("#reportManageSection").removeClass("hidden");
                            $("#btnAdminFiles").removeClass("hidden");
                            $("#btnReportManage").removeClass("hidden");
                        }
                    } else {
                        // 用户未登录
                        $("#guestSection").removeClass("hidden");
                        $("#userInfoSection").addClass("hidden");
                        $("#btnUpload").addClass("hidden");
                        $("#btnViewPrivate").addClass("hidden");
                        $("#btnAdminFiles").addClass("hidden");
                        $("#btnReportManage").addClass("hidden");
                    }
                },
                error: function() {
                    // 默认为未登录状态
                    $("#guestSection").removeClass("hidden");
                    $("#userInfoSection").addClass("hidden");
                    $("#btnUpload").addClass("hidden");
                    $("#btnViewPrivate").addClass("hidden");
                    $("#btnAdminFiles").addClass("hidden");
                    $("#btnReportManage").addClass("hidden");
                }
            });
        }
        
        // 加载公共文件列表
        function loadPublicFiles(page) {
            $.ajax({
                url: 'files/public',
                type: 'GET',
                data: { page: page },
                dataType: 'json',
                success: function(response) {
                    if (response.status === 200) {
                        displayFiles(response.files, response.totalPages, page, 'public');
                    } else {
                        $("#fileTableBody").html('<tr><td colspan="7" class="text-center">暂无文件</td></tr>');
                    }
                },
                error: function() {
                    $("#fileTableBody").html('<tr><td colspan="7" class="text-center">加载失败，请重试</td></tr>');
                }
            });
        }
        
        // 加载私有文件列表
        function loadPrivateFiles(page) {
            $.ajax({
                url: 'files/private',
                type: 'GET',
                data: { page: page },
                dataType: 'json',
                success: function(response) {
                    if (response.status === 200) {
                        displayFiles(response.files, response.totalPages, page, 'private');
                    } else {
                        $("#fileTableBody").html('<tr><td colspan="7" class="text-center">暂无文件</td></tr>');
                    }
                },
                error: function(xhr) {
                    if (xhr.status === 401) {
                        $("#fileTableBody").html('<tr><td colspan="7" class="text-center">请先登录</td></tr>');
                    } else {
                        $("#fileTableBody").html('<tr><td colspan="7" class="text-center">加载失败，请重试</td></tr>');
                    }
                }
            });
        }
        
        // 加载管理员文件列表
        function loadAdminFiles(page) {
            $.ajax({
                url: 'admin/files',
                type: 'GET',
                data: { page: page },
                dataType: 'json',
                success: function(response) {
                    if (response.status === 200) {
                        displayFiles(response.files, response.totalPages, page, 'admin');
                    } else {
                        $("#fileTableBody").html('<tr><td colspan="7" class="text-center">暂无文件</td></tr>');
                    }
                },
                error: function(xhr) {
                    if (xhr.status === 403) {
                        $("#fileTableBody").html('<tr><td colspan="7" class="text-center">权限不足，只有管理员可以访问</td></tr>');
                    } else {
                        $("#fileTableBody").html('<tr><td colspan="7" class="text-center">加载失败，请重试</td></tr>');
                    }
                }
            });
        }
        
        // 显示文件列表
        function displayFiles(files, totalPages, currentPage, listType) {
            var tableContent = '';
            
            if (files && files.length > 0) {
                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    var rowClass = file.blocked ? 'blocked-file' : '';
                    
                    tableContent += '<tr class="' + rowClass + '">';
                    
                    // 根据文件类型选择图标
                    var fileIcon = getFileIcon(file.fileType);
                    tableContent += '<td><i class="' + fileIcon + ' file-icon"></i> ' + file.fileName;
                    if (file.blocked) {
                        tableContent += ' <span class="badge bg-danger">已封禁</span>';
                    }
                    tableContent += '</td>';
                    
                    tableContent += '<td>' + formatFileSize(file.fileSize) + '</td>';
                    tableContent += '<td>' + file.fileType + '</td>';
                    tableContent += '<td>' + (file.username || '未知用户') + '</td>';
                    tableContent += '<td>' + formatDate(file.uploadTime) + '</td>';
                    tableContent += '<td>' + file.downloadCount + '</td>';
                    
                    // 操作按钮
                    tableContent += '<td>';
                    // 下载按钮 - 根据文件状态和用户权限决定是否显示
                    if (!file.blocked && file.canDownload) {
                        tableContent += '<button class="btn btn-sm btn-success me-1" onclick="downloadFile(' + file.id + ')">';
                        tableContent += '<i class="fas fa-download"></i> 下载</button>';
                    } else if (file.blocked) {
                        tableContent += '<span class="badge bg-danger me-1">不可下载</span>';
                    } else {
                        tableContent += '<span class="badge bg-secondary me-1">需要登录</span>';
                    }
                    
                    // 举报按钮 - 用户登录后才显示
                    if ($("#userInfoSection").hasClass("hidden") === false && !file.blocked) {
                        tableContent += '<button class="btn btn-sm btn-outline-danger me-1" onclick="showReportDialog(' + file.id + ', \'' + file.fileName + '\')">';
                        tableContent += '<i class="fas fa-flag"></i> 举报</button>';
                    }
                    
                    // 管理员操作按钮
                    if (listType === 'admin') {
                        if (file.blocked) {
                            tableContent += '<button class="btn btn-sm btn-warning" onclick="toggleBlockStatus(' + file.id + ', false)">';
                            tableContent += '<i class="fas fa-unlock"></i> 解除封禁</button>';
                        } else {
                            tableContent += '<button class="btn btn-sm btn-danger" onclick="toggleBlockStatus(' + file.id + ', true)">';
                            tableContent += '<i class="fas fa-ban"></i> 封禁</button>';
                        }
                    }
                    
                    tableContent += '</td>';
                    tableContent += '</tr>';
                }
            } else {
                tableContent = '<tr><td colspan="7" class="text-center">暂无文件</td></tr>';
            }
            
            $("#fileTableBody").html(tableContent);
            
            // 生成分页控件
            generatePagination(totalPages, currentPage, listType);
        }
        
        // 封禁/解封文件
        function toggleBlockStatus(fileId, blockStatus) {
            $.ajax({
                url: 'admin/block',
                type: 'POST',
                data: {
                    fileId: fileId,
                    block: blockStatus
                },
                dataType: 'json',
                success: function(response) {
                    if (response.status === 200) {
                        // 操作成功，刷新文件列表
                        alert(response.message);
                        loadAdminFiles(1);
                    } else {
                        alert('操作失败: ' + response.message);
                    }
                },
                error: function(xhr) {
                    var errorMsg = '操作失败';
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMsg += ': ' + xhr.responseJSON.message;
                    }
                    alert(errorMsg);
                }
            });
        }
        
        // 根据文件类型获取对应的图标
        function getFileIcon(fileType) {
            fileType = fileType.toLowerCase();
            if (fileType === 'pdf') {
                return 'fas fa-file-pdf';
            } else if (['doc', 'docx'].includes(fileType)) {
                return 'fas fa-file-word';
            } else if (['xls', 'xlsx'].includes(fileType)) {
                return 'fas fa-file-excel';
            } else if (['ppt', 'pptx'].includes(fileType)) {
                return 'fas fa-file-powerpoint';
            } else if (['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(fileType)) {
                return 'fas fa-file-image';
            } else if (['mp3', 'wav', 'ogg'].includes(fileType)) {
                return 'fas fa-file-audio';
            } else if (['mp4', 'avi', 'mov', 'wmv'].includes(fileType)) {
                return 'fas fa-file-video';
            } else if (['zip', 'rar', '7z', 'tar', 'gz'].includes(fileType)) {
                return 'fas fa-file-archive';
            } else if (['html', 'htm', 'xml'].includes(fileType)) {
                return 'fas fa-file-code';
            } else if (['txt', 'md'].includes(fileType)) {
                return 'fas fa-file-alt';
            } else {
                return 'fas fa-file';
            }
        }
        
        // 生成分页控件
        function generatePagination(totalPages, currentPage, listType) {
            var paginationHtml = '';
            
            // 确定加载函数名称
            var loadFunctionName;
            if (listType === 'public') {
                loadFunctionName = 'loadPublicFiles';
            } else if (listType === 'private') {
                loadFunctionName = 'loadPrivateFiles';
            } else if (listType === 'admin') {
                loadFunctionName = 'loadAdminFiles';
            }
            
            // 上一页按钮
            paginationHtml += '<li class="page-item ' + (currentPage === 1 ? 'disabled' : '') + '">';
            paginationHtml += '<a class="page-link" href="javascript:void(0)" onclick="' + loadFunctionName + '(' + (currentPage - 1) + ')">&laquo;</a>';
            paginationHtml += '</li>';
            
            // 页码按钮
            for (var i = 1; i <= totalPages; i++) {
                paginationHtml += '<li class="page-item ' + (i === currentPage ? 'active' : '') + '">';
                paginationHtml += '<a class="page-link" href="javascript:void(0)" onclick="' + loadFunctionName + '(' + i + ')">' + i + '</a>';
                paginationHtml += '</li>';
            }
            
            // 下一页按钮
            paginationHtml += '<li class="page-item ' + (currentPage === totalPages ? 'disabled' : '') + '">';
            paginationHtml += '<a class="page-link" href="javascript:void(0)" onclick="' + loadFunctionName + '(' + (currentPage + 1) + ')">&raquo;</a>';
            paginationHtml += '</li>';
            
            $("#pagination").html(paginationHtml);
        }
        
        // 文件上传
        function uploadFile() {
            var formData = new FormData($("#fileUploadForm")[0]);
            
            // 显示进度条
            $("#uploadProgress").removeClass("hidden");
            $(".progress-bar").css("width", "0%");
            
            $.ajax({
                url: 'upload',
                type: 'POST',
                data: formData,
                cache: false,
                contentType: false,
                processData: false,
                xhr: function() {
                    var xhr = new window.XMLHttpRequest();
                    xhr.upload.addEventListener("progress", function(evt) {
                        if (evt.lengthComputable) {
                            var percentComplete = evt.loaded / evt.total * 100;
                            $(".progress-bar").css("width", percentComplete + "%");
                        }
                    }, false);
                    return xhr;
                },
                success: function(response) {
                    if (response.status === 200) {
                        showUploadMessage('success', '文件上传成功！');
                        clearFileInput();
                        
                        // 隐藏上传表单
                        $("#uploadForm").addClass("hidden");
                        
                        // 如果是公开文件，则重新加载公共文件列表，否则加载私有文件列表
                        if ($("#isPublic").is(":checked")) {
                            loadPublicFiles(1);
                        } else {
                            loadPrivateFiles(1);
                        }
                    } else {
                        showUploadMessage('danger', '上传失败: ' + response.message);
                    }
                },
                error: function(xhr) {
                    var errorMsg = '上传失败';
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMsg += ': ' + xhr.responseJSON.message;
                    } else if (xhr.status === 401) {
                        errorMsg = '请先登录后再上传文件';
                    }
                    showUploadMessage('danger', errorMsg);
                },
                complete: function() {
                    // 隐藏进度条
                    setTimeout(function() {
                        $("#uploadProgress").addClass("hidden");
                    }, 1000);
                }
            });
        }
        
        // 下载文件
        function downloadFile(fileId) {
            // 创建一个隐藏的a标签，用于下载
            var downloadLink = document.createElement('a');
            downloadLink.href = 'download?id=' + fileId;
            downloadLink.target = '_blank';
            document.body.appendChild(downloadLink);
            downloadLink.click();
            document.body.removeChild(downloadLink);
        }
        
        // 显示上传消息
        function showUploadMessage(type, message) {
            $("#uploadMessage").removeClass("hidden alert-success alert-danger").addClass("alert-" + type).html(message);
            
            // 3秒后自动隐藏消息
            setTimeout(function() {
                $("#uploadMessage").addClass("hidden");
            }, 3000);
        }
        
        // 清空文件输入框
        function clearFileInput() {
            $("#fileUploadForm")[0].reset();
        }
        
        // 格式化文件大小
        function formatFileSize(size) {
            if (size < 1024) {
                return size + ' B';
            } else if (size < 1024 * 1024) {
                return (size / 1024).toFixed(2) + ' KB';
            } else if (size < 1024 * 1024 * 1024) {
                return (size / (1024 * 1024)).toFixed(2) + ' MB';
            } else {
                return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
            }
        }
        
        // 格式化日期
        function formatDate(dateString) {
            var date = new Date(dateString);
            return date.getFullYear() + '-' + 
                   padZero(date.getMonth() + 1) + '-' + 
                   padZero(date.getDate()) + ' ' + 
                   padZero(date.getHours()) + ':' + 
                   padZero(date.getMinutes());
        }
        
        // 数字补零
        function padZero(num) {
            return (num < 10 ? '0' : '') + num;
        }
        
        // 注销功能
        function logout() {
            $.ajax({
                url: 'logout',
                type: 'GET',
                dataType: 'json',
                success: function(response) {
                    if (response.status === 200) {
                        // 更新界面状态
                        checkLoginStatus();
                        // 切换回公共文件列表
                        $("#fileListTitle").text("公共文件列表");
                        loadPublicFiles(1);
                    }
                }
            });
        }
        
        // 显示举报对话框
        function showReportDialog(fileId, fileName) {
            $('#reportFileId').val(fileId);
            $('#reportFileName').val(fileName);
            $('#reason').val('');
            $('#reportModal').modal('show');
        }
        
        // 提交举报
        function submitReport() {
            var fileId = $('#reportFileId').val();
            var reason = $('#reason').val();
            
            if (!reason || reason.trim() === '') {
                alert('请输入举报原因');
                return;
            }
            
            $.ajax({
                url: 'report/submit',
                type: 'POST',
                data: {
                    fileId: fileId,
                    reason: reason
                },
                dataType: 'json',
                success: function(response) {
                    if (response.success) {
                        $('#reportModal').modal('hide');
                        alert('举报提交成功，管理员会尽快处理');
                    } else {
                        alert('举报提交失败: ' + response.message);
                    }
                },
                error: function() {
                    alert('服务器错误，请稍后重试');
                }
            });
        }
    </script>
</body>
</html>