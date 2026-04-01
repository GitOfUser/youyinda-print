@echo off
chcp 65001
echo ========================================
echo 优印达项目 - 前后端联调启动脚本
echo ========================================
echo.

echo [1/3] 检查MySQL服务...
sc query MySQL80 >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] MySQL服务未运行，请先启动MySQL服务
    echo 启动命令: net start MySQL80
    pause
    exit /b 1
) else (
    echo [成功] MySQL服务正在运行
)

echo.
echo [2/3] 检查Redis服务...
sc query Redis >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] Redis服务未运行，Redis为可选服务
    echo 如需使用Redis缓存功能，请启动Redis服务
) else (
    echo [成功] Redis服务正在运行
)

echo.
echo [3/3] 启动后端服务...
cd /d "%~dp0youyinda-backend"
echo 正在编译项目...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [错误] 项目编译失败
    pause
    exit /b 1
)

echo.
echo 编译成功，正在启动后端服务...
echo 后端服务地址: http://localhost:8080
echo API文档地址: http://localhost:8080/api/doc.html
echo.
echo 按 Ctrl+C 可停止服务
echo.

java -jar target/youyinda-backend-1.0.0.jar

pause
