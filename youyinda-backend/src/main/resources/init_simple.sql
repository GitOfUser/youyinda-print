SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `youyinda` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `youyinda`;

-- 用户信息表
CREATE TABLE IF NOT EXISTS `user_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `openid` varchar(100) NOT NULL,
  `nickname` varchar(100) DEFAULT NULL,
  `avatar` varchar(500) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 订单主表
CREATE TABLE IF NOT EXISTS `order_main` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL,
  `user_id` bigint NOT NULL,
  `order_type` varchar(20) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `pay_amount` decimal(10,2) NOT NULL,
  `order_status` varchar(20) NOT NULL,
  `pay_status` tinyint DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 打印基础价格表
CREATE TABLE IF NOT EXISTS `print_base_price` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `paper_type` varchar(20) NOT NULL,
  `color_type` varchar(20) NOT NULL,
  `single_double` varchar(20) NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` tinyint DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 快递基础价格表
CREATE TABLE IF NOT EXISTS `express_base_price` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `express_code` varchar(20) NOT NULL,
  `express_name` varchar(50) NOT NULL,
  `sender_province` varchar(50) NOT NULL,
  `receiver_province` varchar(50) NOT NULL,
  `first_weight` decimal(10,2) NOT NULL,
  `first_price` decimal(10,2) NOT NULL,
  `continue_weight` decimal(10,2) NOT NULL,
  `continue_price` decimal(10,2) NOT NULL,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` tinyint DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) NOT NULL,
  `config_value` text NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 第三方API配置表
CREATE TABLE IF NOT EXISTS `third_api_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `provider_code` varchar(50) NOT NULL,
  `provider_name` varchar(100) NOT NULL,
  `api_type` varchar(20) NOT NULL,
  `api_url` varchar(500) NOT NULL,
  `app_id` varchar(200) DEFAULT NULL,
  `app_secret` varchar(200) DEFAULT NULL,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` tinyint DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 插入基础数据
INSERT INTO `print_base_price` (`paper_type`, `color_type`, `single_double`, `base_price`) VALUES
('A4', 'black', 'single', 0.10),
('A4', 'black', 'double', 0.15),
('A4', 'color', 'single', 0.50),
('A4', 'color', 'double', 0.80);

INSERT INTO `express_base_price` (`express_code`, `express_name`, `sender_province`, `receiver_province`, `first_weight`, `first_price`, `continue_weight`, `continue_price`) VALUES
('SF', '顺丰速运', '北京市', '上海市', 1.0, 12.00, 0.5, 3.00),
('ZTO', '中通快递', '北京市', '上海市', 1.0, 8.00, 0.5, 2.00);

SET FOREIGN_KEY_CHECKS = 1;