# 优印达 Docker 环境配置说明

## 项目结构

```
youyinda-print/
├── docker-compose.yml          # Docker Compose 配置文件
├── start-docker.bat            # Windows 启动脚本（MySQL + Redis）
├── stop-docker.bat             # Windows 停止脚本
├── youyinda-backend/
│   ├── Dockerfile              # 后端服务 Docker 镜像构建文件
│   ├── .dockerignore           # Docker 构建忽略文件
│   └── src/main/resources/
│       └── application-docker.yml  # Docker 环境配置文件
```

## 服务说明

| 服务 | 镜像 | 端口 | 说明 |
|------|------|------|------|
| mysql | mysql:8.0 | 3307 | 数据库服务 |
| redis | redis:6.2-alpine | 6380 | 缓存服务 |

## 快速开始

### 1. 启动 Docker 环境（MySQL + Redis）

双击运行 `start-docker.bat`

或者手动命令：

```bash
docker-compose up -d
```

### 2. 启动后端项目

**方式一：Maven 命令**

```bash
cd youyinda-backend
mvn spring-boot:run
```

**方式二：直接运行 JAR 包**

```bash
cd youyinda-backend
mvn clean package -DskipTests
java -jar target/youyinda-backend-1.0.0.jar
```

**方式三：在 IDE 中运行**

直接在 IDE 中运行 `YouyindaApplication` 主类。

### 3. 访问地址

- 后端 API: http://localhost:8080/api
- 健康检查: http://localhost:8080/api/actuator/health
- Druid 监控: http://localhost:8080/api/druid (admin/admin)

## 配置说明

### 数据库配置 (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/youyinda
    username: root
    password: 1234

  redis:
    host: localhost
    port: 6380
```

### 环境变量

可通过环境变量覆盖配置：

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| WX_MINIAPP_APP_ID | 微信小程序 AppID | your_app_id |
| WX_MINIAPP_APP_SECRET | 微信小程序 AppSecret | your_app_secret |
| WX_PAY_MCH_ID | 微信支付商户号 | your_mch_id |
| WX_PAY_API_V3_KEY | 微信支付 API V3 Key | your_api_v3_key |
| WX_PAY_SERIAL_NO | 微信支付证书序列号 | your_serial_no |
| WX_PAY_NOTIFY_URL | 微信支付回调地址 | https://your-domain.com/api/v1/pay/notify |
| PRINT_PROVIDER_URL | 打印服务商 API 地址 | https://api.print-provider.com |
| EXPRESS_PROVIDER_URL | 快递服务商 API 地址 | https://api.express-provider.com |

## 常用命令

```bash
# 启动 Docker 服务
docker-compose up -d

# 停止 Docker 服务
docker-compose down

# 查看 Docker 服务状态
docker-compose ps

# 查看 Docker 日志
docker-compose logs -f

# 进入 MySQL 容器
docker exec -it youyinda-mysql mysql -uroot -p1234

# 进入 Redis 容器
docker exec -it youyinda-redis redis-cli

# 清理所有未使用的 Docker 资源
docker system prune -a
```

## 故障排查

### 端口被占用

修改 `docker-compose.yml` 中的端口映射：

```yaml
ports:
  - "3308:3306"  # 将本地 3308 映射到容器 3306
```

然后更新 `application.yml` 中的数据库端口。

### 后端启动失败

1. 检查 MySQL 和 Redis 是否健康启动：`docker-compose ps`
2. 查看后端日志
3. 确认数据库连接配置正确

### 数据库初始化

MySQL 首次启动时会自动执行 `youyinda-backend/src/main/resources/init.sql` 初始化脚本。

如需重新初始化：

```bash
docker-compose down -v
docker-compose up -d
```
