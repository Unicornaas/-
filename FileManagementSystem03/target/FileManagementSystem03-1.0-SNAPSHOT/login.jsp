<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>文件管理系统 - 登录/注册</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.bootcdn.net/ajax/libs/bootstrap/5.2.3/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, rgba(116,235,213,0.55) 0%, rgba(172,182,229,0.55) 100%), url('image/R-C.jpg') center center/cover no-repeat fixed;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-container {
            width: 100%;
            max-width: 450px;
            padding: 15px;
        }
        .card {
            border-radius: 22px;
            box-shadow: 0 12px 36px 0 rgba(31, 38, 135, 0.22);
            background: rgba(255,255,255,0.82);
            backdrop-filter: blur(14px) saturate(1.5);
            overflow: hidden;
        }
        .card-header {
            border-radius: 0;
            padding: 1rem;
            background: transparent;
        }
        .nav-tabs .nav-link {
            border: none;
            color: #6c757d;
            font-weight: 600;
            padding: 1rem;
            font-size: 1.1rem;
            letter-spacing: 0.03em;
            transition: color 0.2s;
        }
        .nav-tabs .nav-link.active {
            color: #fff;
            background: linear-gradient(90deg, #74ebd5 0%, #ACB6E5 100%);
            border-radius: 8px 8px 0 0;
            border-bottom: none;
        }
        .card-body {
            padding: 2.2rem 2rem 2rem 2rem;
        }
        .form-floating {
            margin-bottom: 1.2rem;
        }
        .form-control {
            border-radius: 2rem;
            padding-left: 2.5rem;
        }
        .input-icon {
            position: absolute;
            left: 18px;
            top: 50%;
            transform: translateY(-50%);
            color: #74ebd5;
            font-size: 1.1rem;
        }
        .form-floating > .form-control, .form-floating > label {
            padding-left: 2.5rem;
        }
        .form-floating > .input-icon {
            z-index: 2;
        }
        .invalid-feedback {
            display: none;
        }
        .form-control.is-invalid ~ .invalid-feedback {
            display: block;
        }
        .system-name {
            text-align: center;
            margin-bottom: 2rem;
            color: #0d6efd;
            font-weight: 700;
            text-shadow: 0 2px 12px rgba(13,110,253,0.18), 0 0 8px #fff;
            letter-spacing: 0.08em;
            font-size: 2.2rem;
            filter: drop-shadow(0 2px 8px rgba(0,0,0,0.10));
        }
        .btn-login {
            font-size: 1.1rem;
            letter-spacing: 0.07rem;
            padding: 0.85rem 1rem;
            border-radius: 2rem;
            transition: all 0.2s;
            font-weight: 600;
        }
        .btn-login:hover {
            transform: translateY(-2px) scale(1.03);
            box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.13);
        }
        .login-footer {
            text-align: center;
            margin-top: 2rem;
            color: #6c757d;
            font-size: 0.95rem;
        }
        .alert-success {
            background: linear-gradient(90deg, #a8ff78 0%, #78ffd6 100%);
            color: #155724;
            border: none;
        }
        .alert-danger {
            background: linear-gradient(90deg, #ffecd2 0%, #fcb69f 100%);
            color: #721c24;
            border: none;
        }
        @media (max-width: 576px) {
            .card-body { padding: 1.2rem; }
            .system-name { font-size: 1.3rem; }
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h1 class="system-name">欢迎来到文件管理系统</h1>
        
        <div class="card">
            <div class="card-header bg-white p-0">
                <ul class="nav nav-tabs nav-fill" id="authTab" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="login-tab" data-bs-toggle="tab" data-bs-target="#login-tab-pane" type="button" role="tab" aria-controls="login-tab-pane" aria-selected="true">用户登录</button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="register-tab" data-bs-toggle="tab" data-bs-target="#register-tab-pane" type="button" role="tab" aria-controls="register-tab-pane" aria-selected="false">新用户注册</button>
                    </li>
                </ul>
            </div>
            <div class="card-body">
                <div class="tab-content" id="authTabContent">
                    <!-- 登录表单 -->
                    <div class="tab-pane fade show active" id="login-tab-pane" role="tabpanel" aria-labelledby="login-tab" tabindex="0">
                        <form id="loginForm">
                            <div class="alert alert-danger hidden" id="loginError" role="alert" style="display: none;"></div>
                            <div class="alert alert-success hidden" id="loginSuccess" role="alert" style="display: none;"></div>
                            
                            <div class="form-floating mb-3 position-relative">
                                <span class="input-icon"><i class="fa fa-user"></i></span>
                                <input type="text" class="form-control" id="loginUsername" name="username" placeholder="用户名" required>
                                <label for="loginUsername">用户名</label>
                                <div class="invalid-feedback">请输入用户名</div>
                            </div>
                            
                            <div class="form-floating mb-3 position-relative">
                                <span class="input-icon"><i class="fa fa-lock"></i></span>
                                <input type="password" class="form-control" id="loginPassword" name="password" placeholder="密码" required>
                                <label for="loginPassword">密码</label>
                                <div class="invalid-feedback">请输入密码</div>
                            </div>
                            
                            <div class="form-check mb-3">
                                <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe">
                                <label class="form-check-label" for="rememberMe">记住我</label>
                            </div>
                            
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary btn-login">登录</button>
                            </div>
                            <div class="d-grid mt-3">
                                <a href="index.jsp" class="btn btn-outline-secondary btn-login">游客访问（仅浏览公共文件）</a>
                            </div>
                        </form>
                    </div>
                    
                    <!-- 注册表单 -->
                    <div class="tab-pane fade" id="register-tab-pane" role="tabpanel" aria-labelledby="register-tab" tabindex="0">
                        <form id="registerForm">
                            <div class="alert alert-danger hidden" id="registerError" role="alert" style="display: none;"></div>
                            <div class="alert alert-success hidden" id="registerSuccess" role="alert" style="display: none;"></div>
                            
                            <div class="form-floating mb-3 position-relative">
                                <span class="input-icon"><i class="fa fa-user"></i></span>
                                <input type="text" class="form-control" id="registerUsername" name="username" placeholder="用户名" required>
                                <label for="registerUsername">用户名</label>
                                <div class="invalid-feedback">请输入3-20位的用户名</div>
                            </div>
                            
                            <div class="form-floating mb-3 position-relative">
                                <span class="input-icon"><i class="fa fa-lock"></i></span>
                                <input type="password" class="form-control" id="registerPassword" name="password" placeholder="密码" required>
                                <label for="registerPassword">密码</label>
                                <div class="invalid-feedback">密码长度至少为6位</div>
                            </div>
                            
                            <div class="form-floating mb-3 position-relative">
                                <span class="input-icon"><i class="fa fa-lock"></i></span>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" placeholder="确认密码" required>
                                <label for="confirmPassword">确认密码</label>
                                <div class="invalid-feedback">两次输入的密码不一致</div>
                            </div>
                            
                            <div class="form-floating mb-3 position-relative">
                                <span class="input-icon"><i class="fa fa-envelope"></i></span>
                                <input type="email" class="form-control" id="email" name="email" placeholder="邮箱" required>
                                <label for="email">邮箱</label>
                                <div class="invalid-feedback">请输入有效的邮箱地址</div>
                            </div>
                            
                            <div class="d-grid">
                                <button type="submit" class="btn btn-success btn-login">注册</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="login-footer">
        </div>
    </div>
    
    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/5.2.3/js/bootstrap.bundle.min.js"></script>
    <script>
        $(document).ready(function() {
            // 登录表单验证和提交
            $("#loginForm").submit(function(e) {
                e.preventDefault();
                
                // 重置表单状态
                resetFormStatus("#loginForm");
                
                // 表单验证
                var username = $("#loginUsername").val().trim();
                var password = $("#loginPassword").val().trim();
                
                if (!username) {
                    showFieldError("#loginUsername", "请输入用户名");
                    return;
                }
                
                if (!password) {
                    showFieldError("#loginPassword", "请输入密码");
                    return;
                }
                
                // 提交登录请求
                $.ajax({
                    url: "login",
                    type: "POST",
                    data: $("#loginForm").serialize(),
                    dataType: "json",
                    success: function(response) {
                        if (response.status === 200) {
                            // 登录成功
                            showMessage("#loginSuccess", response.message, true);
                            
                            // 立即跳转到首页
                            window.location.href = "index.jsp";
                        } else {
                            // 登录失败
                            showMessage("#loginError", response.message);
                        }
                    },
                    error: function() {
                        showMessage("#loginError", "登录请求失败，请稍后重试");
                    }
                });
            });
            
            // 注册表单验证和提交
            $("#registerForm").submit(function(e) {
                e.preventDefault();
                
                // 重置表单状态
                resetFormStatus("#registerForm");
                
                // 表单验证
                var username = $("#registerUsername").val().trim();
                var password = $("#registerPassword").val().trim();
                var confirmPassword = $("#confirmPassword").val().trim();
                var email = $("#email").val().trim();
                var isValid = true;
                
                // 用户名验证
                if (!username || username.length < 3 || username.length > 20) {
                    showFieldError("#registerUsername", "用户名长度应为3-20位");
                    isValid = false;
                }
                
                // 密码验证
                if (!password || password.length < 6) {
                    showFieldError("#registerPassword", "密码长度至少为6位");
                    isValid = false;
                }
                
                // 确认密码验证
                if (password !== confirmPassword) {
                    showFieldError("#confirmPassword", "两次输入的密码不一致");
                    isValid = false;
                }
                
                // 邮箱验证
                var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!email || !emailRegex.test(email)) {
                    showFieldError("#email", "请输入有效的邮箱地址");
                    isValid = false;
                }
                
                if (!isValid) {
                    return;
                }
                
                // 提交注册请求
                $.ajax({
                    url: "register",
                    type: "POST",
                    data: $("#registerForm").serialize(),
                    dataType: "json",
                    success: function(response) {
                        if (response.status === 200) {
                            // 注册成功
                            showMessage("#registerSuccess", response.message + "，即将转到登录页面", true);
                            
                            // 重置表单
                            $("#registerForm")[0].reset();
                            
                            // 延迟跳转到登录标签页
                            setTimeout(function() {
                                $('#authTab button[data-bs-target="#login-tab-pane"]').tab('show');
                            }, 2000);
                        } else {
                            // 注册失败
                            showMessage("#registerError", response.message);
                        }
                    },
                    error: function() {
                        showMessage("#registerError", "注册请求失败，请稍后重试");
                    }
                });
            });
            
            // 实时验证用户名
            $("#registerUsername").blur(function() {
                var username = $(this).val().trim();
                if (username && username.length >= 3 && username.length <= 20) {
                    // 检查用户名是否已存在
                    $.ajax({
                        url: "checkUsername",
                        type: "GET",
                        data: { username: username },
                        dataType: "json",
                        success: function(response) {
                            if (response.status === 409) {
                                showFieldError("#registerUsername", "该用户名已被使用");
                            }
                        }
                    });
                }
            });
            
            // 实时验证两次密码是否一致
            $("#confirmPassword").keyup(function() {
                var password = $("#registerPassword").val();
                var confirmPassword = $(this).val();
                
                if (password && confirmPassword && password !== confirmPassword) {
                    showFieldError("#confirmPassword", "两次输入的密码不一致");
                } else if (password && confirmPassword && password === confirmPassword) {
                    $("#confirmPassword").removeClass("is-invalid").addClass("is-valid");
                }
            });
            
            // 实时验证邮箱格式
            $("#email").blur(function() {
                var email = $(this).val().trim();
                var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                
                if (email && !emailRegex.test(email)) {
                    showFieldError("#email", "请输入有效的邮箱地址");
                } else if (email && emailRegex.test(email)) {
                    $("#email").removeClass("is-invalid").addClass("is-valid");
                }
            });
        });
        
        // 显示字段错误信息
        function showFieldError(selector, message) {
            $(selector).addClass("is-invalid").removeClass("is-valid");
            $(selector).siblings(".invalid-feedback").text(message);
        }
        
        // 显示表单消息
        function showMessage(selector, message, isSuccess) {
            $(selector).text(message).show();
            
            if (!isSuccess) {
                // 3秒后自动隐藏错误消息
                setTimeout(function() {
                    $(selector).hide();
                }, 3000);
            }
        }
        
        // 重置表单状态
        function resetFormStatus(formSelector) {
            $(formSelector + " .is-invalid").removeClass("is-invalid");
            $(formSelector + " .is-valid").removeClass("is-valid");
            $(formSelector + " .alert").hide();
        }
    </script>
</body>
</html> 