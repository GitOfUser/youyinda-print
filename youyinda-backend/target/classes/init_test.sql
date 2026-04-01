-- 优印达项目 - 简化数据库初始化脚本（用于联调测试）
-- 注意：这只是简化版本，生产环境请使用完整的init.sql

-- 创建数据库
CREATE DATABASE IF NOT EXISTS youyinda DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE youyinda;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  openid VARCHAR(100) NOT NULL UNIQUE COMMENT '微信OpenID',
  nick_name VARCHAR(100) COMMENT '昵称',
  avatar_url VARCHAR(500) COMMENT '头像URL',
  phone VARCHAR(20) COMMENT '手机号',
  gender INT COMMENT '性别：0-未知，1-男，2-女',
  city VARCHAR(50) COMMENT '城市',
  province VARCHAR(50) COMMENT '省份',
  country VARCHAR(50) COMMENT '国家',
  language VARCHAR(20) COMMENT '语言',
  last_login_time DATETIME COMMENT '最后登录时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT DEFAULT 0,
  INDEX idx_openid (openid),
  INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户地址表
CREATE TABLE IF NOT EXISTS user_address (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
  province VARCHAR(50) NOT NULL COMMENT '省份',
  city VARCHAR(50) NOT NULL COMMENT '城市',
  district VARCHAR(50) COMMENT '区县',
  detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
  latitude DECIMAL(10,6) COMMENT '纬度',
  longitude DECIMAL(10,6) COMMENT '经度',
  is_default TINYINT DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT DEFAULT 0,
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 订单主表
CREATE TABLE IF NOT EXISTS order_main (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  order_type INT NOT NULL COMMENT '订单类型：1-打印订单，2-快递订单',
  total_price DECIMAL(10,2) NOT NULL COMMENT '订单总价',
  actual_price DECIMAL(10,2) COMMENT '实际支付金额',
  status INT DEFAULT 1 COMMENT '订单状态：1-待支付，2-待打印/待揽收，3-待发货/运输中，4-待收货/派送中，5-已取消，6-已完成，7-售后中',
  pay_status INT DEFAULT 1 COMMENT '支付状态：1-未支付，2-已支付，3-支付失败，4-已退款',
  pay_type VARCHAR(20) COMMENT '支付方式：wechat-微信支付',
  pay_time DATETIME COMMENT '支付时间',
  pay_nonce VARCHAR(100) COMMENT '支付回调唯一标识',
  courier VARCHAR(50) COMMENT '快递公司',
  tracking_no VARCHAR(100) COMMENT '快递单号',
  address_id BIGINT COMMENT '收货地址ID',
  from_address_id BIGINT COMMENT '寄件地址ID',
  weight DECIMAL(10,2) COMMENT '重量（kg）',
  goods_type VARCHAR(100) COMMENT '物品类型',
  remark VARCHAR(500) COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT DEFAULT 0,
  INDEX idx_user_id (user_id),
  INDEX idx_order_no (order_no),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 订单详情表
CREATE TABLE IF NOT EXISTS order_detail (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  file_name VARCHAR(200) COMMENT '文件名称',
  file_url VARCHAR(500) COMMENT '文件URL',
  file_type VARCHAR(50) COMMENT '文件类型',
  file_size BIGINT COMMENT '文件大小（字节）',
  print_pages INT COMMENT '打印页数',
  print_copies INT COMMENT '打印份数',
  paper_type VARCHAR(50) COMMENT '纸张类型',
  color_type VARCHAR(50) COMMENT '颜色类型',
  print_side VARCHAR(50) COMMENT '打印面数',
  binding_type VARCHAR(50) COMMENT '装订类型',
  value_added_service VARCHAR(100) COMMENT '增值服务',
  unit_price DECIMAL(10,2) COMMENT '单价',
  subtotal DECIMAL(10,2) COMMENT '小计',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT DEFAULT 0,
  INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- 打印基础价格表
CREATE TABLE IF NOT EXISTS print_base_price (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  paper_type VARCHAR(50) NOT NULL COMMENT '纸张类型',
  color_type VARCHAR(50) NOT NULL COMMENT '颜色类型',
  print_side VARCHAR(50) NOT NULL COMMENT '打印面数',
  base_price DECIMAL(10,2) NOT NULL COMMENT '基础价格',
  min_price DECIMAL(10,2) COMMENT '最低限价',
  profit_ratio DECIMAL(5,2) COMMENT '盈利比例（%）',
  third_provider VARCHAR(100) COMMENT '第三方服务商',
  is_active TINYINT DEFAULT 1 COMMENT '是否激活：0-未激活，1-激活',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT DEFAULT 0,
  INDEX idx_paper_type (paper_type),
  INDEX idx_color_type (color_type),
  INDEX idx_print_side (print_side)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印基础价格表';

-- 快递基础价格表
CREATE TABLE IF NOT EXISTS express_base_price (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  courier VARCHAR(50) NOT NULL COMMENT '快递公司',
  from_province VARCHAR(50) NOT NULL COMMENT '寄件省份',
  to_province VARCHAR(50) NOT NULL COMMENT '收件省份',
  first_weight DECIMAL(10,2) NOT NULL COMMENT '首重（kg）',
  first_price DECIMAL(10,2) NOT NULL COMMENT '首重价格',
  continue_weight DECIMAL(10,2) NOT NULL COMMENT '续重单位（kg）',
  continue_price DECIMAL(10,2) NOT NULL COMMENT '续重价格',
  weight_ceiling DECIMAL(10,2) COMMENT '重量上限（kg）',
  min_profit DECIMAL(10,2) COMMENT '最低固定盈利额',
  profit_ratio DECIMAL(5,2) COMMENT '盈利比例（%）',
  third_provider VARCHAR(100) COMMENT '第三方服务商',
  is_active TINYINT DEFAULT 1 COMMENT '是否激活：0-未激活，1-激活',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_delete TINYINT DEFAULT 0,
  INDEX idx_courier (courier),
  INDEX idx_from_province (from_province),
  INDEX idx_to_province (to_province)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递基础价格表';

-- 插入测试数据
INSERT INTO print_base_price (paper_type, color_type, print_side, base_price, min_price, profit_ratio, third_provider) VALUES
('A4', '黑白', '单面', 0.20, 0.15, 50.00, '小猴云印'),
('A4', '黑白', '双面', 0.40, 0.30, 50.00, '小猴云印'),
('A4', '彩色', '单面', 0.80, 0.60, 50.00, '小猴云印'),
('A4', '彩色', '双面', 1.60, 1.20, 50.00, '小猴云印');

INSERT INTO express_base_price (courier, from_province, to_province, first_weight, first_price, continue_weight, continue_price, weight_ceiling, min_profit, profit_ratio, third_provider) VALUES
('顺丰速运', '广东省', '北京市', 1.00, 12.00, 1.00, 5.00, 30.00, 2.00, 20.00, '快递100'),
('顺丰速运', '广东省', '上海市', 1.00, 12.00, 1.00, 5.00, 30.00, 2.00, 20.00, '快递100'),
('中通快递', '广东省', '北京市', 1.00, 8.00, 1.00, 3.00, 30.00, 1.50, 15.00, '快递100'),
('中通快递', '广东省', '上海市', 1.00, 8.00, 1.00, 3.00, 30.00, 1.50, 15.00, '快递100');
