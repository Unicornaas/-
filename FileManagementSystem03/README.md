# 文件管理系统

一个基于Java Web的文件管理系统，支持文件上传、下载、权限管理等功能。

## 功能特性

- 用户管理：支持用户注册、登录、注销
- 文件上传：登录用户可上传文件
- 文件分类：支持公共文件和私有文件
- 权限控制：
  - 游客仅可查看公共文件列表，不能下载
  - 登录用户可下载公共文件和自己的私有文件
  - 管理员可管理所有文件
- 文件下载：记录文件下载次数
- 资源管理：管理员可处理举报，封禁违规资源
- 分页展示：文件列表支持分页显示

## 技术栈

- 前端：HTML5、CSS3、JavaScript、jQuery、Bootstrap 5
- 后端：Java、Jakarta Servlet、JDBC
- 数据库：MySQL
- 开发工具：Maven

## 快速开始

### 1. 数据库配置

1. 创建MySQL数据库
2. 执行`src/main/resources/db_init.sql`初始化数据库和表结构

### 2. 修改数据库连接配置

编辑`src/main/java/org/example/filemanagementsystem03/util/DatabaseUtil.java`文件，设置正确的数据库连接参数：

```java
private static final String URL = "jdbc:mysql://localhost:3306/file_management?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
private static final String USERNAME = "root";  // 修改为你的MySQL用户名
private static final String PASSWORD = "123456"; // 修改为你的MySQL密码
```

### 3. 编译部署

1. 使用Maven编译项目：`mvn clean package`
2. 将生成的war包部署到Tomcat等Web服务器

### 4. 访问系统

浏览器访问：`http://localhost:8080/FileManagementSystem03/`

## 默认账户

- 管理员账户：admin / admin123
- 测试用户账户：test / test123

## 注意事项

- 文件上传目录默认为应用根目录下的`uploads`文件夹，确保该目录有写入权限
- 上传文件大小限制：单个文件不超过10MB，总请求不超过50MB
- 本系统为教学/演示项目，生产环境使用前请加强安全措施 