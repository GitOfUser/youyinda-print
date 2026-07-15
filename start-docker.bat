@echo off
echo ========================================
echo 优印达 Docker 环境启动脚本
echo ========================================
echo.

REM 检查 Docker 是否运行
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] Docker 未运行，请先启动 Docker Desktop！
    pause
    exit /b 1
)

echo [1/4] 停止并清理旧容器...
docker-compose down -v 2>nul

echo.
echo [2/4] 启动 MySQL 和 Redis 服务...
docker-compose up -d

echo.
echo [3/4] 等待服务启动完成...
timeout /t 15 /nobreak >nul

echo.
echo [4/4] 检查服务状态...
docker-compose ps

echo.
echo ========================================
echo Docker 服务启动完成！
echo ========================================
echo - MySQL:  localhost:3307
echo - Redis:  localhost:6380
echo.
echo 后端请在 IDE 或使用 mvn spring-boot:run 启动
echo 查看日志: docker-compose logs -f
echo 停止服务: docker-compose down
echo ========================================
echo.
pause
