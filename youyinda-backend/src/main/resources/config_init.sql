USE `youyinda`;

INSERT INTO `sys_config` (`config_key`, `config_value`, `config_desc`, `group_name`) VALUES
('print_profit_rate', '0.15', '打印全局盈利比例（如0.15表示15%）', 'profit'),
('print_min_unit_price', '0.1', '打印单页最低限价（元）', 'profit'),
('express_profit_rate', '0.15', '快递全局盈利比例（如0.15表示15%）', 'profit'),
('express_min_fixed_profit', '1.0', '快递最低固定盈利额（元）', 'profit'),
('price_cache_expire_time', '3600', '价格缓存过期时间（秒）', 'cache'),
('print_default_binding_fee', '0', '默认装订服务费（元）', 'print'),
('print_default_value_added_fee', '0', '默认增值服务费（元）', 'print'),
('express_default_first_weight', '1.0', '快递默认首重（kg）', 'express'),
('express_default_continue_weight', '1.0', '快递默认续重单位（kg）', 'express');

INSERT INTO `third_api_config` (`provider_code`, `provider_name`, `api_type`, `api_url`, `app_id`, `app_secret`, `timeout`, `status`) VALUES
('print_provider_001', '小猴云印', 'print', 'https://api.print-provider.com', 'your_print_app_id', 'your_print_app_secret', 3000, 1),
('express_provider_001', '快递100', 'express', 'https://api.express-provider.com', 'your_express_app_id', 'your_express_app_secret', 3000, 1);

INSERT INTO `print_base_price` (`paper_type`, `color_type`, `single_double`, `base_price`, `min_order_price`, `provider_code`, `status`) VALUES
('A4', 'black', 'single', 0.10, 1.00, 'print_provider_001', 1),
('A4', 'black', 'double', 0.08, 1.00, 'print_provider_001', 1),
('A4', 'color', 'single', 0.50, 5.00, 'print_provider_001', 1),
('A4', 'color', 'double', 0.40, 5.00, 'print_provider_001', 1),
('A3', 'black', 'single', 0.20, 2.00, 'print_provider_001', 1),
('A3', 'black', 'double', 0.16, 2.00, 'print_provider_001', 1),
('A3', 'color', 'single', 1.00, 10.00, 'print_provider_001', 1),
('A3', 'color', 'double', 0.80, 10.00, 'print_provider_001', 1);

INSERT INTO `express_base_price` (`express_code`, `express_name`, `sender_province`, `sender_city`, `receiver_province`, `receiver_city`, `first_weight`, `first_price`, `continue_weight`, `continue_price`, `provider_code`, `status`) VALUES
('SF', '顺丰速运', '广东省', '深圳市', '广东省', '广州市', 1.00, 12.00, 1.00, 2.00, 'express_provider_001', 1),
('SF', '顺丰速运', '广东省', '深圳市', '北京市', '北京市', 1.00, 22.00, 1.00, 10.00, 'express_provider_001', 1),
('YTO', '圆通速递', '广东省', '深圳市', '广东省', '广州市', 1.00, 8.00, 1.00, 1.50, 'express_provider_001', 1),
('YTO', '圆通速递', '广东省', '深圳市', '北京市', '北京市', 1.00, 15.00, 1.00, 6.00, 'express_provider_001', 1),
('ZTO', '中通快递', '广东省', '深圳市', '广东省', '广州市', 1.00, 7.00, 1.00, 1.20, 'express_provider_001', 1),
('ZTO', '中通快递', '广东省', '深圳市', '北京市', '北京市', 1.00, 14.00, 1.00, 5.00, 'express_provider_001', 1),
('STO', '申通快递', '广东省', '深圳市', '广东省', '广州市', 1.00, 7.50, 1.00, 1.30, 'express_provider_001', 1),
('STO', '申通快递', '广东省', '深圳市', '北京市', '北京市', 1.00, 14.50, 1.00, 5.50, 'express_provider_001', 1),
('YD', '韵达快递', '广东省', '深圳市', '广东省', '广州市', 1.00, 6.50, 1.00, 1.00, 'express_provider_001', 1),
('YD', '韵达快递', '广东省', '深圳市', '北京市', '北京市', 1.00, 13.00, 1.00, 4.50, 'express_provider_001', 1),
('JD', '京东物流', '广东省', '深圳市', '广东省', '广州市', 1.00, 10.00, 1.00, 1.80, 'express_provider_001', 1),
('JD', '京东物流', '广东省', '深圳市', '北京市', '北京市', 1.00, 18.00, 1.00, 8.00, 'express_provider_001', 1);
