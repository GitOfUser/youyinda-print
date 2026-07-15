# 第一阶段：构建
FROM maven:3.8.6-jdk-8-slim AS builder

WORKDIR /app

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建 jar 包
RUN mvn clean package -DskipTests

# 第二阶段：运行
FROM openjdk:8-jre-slim

WORKDIR /app

# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/*.jar app.jar

# 创建日志目录
RUN mkdir -p logs

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
