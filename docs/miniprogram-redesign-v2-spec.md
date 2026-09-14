# 优印达小程序 UI 重设计规范 v2（2026-09）

> 本规范是小程序端 18 个页面重设计的唯一视觉标准。所有页面必须严格使用本规范的色值、字号、圆角、阴影与组件类，禁止自定义偏离。

## 1. 设计方向

- 调性：现代、干净、年轻、有呼吸感。白底大留白 + 品牌渐变点缀 + 精致卡片层次。
- 告别 emoji 图标，全站统一使用 `components/icon` 线性 SVG 图标组件。
- 双服务线配色：打印 = 品牌蓝紫，快递 = 青绿。其余界面以品牌蓝紫为主。

## 2. 色彩系统

| 令牌 | 色值 | 用途 |
|------|------|------|
| `--brand` | `#4F46E5` | 品牌主色：主按钮、选中态、tabBar 选中、关键强调 |
| `--brand-dark` | `#4338CA` | 按压态/深色强调 |
| `--brand-light` | `#EEF0FF` | 主色浅底（标签、选中项背景） |
| `--brand-gradient` | `linear-gradient(135deg,#6366F1 0%,#8B5CF6 100%)` | 主按钮/头部氛围渐变 |
| `--print` | `#4F46E5` | 打印服务线主色 |
| `--print-light` | `#EEF0FF` | 打印浅底 |
| `--print-gradient` | 同品牌渐变 | 打印模块渐变 |
| `--express` | `#0D9488` | 快递服务线主色 |
| `--express-light` | `#E6F7F4` | 快递浅底 |
| `--express-gradient` | `linear-gradient(135deg,#14B8A6 0%,#2DD4BF 100%)` | 快递模块渐变 |
| `--success` | `#10B981` / `--success-light` `#E8F8F1` | 已完成 |
| `--info` | `#3B82F6` / `--info-light` `#EBF2FE` | 处理中 |
| `--warning` | `#F59E0B` / `--warning-light` `#FEF5E4` | 待支付/提醒 |
| `--danger` | `#EF4444` / `--danger-light` `#FDEBEB` | 错误/删除 |
| `--bg` | `#F5F6FA` | 页面底色 |
| `--card` | `#FFFFFF` | 卡片/弹层底色 |
| `--t1` | `#0F172A` | 标题/正文主文字 |
| `--t2` | `#475569` | 次要文字 |
| `--t3` | `#94A3B8` | 占位/弱提示 |
| `--line` | `#EEF0F4` | 卡片内分割线 |
| `--border` | `#E5E8F0` | 输入框/选项边框 |

## 3. 字号（rpx）

44（页面大标题）/ 36（区块标题）/ 32（卡片标题）/ 28（正文）/ 24（辅助）/ 20（标签角标）。
价格：整数部分 40-48rpx 加粗，符号与小数 24-28rpx。

## 4. 圆角

10（小标签）/ 16（输入框、选项）/ 20（普通卡片）/ 24（主卡片、弹层）/ 999（胶囊按钮、头像、进度条）。

## 5. 阴影

- 卡片：`0 4rpx 20rpx rgba(15,23,42,0.05)`
- 主按钮：`0 8rpx 24rpx rgba(99,102,241,0.35)`
- 快递按钮：`0 8rpx 24rpx rgba(20,184,166,0.30)`
- 弹层/底部栏：`0 -4rpx 24rpx rgba(15,23,42,0.06)`

## 6. 间距（rpx）

8 / 16 / 24 / 32 / 48；页面左右边距统一 32rpx；卡片内边距 28-32rpx；卡片间距 24rpx。

## 7. 图标组件用法

页面 json 中引入：`"usingComponents": { "icon": "/components/icon/icon" }`

```xml
<icon name="printer" size="{{40}}" color="#4F46E5" />
<icon name="truck" size="{{36}}" color="#0D9488" />
```

- `size` 单位为 rpx，默认 40。
- 图标容器（彩色方块底）用类：`.icon-tile`（96rpx 圆角 24）/ `.icon-tile-sm`（72rpx 圆角 18），配色类 `.tile-brand` `.tile-print` `.tile-express` `.tile-orange` `.tile-red` `.tile-gray`。
- 已内置图标名见 `components/icon/icons.js`（home/order/user/location/bell/search/printer/truck/file/upload/plus/minus/check/close/arrow-right/chevron-right/chevron-down/clock/ticket/help/setting/edit/trash/phone/star/wallet/copy/info/alert/refresh/gift/users/message/eye/shield/credit-card/dot 等）。

## 8. 核心组件规范

### 8.1 页面骨架
- 页面底色 `--bg`；内容区左右 32rpx 边距。
- 卡片 `.card`：白底、圆角 24rpx、轻阴影、内边距 32rpx、下边距 24rpx。

### 8.2 按钮
- 主按钮 `.btn-primary`：高 96rpx、胶囊、品牌渐变、白字 30rpx 600、渐变阴影；`:active` 缩放 0.97。
- 快递场景主按钮加 `.btn-express`（青绿渐变）。
- 次按钮 `.btn-ghost`：白底、1.5rpx `--border` 边框、`--t2` 文字、胶囊。
- 小按钮 `.btn-sm`：高 64rpx。
- 底部固定操作栏 `.bottom-bar`：白底、顶部细线、左侧价格区（`¥` 小字 + 大数字品牌色）、右侧主按钮；页面底部留 `.bottom-bar-space` 占位。

### 8.3 选项组（打印配置等）
- `.opt`：白底、边框 1.5rpx `--border`、圆角 20rpx、文字 `--t2`。
- 选中 `.opt.active`：边框品牌色、浅底 `--brand-light`、文字品牌色加粗。
- 快递场景选中用 `.opt.active-express`（青绿）。

### 8.4 状态标签（订单状态）
- 待支付 `.st-warning`（橙）、处理中 `.st-info`（蓝）、已完成 `.st-success`（绿）、已取消 `.st-gray`（灰）。
- 样式：浅底 + 同色文字 + 圆角 10rpx + 20-22rpx 字号 + 前置小圆点。

### 8.5 表单
- 输入框 `.field`：高 96rpx、底色 `#F7F8FB`、圆角 16rpx、无边框、聚焦时白底 + 品牌色外描边。
- 标签 `.field-label`：24rpx `--t2`，必填用 `*` 品牌色。

### 8.6 空状态
- 居中：大号浅灰线性图标（用 icon 组件 size 120，color #CBD5E1）+ `--t3` 文案 + 可选品牌描边按钮。

### 8.7 物流时间轴
- 左侧竖线 `--line`，节点圆点：最新节点品牌色实心 + 光晕，历史节点 12rpx 浅灰；最新一条文字 `--t1` 加粗，其余 `--t2`。

### 8.8 tabBar
- 图标 PNG 位于 `assets/tabbar/`；未选中 `#94A3B8`，选中 `#4F46E5`；白底顶部分割线。

## 9. 实现约束

1. **禁止改动页面 js 业务逻辑**：所有 bind 事件名、data 字段、setData 调用保持不变；仅允许重排 wxml 结构与 wxss。
2. wxml 中禁止再出现任何 emoji 字符作为图标。
3. 所有页面 json 保持原有 `navigationBarTitleText` 等配置，仅按需添加 usingComponents。
4. 样式优先使用 app.wxss 全局类与 CSS 变量，页面 wxss 只写结构性样式。
