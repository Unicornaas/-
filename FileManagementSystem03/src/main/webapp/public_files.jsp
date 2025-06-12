<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>公共文件列表</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.bootcdn.net/ajax/libs/bootstrap/5.2.3/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; }
        .table thead th { vertical-align: middle; }
        .empty-tip { color: #888; text-align: center; padding: 40px 0; font-size: 1.2rem; }
    </style>
</head>
<body>
<div class="container mt-5 mb-5">
    <h2 class="mb-4 text-center">公共文件列表</h2>
    <div class="table-responsive">
        <table class="table table-bordered table-hover table-striped align-middle">
            <thead class="table-light">
            <tr>
                <th>文件名</th>
                <th>大小</th>
                <th>上传者</th>
                <th>上传时间</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty files}">
                    <tr><td colspan="5" class="empty-tip">暂无文件</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="file" items="${files}">
                        <tr>
                            <td>${file.fileName}</td>
                            <td>${file.fileSize} 字节</td>
                            <td>${file.username}</td>
                            <td><fmt:formatDate value="${file.uploadTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                            <td>
                                <button class="btn btn-outline-secondary btn-sm" disabled title="游客无法下载">下载</button>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
    <!-- 分页 -->
    <c:if test="${totalPages > 1}">
        <nav aria-label="Page navigation">
            <ul class="pagination justify-content-center">
                <li class="page-item <c:if test='${currentPage == 1}'>disabled</c:if>'">
                    <a class="page-link" href="?page=${currentPage - 1}" tabindex="-1">上一页</a>
                </li>
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <li class="page-item <c:if test='${i == currentPage}'>active</c:if>'">
                        <a class="page-link" href="?page=${i}">${i}</a>
                    </li>
                </c:forEach>
                <li class="page-item <c:if test='${currentPage == totalPages}'>disabled</c:if>'">
                    <a class="page-link" href="?page=${currentPage + 1}">下一页</a>
                </li>
            </ul>
        </nav>
    </c:if>
    <div class="mt-4 text-center">
        <a href="login.jsp" class="btn btn-outline-primary">返回登录</a>
    </div>
</div>
</body>
</html> 