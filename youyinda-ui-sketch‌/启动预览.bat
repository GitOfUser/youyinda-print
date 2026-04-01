@echo off
chcp 65001 >nul
title 优印达 UI 设计稿预览

echo ================================================
echo   优印达 UI 设计方案 - 本地预览启动器
echo ================================================
echo.

:: 获取脚本所在目录
cd /d "%~dp0"

:: 方式一：尝试用 Python 启动 HTTP Server（推荐，避免跨域问题）
echo [1/2] 正在检测 Python...
python --version >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] 检测到 Python，正在启动 HTTP Server...
    echo.
    echo 服务已启动，请在浏览器中访问：
    echo.
    echo   导航首页:   http://localhost:8888/index.html
    echo   小程序端:   http://localhost:8888/miniapp.html
    echo   管理后台:   http://localhost:8888/admin.html
    echo.
    echo 关闭此窗口即可停止服务。
    echo ================================================
    :: 自动打开浏览器
    start "" "http://localhost:8888/index.html"
    python -m http.server 8888
    goto end
)

:: 方式二：Python3 命令
echo [尝试] 检测 python3...
python3 --version >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] 检测到 python3，正在启动 HTTP Server...
    start "" "http://localhost:8888/index.html"
    python3 -m http.server 8888
    goto end
)

:: 方式三：直接用浏览器打开文件（无需服务器）
echo [!] 未检测到 Python，直接用浏览器打开文件...
echo.
echo 正在打开设计稿文件...
start "" "%~dp0index.html"
echo.
echo 如需查看其他页面，请手动打开：
echo   - index.html    (导航首页)
echo   - miniapp.html  (微信小程序端)
echo   - admin.html    (PC 运营管理后台)
echo.
pause

:end
