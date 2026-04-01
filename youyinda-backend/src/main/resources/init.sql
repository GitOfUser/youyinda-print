-- 优印达数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `youyinda` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `youyinda`;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` VARCHAR(100) NOT NULL COMMENT '微信OpenID',
  `nick_name` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '用户头像',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号（加密存储）',
  `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
  `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
  `country` VARCHAR(50) DEFAULT NULL COMMENT '国家',
  `language` VARCHAR(20) DEFAULT NULL COMMENT '语言',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 用户地址表
CREATE TABLE IF NOT EXISTS `user_address` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号（加密存储）',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) NOT NULL COMMENT '区县',
  `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址',
  `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
  `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
  `is_default` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认：0-否，1-是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_is_default` (`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 3. 订单主表
CREATE TABLE IF NOT EXISTS `order_main` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `order_type` TINYINT NOT NULL COMMENT '订单类型：1-打印订单，2-快递订单',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
  `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际支付金额',
  `status` TINYINT NOT NULL COMMENT '订单状态：1-待支付，2-待打印/待揽收，3-待发货/运输中，4-待收货/派送中，5-已完成，6-已取消，7-售后中',
  `pay_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-退款中，3-已退款',
  `pay_type` TINYINT DEFAULT 0 COMMENT '支付方式：1-微信支付',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `pay_nonce` VARCHAR(100) DEFAULT NULL COMMENT '支付回调唯一标识',
  `express_company` VARCHAR(50) DEFAULT NULL COMMENT '快递公司',
  `express_no` VARCHAR(50) DEFAULT NULL COMMENT '快递单号',
  `address_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '收货地址ID',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_order_type` (`order_type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_pay_status` (`pay_status`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- 4. 订单详情表
CREATE TABLE IF NOT EXISTS `order_detail` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  `product_type` TINYINT NOT NULL COMMENT '产品类型：1-打印，2-快递',
  `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
  `quantity` INT NOT NULL COMMENT '数量',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `total_price` DECIMAL(10,2) NOT NULL COMMENT '总价',
  `file_hash` VARCHAR(100) DEFAULT NULL COMMENT '文件哈希值',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '文件URL',
  `print_config` JSON DEFAULT NULL COMMENT '打印配置（JSON格式）',
  `express_config` JSON DEFAULT NULL COMMENT '快递配置（JSON格式）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_file_hash` (`file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单详情表';

-- 5. 打印订单表
CREATE TABLE IF NOT EXISTS `print_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '打印订单ID',
  `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单主表ID',
  `page_count` INT NOT NULL COMMENT '页数',
  `color_type` TINYINT NOT NULL COMMENT '打印类型：1-黑白，2-彩色',
  `paper_type` TINYINT NOT NULL COMMENT '纸张类型：1-A4，2-A3，3-其他',
  `binding_type` TINYINT NOT NULL COMMENT '装订类型：0-不装订，1-胶装，2-骑马订',
  `double_sided` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否双面：0-单面，1-双面',
  `third_order_no` VARCHAR(100) DEFAULT NULL COMMENT '第三方订单号',
  `third_status` TINYINT DEFAULT 0 COMMENT '第三方订单状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  INDEX `idx_third_order_no` (`third_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打印订单表';

-- 6. 快递订单表
CREATE TABLE IF NOT EXISTS `express_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '快递订单ID',
  `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单主表ID',
  `sender_name` VARCHAR(50) NOT NULL COMMENT '寄件人姓名',
  `sender_phone` VARCHAR(20) NOT NULL COMMENT '寄件人手机号（加密存储）',
  `sender_province` VARCHAR(50) NOT NULL COMMENT '寄件人省份',
  `sender_city` VARCHAR(50) NOT NULL COMMENT '寄件人城市',
  `sender_district` VARCHAR(50) NOT NULL COMMENT '寄件人区县',
  `sender_detail` VARCHAR(200) NOT NULL COMMENT '寄件人详细地址',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收件人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收件人手机号（加密存储）',
  `receiver_province` VARCHAR(50) NOT NULL COMMENT '收件人省份',
  `receiver_city` VARCHAR(50) NOT NULL COMMENT '收件人城市',
  `receiver_district` VARCHAR(50) NOT NULL COMMENT '收件人区县',
  `receiver_detail` VARCHAR(200) NOT NULL COMMENT '收件人详细地址',
  `weight` DECIMAL(10,2) NOT NULL COMMENT '重量（kg）',
  `volume` DECIMAL(10,2) DEFAULT 0 COMMENT '体积（m³）',
  `goods_type` VARCHAR(50) DEFAULT NULL COMMENT '物品类型',
  `third_order_no` VARCHAR(100) DEFAULT NULL COMMENT '第三方订单号',
  `third_status` TINYINT DEFAULT 0 COMMENT '第三方订单状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  INDEX `idx_third_order_no` (`third_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='快递订单表';

-- 7. 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(1000) NOT NULL COMMENT '配置值',
  `config_type` TINYINT NOT NULL COMMENT '配置类型：1-系统配置，2-盈利规则，3-第三方接口',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活：0-未激活，1-激活',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  INDEX `idx_config_type` (`config_type`),
  INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 8. 打印基础价格表
CREATE TABLE IF NOT EXISTS `print_base_price` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '价格ID',
  `paper_type` TINYINT NOT NULL COMMENT '纸张类型：1-A4，2-A3，3-其他',
  `color_type` TINYINT NOT NULL COMMENT '打印类型：1-黑白，2-彩色',
  `double_sided` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否双面：0-单面，1-双面',
  `base_price` DECIMAL(10,2) NOT NULL COMMENT '基础价格',
  `min_price` DECIMAL(10,2) NOT NULL COMMENT '最低限价',
  `profit_ratio` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '盈利比例（%）',
  `third_provider` VARCHAR(50) NOT NULL COMMENT '第三方服务商',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活：0-未激活，1-激活',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_paper_type` (`paper_type`),
  INDEX `idx_color_type` (`color_type`),
  INDEX `idx_double_sided` (`double_sided`),
  INDEX `idx_third_provider` (`third_provider`),
  INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打印基础价格表';

-- 9. 快递基础价格表
CREATE TABLE IF NOT EXISTS `express_base_price` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '价格ID',
  `courier` VARCHAR(50) NOT NULL COMMENT '快递公司',
  `from_province` VARCHAR(50) NOT NULL COMMENT '出发省份',
  `to_province` VARCHAR(50) NOT NULL COMMENT '到达省份',
  `first_weight` DECIMAL(10,2) NOT NULL COMMENT '首重（kg）',
  `first_price` DECIMAL(10,2) NOT NULL COMMENT '首重价格',
  `continue_weight` DECIMAL(10,2) NOT NULL COMMENT '续重单位（kg）',
  `continue_price` DECIMAL(10,2) NOT NULL COMMENT '续重价格',
  `weight_ceiling` DECIMAL(10,2) NOT NULL COMMENT '重量上限（kg）',
  `min_profit` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '最低固定盈利额',
  `profit_ratio` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '盈利比例（%）',
  `third_provider` VARCHAR(50) NOT NULL COMMENT '第三方服务商',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活：0-未激活，1-激活',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_courier` (`courier`),
  INDEX `idx_from_province` (`from_province`),
  INDEX `idx_to_province` (`to_province`),
  INDEX `idx_weight_ceiling` (`weight_ceiling`),
  INDEX `idx_third_provider` (`third_provider`),
  INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='快递基础价格表';

-- 10. 第三方API日志表
CREATE TABLE IF NOT EXISTS `third_api_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `trace_id` VARCHAR(100) NOT NULL COMMENT '追踪ID',
  `api_type` TINYINT NOT NULL COMMENT 'API类型：1-打印，2-快递，3-微信',
  `api_name` VARCHAR(100) NOT NULL COMMENT 'API名称',
  `request_url` VARCHAR(500) NOT NULL COMMENT '请求URL',
  `request_method` VARCHAR(10) NOT NULL COMMENT '请求方法',
  `request_params` TEXT DEFAULT NULL COMMENT '请求参数（脱敏）',
  `response_data` TEXT DEFAULT NULL COMMENT '响应数据（脱敏）',
  `status_code` INT DEFAULT NULL COMMENT '响应状态码',
  `cost_time` BIGINT DEFAULT NULL COMMENT '耗时（ms）',
  `is_success` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否成功：0-失败，1-成功',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_trace_id` (`trace_id`),
  INDEX `idx_api_type` (`api_type`),
  INDEX `idx_api_name` (`api_name`),
  INDEX `idx_is_success` (`is_success`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方API日志表';

-- 11. 微信消息模板表
CREATE TABLE IF NOT EXISTS `wx_msg_template` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_id` VARCHAR(100) NOT NULL COMMENT '微信模板ID',
  `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `template_type` TINYINT NOT NULL COMMENT '模板类型：1-订单通知，2-支付通知，3-物流通知',
  `content` TEXT DEFAULT NULL COMMENT '模板内容',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活：0-未激活，1-激活',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_id` (`template_id`),
  INDEX `idx_template_type` (`template_type`),
  INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信消息模板表';

-- 12. 用户同意记录表
CREATE TABLE IF NOT EXISTS `user_consent_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `consent_type` TINYINT NOT NULL COMMENT '同意类型：1-隐私协议，2-服务条款，3-地理位置',
  `consent_status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '同意状态：0-拒绝，1-同意',
  `consent_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '同意时间',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `device_info` VARCHAR(200) DEFAULT NULL COMMENT '设备信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_consent_type` (`consent_type`),
  INDEX `idx_consent_time` (`consent_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户同意记录表';

-- 13. 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 14. 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 15. 菜单表
CREATE TABLE IF NOT EXISTS `menu` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父菜单ID',
  `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `menu_code` VARCHAR(50) NOT NULL COMMENT '菜单编码',
  `menu_url` VARCHAR(200) DEFAULT NULL COMMENT '菜单URL',
  `menu_icon` VARCHAR(50) DEFAULT NULL COMMENT '菜单图标',
  `menu_type` TINYINT NOT NULL COMMENT '菜单类型：1-目录，2-菜单，3-按钮',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_code` (`menu_code`),
  INDEX `idx_parent_id` (`parent_id`),
  INDEX `idx_menu_type` (`menu_type`),
  INDEX `idx_sort` (`sort`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- 16. 角色菜单关联表
CREATE TABLE IF NOT EXISTS `role_menu` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  INDEX `idx_role_id` (`role_id`),
  INDEX `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- 17. 管理员角色关联表
CREATE TABLE IF NOT EXISTS `admin_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `admin_id` BIGINT UNSIGNED NOT NULL COMMENT '管理员ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role` (`admin_id`, `role_id`),
  INDEX `idx_admin_id` (`admin_id`),
  INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员角色关联表';

-- 插入默认数据
-- 系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `description`, `is_active`) VALUES
('system_name', '优印达', 1, '系统名称', 1),
('system_version', '1.0.0', 1, '系统版本', 1),
('profit_ratio', '10.00', 2, '默认盈利比例（%）', 1),
('min_profit', '2.00', 2, '默认最低盈利额', 1),
('file_max_size', '50', 1, '文件最大大小（MB）', 1),
('file_expire_days', '3', 1, '文件过期天数', 1),
('order_timeout', '30', 1, '订单支付超时时间（分钟）', 1),
('refund_days', '7', 1, '退款期限（天）', 1);

-- 管理员
INSERT INTO `admin` (`username`, `password`, `name`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$7J6j3y4aK0m0b0c0d0e0f0g0h0i0j0k0l0m0n0o0p0q0r0s0t0u0v0w0x0y0z', '超级管理员', '13800138000', 'admin@youyinda.com', 1);

-- 角色
INSERT INTO `role` (`role_name`, `role_code`, `description`, `status`) VALUES
('超级管理员', 'super_admin', '拥有所有权限', 1),
('财务', 'finance', '财务权限', 1),
('客服', 'customer_service', '客服权限', 1),
('API配置管理员', 'api_config', 'API配置权限', 1);

-- 菜单
INSERT INTO `menu` (`parent_id`, `menu_name`, `menu_code`, `menu_url`, `menu_icon`, `menu_type`, `sort`, `status`) VALUES
(0, '系统管理', 'system_manage', '#', 'system', 1, 1, 1),
(1, '用户管理', 'user_manage', '/system/user', 'user', 2, 1, 1),
(1, '订单管理', 'order_manage', '/system/order', 'order', 2, 2, 1),
(1, '管理员管理', 'admin_manage', '/system/admin', 'admin', 2, 3, 1),
(1, '角色管理', 'role_manage', '/system/role', 'role', 2, 4, 1),
(1, '菜单管理', 'menu_manage', '/system/menu', 'menu', 2, 5, 1),
(0, '配置管理', 'config_manage', '#', 'config', 1, 2, 1),
(7, '系统配置', 'sys_config', '/config/system', 'sys', 2, 1, 1),
(7, '盈利规则', 'profit_rule', '/config/profit', 'profit', 2, 2, 1),
(7, '第三方接口', 'third_api', '/config/third', 'api', 2, 3, 1),
(0, '数据看板', 'dashboard', '/dashboard', 'dashboard', 2, 3, 1),
(0, '营销管理', 'marketing', '#', 'marketing', 1, 4, 1),
(11, '优惠券管理', 'coupon_manage', '/marketing/coupon', 'coupon', 2, 1, 1),
(11, '活动管理', 'activity_manage', '/marketing/activity', 'activity', 2, 2, 1);

-- 角色菜单关联
INSERT INTO `role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13);

-- 管理员角色关联
INSERT INTO `admin_role` (`admin_id`, `role_id`) VALUES
(1, 1);
