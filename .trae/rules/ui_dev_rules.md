# 优印达项目 - UI开发强制规则

> **生效日期**: 2026-04-01  
> **规则版本**: v1.0  
> **适用范围**: 全栈开发（微信小程序前端 + Java Spring Boot后端）

---

## 一、核心强制约束（100%严格执行，违者重写）

### 1.1 UI设计稿只读原则
- **绝对禁止**修改项目目录中 `youyinda-ui-sketch` 文件夹及其内部的所有文件、内容
- 该文件夹仅作为UI设计基准，全程保持**只读状态**
- 所有设计参考必须通过复制或引用的方式使用，不得在原文件上修改

### 1.2 像素级还原原则
- 所有前端页面的布局、样式、色值、尺寸、文案、图标、交互逻辑，必须**1:1像素级还原**
- 严格参照 `youyinda-ui-sketch` 文件夹中的高保真UI设计稿
- **不得擅自修改**设计效果，包括但不限于：
  - 颜色值（必须使用设计稿指定的色值）
  - 字体大小和字重
  - 间距和边距
  - 圆角大小
  - 阴影效果
  - 动画时长和缓动函数

### 1.3 接口-UI匹配原则
- 所有后端接口的字段定义、请求/响应格式、业务逻辑、状态流转
- 必须**完全匹配**UI设计稿的页面需求
- 确保前端可直接调用接口实现设计稿中的所有功能

---

## 二、技术栈强制规范

### 2.1 微信小程序前端

#### 框架要求
- **开发框架**: 微信小程序原生框架
- **基础库版本**: 最低兼容 2.25.0+
- **代码规范**: CommonJS规范
- **禁止**: 使用任何第三方跨端框架（如Taro、uni-app等）

#### 代码组织
- **组件化开发**: 页面、组件、工具类分层清晰
- **注释要求**: 所有函数、核心逻辑必须添加中文注释
- **命名规范**: 语义化命名，见名知意

#### 强制封装要求
| 模块 | 要求 |
|------|------|
| 网络请求 | 统一封装request请求（带拦截器、登录态自动处理） |
| API管理 | 集中式API管理，统一维护接口地址 |
| 工具函数 | 全局工具函数库，避免重复代码 |
| 异常处理 | 统一异常处理机制 |

### 2.2 Java后端

#### 基础框架
- **Spring Boot**: 2.7.18 稳定版
- **JDK版本**: 1.8
- **构建工具**: Maven

#### ORM框架
- **MyBatis-Plus**: 3.5.3.1
- **内置插件**: 分页插件、乐观锁插件必须启用

#### 数据库规范
- **MySQL版本**: 8.0
- **字符集**: utf8mb4
- **强制字段**: 所有表必须包含以下字段
  ```sql
  id              bigint      自增主键
  create_time     datetime    创建时间
  update_time     datetime    更新时间（自动更新）
  is_delete       tinyint     软删除（0=未删除，1=已删除）
  ```

#### 缓存规范
- **Redis版本**: 6.x
- **用途**: 用户登录态、接口限流、热点数据缓存

#### 接口规范
- **风格**: RESTful API
- **路径前缀**: `/api/v1/`
- **响应格式**: 统一响应格式
- **参数校验**: 所有接口必须做参数校验

#### 安全规范
- **鉴权**: Spring Security + JWT
- **支付**: 微信支付V3 SDK对接
- **敏感信息**: 脱敏存储
- **日志**: 全链路日志追踪

#### 第三方对接
- **HTTP客户端**: OpenFeign封装打印/快递第三方接口
- **容错机制**: 超时重试、熔断降级
- **配置管理**: 所有配置抽离到application.yml，**禁止硬编码**

---

## 三、业务基准规则

### 3.1 核心业务
- **业务一**: 线上云打印（对标小猴云印交互体验）
- **业务二**: 低价快递寄件聚合平台
- **数据打通**: 双业务数据打通、用户体系统一

### 3.2 盈利模式
```
平台售价 = 第三方基础服务价 × (1 + 后台可配置盈利比例)
```
- 支持**最低限价**兜底规则
- 支持**最低盈利额**兜底规则
- 所有规则支持后台**热更新**

### 3.3 履约模式
- **平台定位**: 纯平台化聚合，不做自营生产
- **打印生产**: 对接第三方打印服务商API
- **快递揽派**: 对接第三方快递服务商API
- **平台职责**: 流量分发、价格封装、订单中转、售后承接

### 3.4 全流程闭环
必须实现从用户进入小程序 → 选择服务 → 下单 → 支付 → 第三方履约 → 物流追踪 → 订单完成的完整链路

---

## 四、设计规范速查表

### 4.1 色彩系统
| 名称 | 色值 | 用途 |
|------|------|------|
| 主色 | `#FF7D00` | 活力橙，主按钮、重点强调 |
| 成功色 | `#00B42A` | 安全绿，成功状态 |
| 错误色 | `#F53F3F` | 警示红，错误提示 |
| 信息色 | `#165DFF` | 信息蓝，链接、提示 |
| 背景色 | `#F9F6F0` | 护眼米白，页面背景 |
| 标题色 | `#1D2129` | 主要文字 |
| 正文色 | `#4E5969` | 次要文字 |
| 辅助色 | `#86909C` | 辅助信息 |

### 4.2 字体规范
| 用途 | 大小 | 字重 |
|------|------|------|
| 页面标题 | 20px | Bold |
| 导航标题 | 16px | SemiBold |
| 正文内容 | 14px | Regular |
| 辅助信息 | 12px | Regular |

### 4.3 圆角规范
| 元素 | 圆角 |
|------|------|
| 主按钮 | 8px |
| 卡片 | 12px |
| 输入框 | 6px |
| 小标签 | 4px |

### 4.4 间距系统（8pt Grid）
| 间距 | 用途 |
|------|------|
| 4px | 最小间距 |
| 8px | 元素内间距 |
| 12px | 页面水平边距 |
| 16px | 卡片内边距 |
| 24px | 模块间距 |

### 4.5 尺寸规范
| 设备 | 尺寸 |
|------|------|
| 小程序 | 375px 宽度 |
| PC后台 | 1920×1080 |

---

## 五、开发任务清单

### 5.1 微信小程序端（8个核心页面）

#### 阶段一：首页与入口
- [ ] **首页** (pages/index/index)
  - 服务入口（打印、快递）
  - Banner轮播
  - 优惠活动展示
  - 订单状态快捷入口

#### 阶段二：打印业务
- [ ] **打印上传** (pages/print/upload)
  - 文件上传组件
  - 文件预览
  - 支持格式提示
- [ ] **打印配置** (pages/print/config)
  - 纸张类型选择
  - 颜色模式选择
  - 单双面选择
  - 装订方式选择
  - 份数设置
  - 价格实时计算
- [ ] **打印确认** (pages/print/confirm)
  - 订单信息确认
  - 收货地址选择
  - 优惠券选择
  - 支付方式选择
  - 最终价格展示

#### 阶段三：快递业务
- [ ] **寄件信息** (pages/express/sender)
  - 寄件人信息填写
  - 收件人信息填写
  - 地址簿选择
- [ ] **包裹信息** (pages/express/package)
  - 物品类型选择
  - 重量/体积输入
  - 保价设置
  - 预估价格计算
- [ ] **快递公司** (pages/express/company)
  - 快递公司列表
  - 价格对比
  - 时效展示
  - 公司选择
- [ ] **快递确认** (pages/express/confirm)
  - 订单信息确认
  - 取件时间选择
  - 支付方式选择
  - 最终价格展示

#### 阶段四：订单与用户
- [ ] **订单列表** (pages/order/list)
  - 状态筛选（全部/待支付/进行中/已完成）
  - 订单卡片展示
  - 分页加载
- [ ] **订单详情** (pages/order/detail)
  - 订单状态追踪
  - 商品/服务信息
  - 价格明细
  - 物流信息（快递）
  - 操作按钮（支付/取消/退款/评价）
- [ ] **物流追踪** (pages/express/track)
  - 物流轨迹展示
  - 地图展示（可选）
  - 快递员信息

#### 阶段五：个人中心
- [ ] **个人中心** (pages/user/index)
  - 用户信息展示
  - 功能入口列表
  - 客服入口
- [ ] **地址管理** (pages/user/address/list)
  - 地址列表
  - 默认地址设置
  - 新增/编辑/删除
- [ ] **地址编辑** (pages/user/address/edit)
  - 地址表单
  - 微信地址导入
- [ ] **优惠券** (pages/user/coupon)
  - 优惠券列表
  - 使用规则说明
- [ ] **帮助中心** (pages/user/help)
  - 常见问题
  - 客服联系方式

### 5.2 后端接口开发

#### 认证模块
- [ ] 微信小程序登录 `/api/v1/auth/login`
- [ ] 手机号解密更新 `/api/v1/auth/phone`
- [ ] 获取用户信息 `/api/v1/auth/user/info`

#### 用户模块
- [ ] 更新用户信息 `/api/v1/user/update`
- [ ] 地址列表 `/api/v1/user/address/list`
- [ ] 新增地址 `/api/v1/user/address/add`
- [ ] 更新地址 `/api/v1/user/address/update`
- [ ] 删除地址 `/api/v1/user/address/delete`
- [ ] 设置默认地址 `/api/v1/user/address/default`

#### 打印模块
- [ ] 文件上传 `/api/v1/print/upload`
- [ ] 获取打印配置选项 `/api/v1/print/options`
- [ ] 计算打印价格 `/api/v1/print/calc`
- [ ] 创建打印订单 `/api/v1/print/order/create`
- [ ] 获取订单状态 `/api/v1/print/order/status`
- [ ] 取消打印订单 `/api/v1/print/order/cancel`

#### 快递模块
- [ ] 获取快递公司列表 `/api/v1/express/companies`
- [ ] 计算快递价格 `/api/v1/express/calc`
- [ ] 创建快递订单 `/api/v1/express/order/create`
- [ ] 取消快递订单 `/api/v1/express/order/cancel`
- [ ] 查询物流轨迹 `/api/v1/express/track`

#### 订单模块
- [ ] 订单列表 `/api/v1/order/list`
- [ ] 订单详情 `/api/v1/order/detail`
- [ ] 取消订单 `/api/v1/order/cancel`
- [ ] 申请退款 `/api/v1/order/refund`

#### 支付模块
- [ ] 创建微信支付 `/api/v1/pay/create`
- [ ] 支付回调 `/api/v1/pay/notify`
- [ ] 查询支付状态 `/api/v1/pay/status`

#### 优惠券模块
- [ ] 优惠券列表 `/api/v1/coupon/list`
- [ ] 领取优惠券 `/api/v1/coupon/receive`

---

## 六、项目目录结构规范

### 6.1 微信小程序端
```
youyinda-miniprogram/
├── app.js                    # 小程序入口
├── app.json                  # 全局配置
├── app.wxss                  # 全局样式
├── sitemap.json              # 站点地图
├── project.config.json       # 项目配置
├── components/               # 公共组件
│   ├── navbar/               # 自定义导航栏
│   ├── tabbar/               # 自定义TabBar
│   ├── price-display/        # 价格展示组件
│   ├── order-card/           # 订单卡片组件
│   └── loading/              # 加载动画组件
├── pages/                    # 页面目录
│   ├── index/                # 首页
│   ├── print/                # 打印业务
│   │   ├── upload/           # 文件上传
│   │   ├── config/           # 打印配置
│   │   └── confirm/          # 订单确认
│   ├── express/              # 快递业务
│   │   ├── sender/           # 寄件信息
│   │   ├── package/          # 包裹信息
│   │   ├── company/          # 快递公司
│   │   ├── confirm/          # 订单确认
│   │   └── track/            # 物流追踪
│   ├── order/                # 订单管理
│   │   ├── list/             # 订单列表
│   │   └── detail/           # 订单详情
│   └── user/                 # 个人中心
│       ├── index/            # 个人中心首页
│       ├── address/          # 地址管理
│       │   ├── list/         # 地址列表
│       │   └── edit/         # 地址编辑
│       ├── coupon/           # 优惠券
│       └── help/             # 帮助中心
├── utils/                    # 工具函数
│   ├── request.js            # 网络请求封装
│   ├── api.js                # API接口管理
│   ├── util.js               # 通用工具函数
│   ├── price.js              # 价格计算工具
│   └── validate.js           # 表单验证工具
├── services/                 # 业务服务层
│   ├── auth.service.js       # 认证服务
│   ├── user.service.js       # 用户服务
│   ├── print.service.js      # 打印服务
│   ├── express.service.js    # 快递服务
│   ├── order.service.js      # 订单服务
│   └── pay.service.js        # 支付服务
└── static/                   # 静态资源
    ├── images/               # 图片资源
    │   ├── icons/            # 图标
    │   ├── banners/          # Banner图
    │   └── empty/            # 空状态图
    └── styles/               # 样式文件
        ├── variables.wxss    # CSS变量
        └── mixins.wxss       # 混入样式
```

### 6.2 Java后端
```
youyinda-backend/
├── src/main/java/com/youyinda/
│   ├── YouyindaApplication.java          # 启动类
│   ├── common/                           # 公共类
│   │   ├── R.java                        # 统一响应封装
│   │   └── PageResult.java               # 分页结果
│   ├── config/                           # 配置类
│   │   ├── SecurityConfig.java           # 安全配置
│   │   ├── JwtAuthenticationFilter.java  # JWT过滤器
│   │   ├── RedisConfig.java              # Redis配置
│   │   ├── MyBatisPlusConfig.java        # MyBatisPlus配置
│   │   └── WebMvcConfig.java             # WebMvc配置
│   ├── controller/                       # 控制器层
│   │   ├── AuthController.java           # 认证接口
│   │   ├── UserController.java           # 用户接口
│   │   ├── PrintController.java          # 打印接口
│   │   ├── ExpressController.java        # 快递接口
│   │   ├── OrderController.java          # 订单接口
│   │   ├── PayController.java            # 支付接口
│   │   └── CouponController.java         # 优惠券接口
│   ├── service/                          # 服务层
│   │   ├── impl/                         # 实现类
│   │   │   ├── UserInfoServiceImpl.java
│   │   │   ├── PrintOrderServiceImpl.java
│   │   │   ├── ExpressOrderServiceImpl.java
│   │   │   └── OrderMainServiceImpl.java
│   │   ├── UserInfoService.java
│   │   ├── PrintOrderService.java
│   │   ├── ExpressOrderService.java
│   │   └── OrderMainService.java
│   ├── mapper/                           # 数据访问层
│   │   ├── UserInfoMapper.java
│   │   ├── PrintOrderMapper.java
│   │   ├── ExpressOrderMapper.java
│   │   └── OrderMainMapper.java
│   ├── entity/                           # 实体类
│   │   ├── UserInfo.java
│   │   ├── PrintOrder.java
│   │   ├── ExpressOrder.java
│   │   └── OrderMain.java
│   ├── dto/                              # 数据传输对象
│   │   ├── WxLoginRequest.java
│   │   ├── PrintOrderCreateRequest.java
│   │   └── ExpressOrderCreateRequest.java
│   ├── vo/                               # 视图对象
│   │   ├── UserInfoVO.java
│   │   ├── PrintOrderVO.java
│   │   └── ExpressOrderVO.java
│   ├── enums/                            # 枚举类
│   │   ├── OrderStatusEnum.java
│   │   └── PayStatusEnum.java
│   ├── util/                             # 工具类
│   │   ├── JwtUtil.java
│   │   ├── WechatUtil.java
│   │   └── PriceCalculateUtil.java
│   ├── feign/                            # Feign客户端
│   │   ├── PrintProviderFeignClient.java
│   │   └── ExpressProviderFeignClient.java
│   ├── fallback/                         # 熔断降级
│   │   ├── PrintProviderFallback.java
│   │   └── ExpressProviderFallback.java
│   └── exception/                        # 异常处理
│       ├── BusinessException.java
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application.yml                   # 主配置
│   ├── application-dev.yml               # 开发环境
│   ├── application-prod.yml              # 生产环境
│   └── mapper/                           # MyBatis映射文件
└── pom.xml                               # Maven配置
```

---

## 七、代码审查检查项

### 7.1 前端检查项
- [ ] 是否严格遵循设计稿的色值、尺寸、间距
- [ ] 是否使用统一的请求封装
- [ ] 是否处理登录态过期
- [ ] 是否添加加载状态
- [ ] 是否处理错误提示
- [ ] 是否添加必要的注释
- [ ] 是否符合微信小程序规范

### 7.2 后端检查项
- [ ] 是否使用统一响应格式
- [ ] 是否做参数校验
- [ ] 是否处理业务异常
- [ ] 是否添加操作日志
- [ ] 是否处理敏感数据脱敏
- [ ] 是否添加必要注释
- [ ] 是否符合RESTful规范

---

## 八、违规处理

| 违规级别 | 处理方式 |
|---------|---------|
| 轻微（命名不规范、缺少注释） | 要求修改后重新提交 |
| 中等（修改设计稿、接口不匹配） | 重写对应模块 |
| 严重（硬编码、安全漏洞） | 回滚代码，重新开发 |

---

**规则维护**: 优印达技术团队  
**最后更新**: 2026-04-01
