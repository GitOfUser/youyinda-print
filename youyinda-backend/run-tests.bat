@echo off
echo === 优印达第三方对接测试 ===
echo.
echo [1/3] 编译项目...
call mvnw.cmd clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo 编译失败！
    exit /b 1
)
echo 编译通过
echo.
echo [2/3] 运行单元测试...
call mvnw.cmd test -pl . -Dtest="com.youyinda.service.*Test" -q
if %ERRORLEVEL% NEQ 0 (
    echo 测试失败！
    exit /b 1
)
echo 测试通过
echo.
echo [3/3] 完成！
