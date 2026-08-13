-- ============================================================
-- 66印 / 快递100 第三方对接初始化数据
-- 说明：本脚本用于初始化 66印 打印服务商配置、打印基础价格，
--       以及快递100 的快递基础价格。请在数据库已建表后执行。
-- ============================================================

-- 66印 第三方 API 配置
INSERT INTO third_api_config (provider_code, provider_name, api_type, api_url, app_id, app_secret, timeout, status) VALUES
('liuliuyin', '66印', 'print', 'https://api.66yin.com', '', '', 5000, 1);

-- 小猴云印 第三方 API 配置（保留，作为 66印 的降级服务商）
INSERT INTO third_api_config (provider_code, provider_name, api_type, api_url, app_id, app_secret, timeout, status) VALUES
('xiaohou', '小猴云印', 'print', 'https://api.print-provider.com', '', '', 3000, 1);

-- 快递100 第三方 API 配置
INSERT INTO third_api_config (provider_code, provider_name, api_type, api_url, app_id, app_secret, timeout, status) VALUES
('kuaidi100', '快递100·百递云', 'express', 'https://api.kuaidi100.com', '', '', 3000, 1);

-- 66印 打印基础价格（比小猴云印更有竞争力）
INSERT INTO print_base_price (paper_type, color_type, print_side, base_price, min_price, profit_ratio, third_provider, provider_code, single_double, is_active) VALUES
('A4', 1, '单面', 0.08, 0.15, 30, '66印', 'liuliuyin', 'single', 1),
('A4', 1, '双面', 0.10, 0.18, 30, '66印', 'liuliuyin', 'double', 1),
('A4', 2, '单面', 0.30, 0.50, 30, '66印', 'liuliuyin', 'single', 1),
('A4', 2, '双面', 0.35, 0.60, 30, '66印', 'liuliuyin', 'double', 1),
('A3', 1, '单面', 0.15, 0.25, 30, '66印', 'liuliuyin', 'single', 1),
('A3', 2, '单面', 0.60, 1.00, 30, '66印', 'liuliuyin', 'single', 1),
('照片纸', 2, '单面', 0.80, 1.50, 30, '66印', 'liuliuyin', 'single', 1);

-- 小猴云印 打印基础价格（降级服务商，价格略高）
INSERT INTO print_base_price (paper_type, color_type, print_side, base_price, min_price, profit_ratio, third_provider, provider_code, single_double, is_active) VALUES
('A4', 1, '单面', 0.10, 0.20, 30, '小猴云印', 'xiaohou', 'single', 1),
('A4', 1, '双面', 0.12, 0.22, 30, '小猴云印', 'xiaohou', 'double', 1),
('A4', 2, '单面', 0.35, 0.60, 30, '小猴云印', 'xiaohou', 'single', 1),
('A4', 2, '双面', 0.40, 0.70, 30, '小猴云印', 'xiaohou', 'double', 1),
('A3', 1, '单面', 0.20, 0.30, 30, '小猴云印', 'xiaohou', 'single', 1),
('A3', 2, '单面', 0.70, 1.20, 30, '小猴云印', 'xiaohou', 'single', 1),
('照片纸', 2, '单面', 0.90, 1.80, 30, '小猴云印', 'xiaohou', 'single', 1);

-- 快递100 基础价格
INSERT INTO express_base_price (courier, from_province, to_province, first_weight, first_price, continue_weight, continue_price, profit_ratio, min_profit, third_provider, is_active) VALUES
('yuantong', '广东', '广东', 1.0, 6.0, 1.0, 2.0, 20, 1.5, '快递100', 1),
('yuantong', '广东', '北京', 1.0, 10.0, 1.0, 5.0, 20, 2.0, '快递100', 1),
('zhongtong', '广东', '广东', 1.0, 5.5, 1.0, 1.8, 20, 1.5, '快递100', 1),
('shunfeng', '广东', '广东', 1.0, 12.0, 1.0, 3.0, 15, 2.0, '快递100', 1);
