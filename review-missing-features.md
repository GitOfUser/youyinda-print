# 优印达打印模块「缺失功能/业务逻辑」审查清单

- 审查时间：2026-09-08
- 项目根：D:\Projects\youyinda-print（前端 youyinda-miniprogram / 后端 youyinda-backend）
- 审查方式：前端页面接口调用 vs 后端 Controller 映射逐项核对；后端 Service 实现通读；状态枚举/落库字段核对；SQL 种子数据核对
- 严重程度：P0=阻断下单/支付核心链路，P1=业务逻辑缺失或错误（影响正确性/体验），P2=可选优化/待确认

---

## P0 阻断下单/支付核心链路

### 1. 微信支付链路完全缺失（阻断所有支付）
- **所在页面/接口**：后端无 `WxPayController`；`WxPayServiceImpl` 的 `createPayment / handlePaymentNotify / closeOrder / refundOrder` 四个方法全部为空实现（仅 log.info 后返回空对象/无操作）。前端 4 处调用 `/wx-pay/create`（pages/print/confirm.js、pages/order/list.js、pages/order/detail.js、pages/express/confirm.js）全部命中 404。
- **为什么需要**：无支付则订单永远停在"待支付"，购物车项 `removeSettledItems()` 只在支付成功回调中执行，用户无法完成任何一单；支付回调缺失导致 `OrderMain.updatePayStatus()`（置 status=2 待打印）无任何调用方，支付后状态永远不推进。
- **建议实现**：新增 `WxPayController(/v1/wx-pay)`，实现 `createPayment`（微信 JSAPI 统一下单，返回 timeStamp/nonceStr/package/signType/paySign）、`notify` 回调（验签→更新 order_main.pay_status/status/pay_time→防重），并实现 closeOrder/refundOrder（退款需接入微信退款接口）。

### 2. 打印文件无法交付第三方（打印订单无文件、第三方无法打印）
- **所在页面/接口**：前端 print/confirm.js 提交 `specJson: null`；后端 `PrintOrderServiceImpl.createPrintOrder` 将 `item.getSpecJson()` 同时赋给 `OrderDetail.productName / fileUrl / fileName`（全部 NULL 落库）；`placeThirdOrder` 中 `req.setFileUrl(item.getSpecJson())` 同样为 null。
- **为什么需要**：① 订单详情查不到打印了什么文件（文件名/URL 为 NULL）；② 第三方打印服务商拿不到文件地址，无法真正执行打印；③ 即便 specJson 传入，`fileUrl` 也是相对路径 `/uploads/...`，第三方公网无法访问 `localhost:8080`。
- **建议实现**：新增文件对外访问地址（nginx/对象存储/公网域名映射 `/uploads/**`）；下单时从 `item.fileId` 反查 FileInfo 取 `fileName/fileUrl` 落库；`placeThirdOrder` 传入完整可下载 URL。

### 3. 快递下单接口路径 + 请求体双重不匹配（快递下单必然失败）
- **所在页面/接口**：前端 pages/express/confirm.js 调 `POST /express/order/create`，后端实为 `POST /v1/express/order`（ExpressController）→ 404。且请求体前端传对象 `{senderAddress, receiverAddress, packageInfo, selectedCompany, couponId, totalAmount}`，后端 `ExpressOrderCreateRequest` 要求 `senderAddressId / receiverAddressId / expressCode / weight / totalAmount`（@NotNull/@NotBlank）→ 即便路径修正，校验也会直接 400。
- **为什么需要**：快递下单是 order/list"全部/快递"Tab 的组成部分，路径+字段双重错误导致快递单永远建不成。
- **建议实现**：前端改为 `POST /express/order`，提交 `{senderAddressId, receiverAddressId, expressCode, weight, goodsType, goodsDesc, totalAmount, couponId, remark}`（从所选地址对象取 id、selectedCompany 取 expressCode、packageInfo 取 weight）。

### 4. 订单详情页接口缺失（打印/快递详情均 404，取消失效）
- **所在页面/接口**：pages/order/detail.js 调 `GET /order/detail?orderId=` 与 `POST /order/cancel`；后端打印为 `GET /v1/print/order/detail/{id}`、`POST /v1/print/order/cancel/{id}`，快递为 `GET /v1/express/order/{id}`、`POST /v1/express/order/{id}/cancel` → 全部不匹配 404。
- **为什么需要**：订单详情页不可用；`canPay: res.status === 0` 与后端 status（中文文本或 1-7 数字）恒不等 → 详情页永远不能支付/取消。
- **建议实现**：detail.js 按 orderType 区分请求路径（打印 `/print/order/detail/{id}`，快递 `/express/order/{id}`）；取消分别走 `/print/order/cancel/{id}`、`/express/order/{id}/cancel`；`canPay` 改为 `status===1`（或后端返回数字状态）。

---

## P1 业务逻辑缺失/错误

### 5. 订单状态机三套互相矛盾的语义，未统一
- **所在位置**：① OrderMain 实体注释 `5=已完成,6=已取消,7=售后中`；② PrintOrderController.getOrderStatusText `5=已取消,6=已完成`；③ ExpressOrderServiceImpl 用字符串常量 `ORDER_STATUS_CANCELED/FINISHED` 且落库按 `6=已取消,7=已完成`；④ enums/OrderStatusEnum code 为字符串 `"pending_pay/paid/..."`，与 OrderMain.status(int) 完全两套体系。
- **为什么需要**：前端 order/list.js 共用 `STATUS_TEXT{5:已取消,6:已完成,7:售后中}` 渲染两类订单——快递"已取消"(6) 会被显示为"已完成"，快递"已完成"(7) 显示为"售后中"，用户看到的状态全错。
- **建议实现**：统一使用数字状态码（1待支付 2待打印 3待发货 4待收货 5已完成 6已取消 7售后中），删除/废弃 OrderStatusEnum 字符串码在落库层的使用，前端两处 STATUS_TEXT/statusMap 收敛为一份。

### 6. 打印订单状态无自动流转（支付后无人推进）
- **所在位置**：`OrderMainService.updatePayStatus` 存在但无任何调用方；`PrintOrder.thirdStatus` 恒为 0；只有快递有 `ExpressTrackSyncTask`，**无打印订单第三方状态轮询任务**。
- **为什么需要**：待支付→(支付)→待打印→待发货→…整条状态机后端没有推进逻辑，订单卡死。
- **建议实现**：新增打印订单状态轮询定时任务（查询第三方订单状态→更新 thirdStatus→联动 order_main.status），或至少提供 admin 手动流转接口 + 支付回调推进。

### 7. 前端展示价 ≠ 后端落库金额（无金额校验，价格不透明）
- **所在位置**：前端 utils/price.js `calcItemPrice` 本地估算（硬编码 10 页、单价 0.10/0.50、色彩加成 0.8 倍）；后端按 `print_base_price` 第三方基础价×profitRatio 重算 orderTotal 且**完全忽略请求 totalAmount**。
- **为什么需要**：用户确认页看到的 finalPrice 与后端实收必然不同（前端 0.10/0.50 估算 vs 后端 0.08/0.30 起步），且后端不校验 totalAmount，金额无法对账。
- **建议实现**：前端配置/确认页统一调后端 `/v1/price/calculate` 获取真实报价；后端下单前校验前端 totalAmount 与后端重算金额一致（允许误差），不一致拒绝。

### 8. 打印页数缺失（quantity 硬编码 10）
- **所在位置**：print/confirm.js `quantity: 10` 硬编码；upload/config 均未解析文件真实页数。
- **为什么需要**：计费页数恒为 10，与文件实际页数无关，价格严重失真，且第三方打印按页计费会被拒绝。
- **建议实现**：上传后解析 PDF/Word 页数（后端解析或前端读页数），随 cart item 存储并在下单时提交真实 `quantity`。

### 9. 下单未校验文件归属/存在（越权风险）
- **所在位置**：`PrintOrderServiceImpl.createPrintOrder` 对 `item.fileId` 完全不查 FileInfo 表、不校验归属当前用户、不校验文件是否存在。
- **为什么需要**：任意用户可伪造 fileId 下单，越权引用他人文件。
- **建议实现**：下单时按 `fileId + userId` 校验 FileInfo 存在且属于当前用户，不满足即拒绝。

### 10. 优惠券功能两端未接通
- **所在位置**：后端 `PrintOrderCreateRequest.couponId` 声明但 createPrintOrder 完全未使用；无用户端 `/user/coupon/list`（仅有 admin `/admin/v1/coupons`）；前端 pages/user/coupon.js 调 `/user/coupon/list` → 404；print/confirm.js 与 express/confirm.js 的 selectCoupon 仅 toast"暂无可用优惠券"。
- **为什么需要**：优惠券入口与接口存在但全链路无效，用户无法使用/查看优惠券。
- **建议实现**：新增用户优惠券查询接口 `/v1/user/coupon/list`；下单时校验并核销 couponId、扣减金额，落库优惠记录。

### 11. 增值服务（覆膜/打孔）未生效
- **所在位置**：后端 `calcValueAddedFee` 用 `specJson.contains("覆膜"/"打孔")` 判断，而前端 specJson 恒为 null 且 print/config.js 无覆膜/打孔 UI → 永不计费，`FILM_PER_PAGE/PUNCH_PER_COPY` 为死代码。
- **为什么需要**：配置页无入口 + 后端判空 → 覆膜/打孔业务完全不存在。
- **建议实现**：config 页增加覆膜/打孔选项并入 cart item config；下单项新增结构化字段（如 `valueAdded: {lamination, punch}`），后端按结构化字段计费。

### 12. 快递物流轨迹不可查（track 路径错误 + 同步任务空转）
- **所在位置**：前端 express/track.js 调 `GET /express/track?orderId=`，后端实为 `GET /v1/express/order/{id}/track` → 404；`ExpressTrackSyncTask` 用 `eq(order_type, "express")`（字符串）比对 int 列恒不命中，且 `OrderLogisticsServiceImpl.syncLogisticsStatus` 为空实现。
- **为什么需要**：轨迹页查不到数据；物流状态定时同步完全不生效，订单永远停在"运输中"。
- **建议实现**：前端改调 `/express/order/{id}/track`；同步任务按 order_type=2 过滤，syncLogisticsStatus 接入快递100 轨迹查询并回写。

### 13. 地址编辑/删除/回显失效（接口与方法不匹配）
- **所在位置**：① edit.js 回显调 `/user/address/detail?id=`，后端无此接口 → 404；② edit.js 保存更新用 `request.post('/user/address/update')`，后端是 `@PutMapping` → 405；③ list.js 删除用 `request.post('/user/address/delete',{id})`，后端是 `@DeleteMapping("/delete/{id}")` → 405+路径不符。
- **为什么需要**：地址新增可用，但编辑回显、保存修改、删除全部不可用，直接影响下单地址选择与维护。
- **建议实现**：后端补 `/user/address/detail/{id}`；前端 update 改用 `request.put`，delete 改用 `request.delete('/user/address/delete/'+id)`。

### 14. 快递公司列表价格是前端伪报价
- **所在位置**：express/company.js `calcExpressPrice(weight, 10)` 硬编码 basePrice=10、续重 5 元/公斤，忽略后端 `/express/price/calculate` 与 company/list 的真实报价；confirm 页 finalPrice=selectedCompany.price 与后端 express_base_price 计价不一致。
- **为什么需要**：用户看到的快递价格与实际结算不一致。
- **建议实现**：company 页调用 `/express/price/calculate` 展示真实报价，confirm 用后端最终价。

### 15. PrintOrder.color_type 恒记 1（黑白）
- **所在位置**：`PrintOrderServiceImpl.colorTypeOf` 返回 `item.getColorType()`（"1"/"2" 数字字符串），`"彩色".equals(...)` 恒 false → `printOrder.setColorType(2:1)` 恒为 1。
- **为什么需要**：打印订单表色彩信息错误，后续按色彩统计/查询失真。
- **建议实现**：colorTypeOf 解析 "1"/"2"/"黑白"/"彩色" 后再比较。

### 16. 第三方服务商全为占位，无真实降级（下单静默失败）
- **所在位置**：`PrintProviderFeignClient`/`ExpressProviderFeignClient` url 指向占位域名（https://api.print-provider.com、https://api.66yin.com 等），无真实密钥；`PrintProviderFallback` 仅打日志返回失败响应；`placeThirdOrder` 在 66印/小猴均失败时 catch 后**不抛异常**，本地订单照常落库。
- **为什么需要**：第三方不可达时订单"看起来成功"但永远不会被打印；无补偿/告警。
- **建议实现**：接入真实服务商并配置密钥；第三方全部失败时要么回滚订单，要么落库标记 thirdStatus 异常并触发告警/人工介入。

### 17. 定时任务占位（快递价格同步空实现、告警无通道）
- **所在位置**：`ThirdApiSyncTask.syncExpressBasePrice` 为空实现（仅 log.info）；`sendAlert` 仅 log.error，无真实告警渠道。
- **为什么需要**：声明的定时能力未实现，快递基础价无法自动同步；异常无通知。
- **建议实现**：补全快递价格同步（调用 ExpressProviderFeignClient），告警接入企业微信/短信/邮件。

---

## P2 可选优化 / 待确认

### 18. 打印场景模板能力未接通（待确认是否本期范围）
- **所在位置**：`PrintSceneServiceImpl` 已实现 listScenes/applyScene/customScene，但**无任何 Controller 暴露**，前端也无入口。
- **为什么需要**：已实现能力处于死代码状态。
- **建议实现**：若场景模板属本期规划，新增 `PrintSceneController(/v1/print/scene)` 并在 config 页提供场景选择入口。

### 19. 文件上传无类型/大小白名单（待确认）
- **所在位置**：PrintFileController.uploadFile 仅校验非空，无扩展名/类型/大小限制（application.yml 已配 multipart 50MB 上限，但未按业务限制可打印格式）。
- **建议实现**：校验 PDF/Word/图片等允许格式，超限拒绝。

### 20. 后端价格引擎 /price/calculate 与前端计价逻辑不一致（待确认）
- **所在位置**：`PriceController.calculatePrice` 存在但前端未调用；其装订费硬编码 staple=2/glue=5（下单侧为 staple=1/glue=5），增值服务仅支持 lamination=3，快递费硬编码 10。
- **建议实现**：前端统一改走后端报价，消除两套计价逻辑。

### 21. WxPayRequest.packageValue 与 wx.requestPayment.package 字段名对齐（待确认）
- **所在位置**：`WxPayRequest.packageValue`，小程序 `wx.requestPayment` 需要 `package` 字段。当前空实现无实际返回，实现支付时需保证序列化字段名。

### 22. 快递模块沿用 globalData.flowState（与打印模块持久化方案不一致，风格问题）
- **所在位置**：express/sender/package/company/confirm 依赖 globalData.flowState + storage 双写；打印模块已改为 printCart 持久化。
- **待确认**：是否统一为持久化方案。

### 23. 任务日志中文乱码（GBK 编码，非功能缺失）
- **所在位置**：ExpressTrackSyncTask / ThirdApiSyncTask 源码内中文日志乱码。
- **建议**：统一 UTF-8 编码，规范日志可读性。

---

## 补充核实结论（已核实、非缺失）

- 前端 BASE_URL `http://localhost:8080/api/v1` 与后端 `context-path: /api` + `/v1/**` 拼接一致。
- `/v1/print/file/upload`、`/v1/print/order/create`、`/v1/print/order/list`、`/v1/express/order/list`、`/v1/express/company/list`、`/v1/user/address/list|add|set-default/{id}`、`/v1/auth/*` 前后端均匹配且有实现。
- 打印下单计价链路（第三方基础价→利润→装订→快递费）后端逻辑完整；A4/A3/照片纸×黑白/彩色×单/双面种子价格数据齐全，color_type 数字(1/2)与前端一致、print_side 中文与映射一致。
- 购物车（printCart）本地持久化闭环完整，下单成功后 removeSettledItems 清理已结算项（前提是支付成功）。
- 后端无 TODO/FIXME/UnsupportedOperationException 显式占位。
