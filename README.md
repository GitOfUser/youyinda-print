---
AIGC:
    Label: "1"
    ContentProducer: 001191440300708461136T1XGW3
    ProduceID: 7620c09593e95e5e8c1e60fb77a4f605_dedf8a21840611f184135254006c9bbf
    ReservedCode1: JhYbm+0Dk74AhDhltWUPT+rduas0ZQooCJLXSvWTf7aYPyvRy/LHQqwz7HESPTATkp2NK5vy29NU6iZaF0buhccGk/skAmAVpudR3ilYQ9wSnyGs1/owuzT+uU9HUhO3qTt9e7CRL0Rjo+f8ZDGnsgkUv8V7vxxy71VuBLQeE7pXBB4zXAgzBdFOmkA=
    ContentPropagator: 001191440300708461136T1XGW3
    PropagateID: 7620c09593e95e5e8c1e60fb77a4f605_dedf8a21840611f184135254006c9bbf
    ReservedCode2: JhYbm+0Dk74AhDhltWUPT+rduas0ZQooCJLXSvWTf7aYPyvRy/LHQqwz7HESPTATkp2NK5vy29NU6iZaF0buhccGk/skAmAVpudR3ilYQ9wSnyGs1/owuzT+uU9HUhO3qTt9e7CRL0Rjo+f8ZDGnsgkUv8V7vxxy71VuBLQeE7pXBB4zXAgzBdFOmkA=
---

# 优印达 YouYinDa

> 集云打印与快递寄件于一体的微信小程序聚合平台，对接第三方服务商 API，提供流量分发、价格封装、订单中转与盈利差价服务。

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen)
![Java](https://img.shields.io/badge/Java-21-orange)
![WeChat MiniProgram](https://img.shields.io/badge/WeChat-MiniProgram_原生-07C160)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Redis](https://img.shields.io/badge/Redis-6.2-red)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

---

## 一、项目简介

优印达采用**纯平台化聚合模式**：所有打印生产、快递揽派履约均对接第三方服务商 API（打印：小猴云印；快递：快递100），平台仅做流量分发、价格封装、订单中转、盈利差价赚取。

- **业务一：云打印** — 用户上传文件、配置打印参数、在线支付，由第三方打印服务商生产并配送。
- **业务二：快递寄件** — 用户填写寄/收件信息、比价选公司、在线支付，由第三方快递服务商揽派。

---

## 二、核心功能

| 业务线 | 功能模块 | 说明 |
|--------|----------|------|
| 云打印 | 文件上传 | 支持多文件上传，前端实时价格预览 |
| 云打印 | 打印配置 | 纸张规格（A4/A3）、色彩（黑白/彩色）、单双面、份数、装订 |
| 云打印 | 订单与支付 | 微信支付统一下单、回调、退款 |
| 快递寄件 | 寄件信息 | 寄件人/收件人表单、物品类型与重量 |
| 快递寄件 | 比价选公司 | 多家快递公司实时比价列表 |
| 快递寄件 | 物流追踪 | 定时同步物流轨迹，时间轴展示 |
| 公共 | 用户中心 | 地址管理、优惠券、订单统计 |
| 后台 | 运营管理 | 仪表盘、订单/用户管理、价格与盈利配置、系统设置 |

---

## 三、技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.18 | 核心框架 |
| Spring Security | 2.7.18 | 安全与鉴权 |
| MyBatis-Plus | 3.5.3.1 | ORM 持久层 |
| MySQL Connector/J | 8.0.33 | 数据库驱动 |
| Druid | 1.2.18 | 数据库连接池 |
| Redis (Lettuce) | — | 缓存 |
| Spring Cloud OpenFeign | 2021.0.8 | 第三方 HTTP 客户端 |
| Resilience4j | — | 熔断降级 / 重试 |
| JWT (jjwt) | 0.11.5 | Token 认证 |
| 微信支付 Java SDK | 0.2.12 | 微信支付 |
| Hutool | 5.8.20 | 工具类 |
| Lombok | — | 代码简化 |

### 小程序

| 技术 | 说明 |
|------|------|
| 微信原生小程序 | WXML / WXSS / JavaScript |
| utils/request.js | 统一请求拦截、Token 管理、401 自动重登 |
| utils/price.js | 前端实时价格预览 |

### 基础设施

| 组件 | 版本 | 说明 |
|------|------|------|
| MySQL | 8.0 | 主数据库（Docker 端口 3309→3306，库名 `youyinda`） |
| Redis | 6.2 | 缓存（Docker 端口 6380→6379） |
| Docker Compose | — | 服务编排，网络 `youyinda-network` |

---

## 四、项目结构

```
D:\Projects\youyinda-print\
├── youyinda-backend/              # 后端服务（Spring Boot）
│   └── src/main/java/com/youyinda/
│       ├── YouyindaApplication.java   # 启动类
│       ├── common/                    # 统一响应 R、异常、错误码
│       ├── config/                    # JWT/Feign/Redis/Security/MyBatisPlus 等配置
│       ├── controller/                # 17 个 Controller
│       ├── dto/                       # 入参 DTO（30+）
│       ├── entity/                    # 实体类（24 个）
│       ├── enums/                     # 订单状态等枚举
│       ├── exception/                 # 全局异常处理
│       ├── fallback/                  # Feign 降级
│       ├── feign/                     # 第三方服务商 Feign 客户端
│       ├── mapper/                    # MyBatis-Plus Mapper（24 个）
│       ├── service/ + impl/           # 业务层
│       ├── task/                      # 定时任务（物流同步/API 同步）
│       ├── util/                      # JwtUtil/PriceCalculateUtil/WechatUtil 等
│       └── vo/                        # 出参 VO（30+）
│       └── resources/
│           ├── application.yml        # 主配置
│           ├── application-docker.yml # Docker 配置
│           └── init.sql / init_simple.sql / init_test.sql / config_init.sql
├── youyinda-miniprogram/          # 微信小程序（原生）
│   ├── app.js / app.json / app.wxss
│   ├── pages/
│   │   ├── index/                  # 首页
│   │   ├── print/  (upload/config/confirm)
│   │   ├── express/ (sender/package/company/confirm/track)
│   │   ├── order/  (list/detail)
│   │   ├── user/   (index/address/coupon/help)
│   │   └── login/
│   ├── components/  (navbar/status-tag)
│   ├── images/tab/
│   └── utils/  (request/price/util/wechat)
├── youyinda-ui/                   # UI 高保真设计稿（HTML 单文件）
│   ├── index.html / miniapp.html / admin.html
│   └── 启动预览.bat
├── youyinda-mp/                   # 小程序占位目录（空）
├── youyinda_db/                   # 数据库脚本目录（空，脚本在 backend/resources）
├── docs/                          # 项目文档（14 个 .md）
├── logs/                          # 运行日志
├── docker-compose.yml             # MySQL + Redis 编排
├── DOCKER_README.md
├── start-debug.bat                # 本地调试启动
├── start-docker.bat / stop-docker.bat
└── .gitignore
```

---

## 五、快速开始

### 1. 克隆项目

```bash
git clone <repo-url> D:\Projects\youyinda-print
cd D:\Projects\youyinda-print
```

### 2. 启动基础设施（MySQL + Redis）

```bash
# 使用 Docker Compose 一键启动
start-docker.bat
# 或手动：docker-compose up -d
```

默认配置：
- MySQL：端口 `3309→3306`，库名 `youyinda`，密码 `1234`
- Redis：端口 `6380→6379`

### 3. 初始化数据库

执行 `youyinda-backend/src/main/resources/init.sql` 与 `config_init.sql` 完成建表与基础数据初始化。

### 4. 配置环境变量

`application.yml` 中微信 appId/secret、支付密钥、第三方 API 地址均为占位符，需通过环境变量注入：

```env
WECHAT_APPID=your_appid
WECHAT_SECRET=your_secret
WX_PAY_MCH_ID=your_mch_id
WX_PAY_API_V3_KEY=your_api_v3_key
WX_PAY_NOTIFY_URL=your_notify_url
PRINT_PROVIDER_API=your_print_api
EXPRESS_PROVIDER_API=your_express_api
```

### 5. 启动后端

```bash
cd youyinda-backend
mvn clean package -DskipTests
java -jar target/youyinda-backend.jar
# 或使用调试脚本
start-debug.bat
```

后端服务地址：`http://localhost:8080/api`

### 6. 打开小程序

使用**微信开发者工具**导入 `youyinda-miniprogram` 目录，配置合法的 AppID 后即可预览调试。

---

## 六、API 接口概览

### 接口规范

- **基础路径**：`/api/v1/`（用户端）、`/api/v1/admin/`（管理端）
- **统一响应**：`{ code: 200, message: "success", }`
- **响应码**：200 成功 / 400 参数错误 / 401 未登录 / 403 无权限 / 404 不存在 / 500 系统错误
- **鉴权**：除登录接口外均需 `Authorization: Bearer <token>`

### 主要接口

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 认证 | POST | `/v1/auth/login` | 微信 code2session 登录、JWT 签发 |
| 用户 | GET | `/v1/user/info` | 获取用户信息 |
| 地址 | GET/POST/PUT/DELETE | `/v1/user/address/*` | 地址管理 |
| 打印 | POST | `/v1/print/upload` | 文件上传 |
| 打印 | POST | `/v1/print/order` | 创建打印订单 |
| 打印 | GET | `/v1/print/order/list` | 打印订单列表 |
| 快递 | POST | `/v1/express/order` | 创建快递订单 |
| 快递 | GET | `/v1/express/companies` | 快递公司比价 |
| 快递 | GET | `/v1/express/track` | 物流追踪 |
| 价格 | POST | `/v1/price/calculate` | 价格计算 |
| 盈利 | GET/PUT | `/v1/profit-rule/*` | 盈利规则配置（热更新） |
| 支付 | POST | `/v1/pay/unified-order` | 微信支付统一下单 |
| 后台 | GET | `/v1/admin/dashboard` | 仪表盘数据 |
| 后台 | GET | `/v1/admin/orders` | 订单管理 |
| 后台 | GET | `/v1/admin/users` | 用户管理 |
| 后台 | GET/PUT | `/v1/admin/price-config` | 价格配置 |
| 后台 | GET/POST/PUT | `/v1/admin/coupon/*` | 优惠券管理 |
| 后台 | GET/PUT | `/v1/admin/sys-config` | 系统配置 |

---

## 七、数据流

### 打印订单流程

```
上传文件 → 配置参数(纸张/色彩/单双面/份数) → 价格计算 → 确认订单
→ 微信支付 → 创建 order_main/order_detail → 调用第三方打印 API
→ 处理中 → 配送 → 完成
```

### 快递订单流程

```
寄件人/收件人/物品 → 选快递公司(比价) → 价格计算 → 确认支付
→ 创建 order_main/order_logistics → 第三方下单
→ 待揽收 → 运输中(定时同步) → 完成
```

### 价格盈利引擎

- 打印单页价 = `MAX(第三方基础价 × (1 + 盈利比例%), 最低限价)`
- 打印计费页数 = 奇数进一 `(pages+1)/2*2`
- 打印总价 = 单页价 × 计费页数 + 装订 + 增值 + 快递
- 快递运费 = 首重价 + `ceil((总重−首重)/续重单位) × 续重单价`
- 快递最终价 = `MAX(第三方运费×(1+盈利比例%), 第三方运费+最低固定盈利额)`
- 所有规则支持后台热更新，禁止硬编码

---

## 八、相关文档索引

| 文档 | 路径 |
|------|------|
| 开发文档（核心参考） | `docs/优印达项目-开发文档.md` |
| 本地运行文档 | `docs/优印达项目-本地运行文档.md` |
| 项目部署文档 | `docs/项目部署文档.md` |
| Docker 说明 | `DOCKER_README.md` |
| 前后端联调指南 | `docs/前后端联调指南.md` |
| 测试用例 | `docs/测试用例.md` |
| 性能优化方案 | `docs/性能优化方案.md` |
| 运维监控方案 | `docs/运维监控方案.md` |
| 上线 Checklist | `docs/上线Checklist.md` |
| 用户协议 / 隐私政策 | `docs/用户协议.md` / `docs/隐私政策.md` |
| UI 设计预览说明 | `youyinda-ui/README.md` |

---

## 九、许可证

本项目采用 [MIT License](LICENSE)。

> ⚠️ 安全提示：生产环境请使用 Jasypt 加密敏感配置（JWT secret、数据库密码、支付密钥），并通过环境变量或配置中心注入，切勿明文提交。
*（内容由AI生成，仅供参考）*
