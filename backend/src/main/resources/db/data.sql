-- SmartWMS 初始数据
-- 版本: 1.0.0

USE smartwms;

-- ============================================
-- 系统管理模块初始数据
-- ============================================

-- 超级管理员 (密码: admin123 BCrypt加密)
INSERT INTO sys_user (id, username, password, real_name, phone, email, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '13800138000', 'admin@smartwms.com', 1),
(2, 'warehouse_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '仓库主管', '13800138001', 'manager@smartwms.com', 1),
(3, 'inbound_operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '入库员', '13800138002', 'inbound@smartwms.com', 1),
(4, 'outbound_operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '出库员', '13800138003', 'outbound@smartwms.com', 1),
(5, 'stocktake_operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '盘点员', '13800138004', 'stocktake@smartwms.com', 1);

-- 角色
INSERT INTO sys_role (id, role_name, role_code, description, status) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 1),
(2, '仓库主管', 'WAREHOUSE_MANAGER', '仓库管理权限', 1),
(3, '入库员', 'INBOUND_OPERATOR', '入库操作权限', 1),
(4, '出库员', 'OUTBOUND_OPERATOR', '出库操作权限', 1),
(5, '盘点员', 'STOCKTAKE_OPERATOR', '盘点操作权限', 1),
(6, '访客', 'GUEST', '只读权限', 1);

-- 权限
INSERT INTO sys_permission (id, permission_name, permission_code, parent_id, type, path, sort_order) VALUES
-- 一级菜单
(100, '工作台', 'dashboard', 0, 1, '/dashboard', 1),
(200, '仓储业务', 'warehouse_business', 0, 1, '', 2),
(300, '运营管理', 'operation_management', 0, 1, '', 3),
(400, '智能分析', 'intelligent_analysis', 0, 1, '', 4),
(500, '系统管理', 'system_management', 0, 1, '', 5),

-- 仓储业务子菜单
(201, '商品中心', 'product:center', 200, 1, '/product', 1),
(202, '仓库中心', 'warehouse:center', 200, 1, '/warehouse', 2),
(203, '入库管理', 'inbound:management', 200, 1, '/inbound', 3),
(204, '出库管理', 'outbound:management', 200, 1, '/outbound', 4),
(205, '库存中心', 'inventory:center', 200, 1, '/inventory', 5),
(206, '调拨管理', 'transfer:management', 200, 1, '/transfer', 6),
(207, '盘点管理', 'stocktake:management', 200, 1, '/stocktake', 7),

-- 运营管理子菜单
(301, '批次管理', 'batch:management', 300, 1, '/batch', 1),
(302, '任务中心', 'task:center', 300, 1, '/task', 2),
(303, '预警中心', 'alert:center', 300, 1, '/alert', 3),

-- 智能分析子菜单
(401, '数据分析', 'analytics', 400, 1, '/analytics', 1),
(402, 'AI智能助手', 'ai:assistant', 400, 1, '/ai', 2),

-- 系统管理子菜单
(501, '用户管理', 'user:management', 500, 1, '/system/user', 1),
(502, '角色权限', 'role:permission', 500, 1, '/system/role', 2),
(503, '操作日志', 'operation:log', 500, 1, '/system/log', 3),
(504, '系统配置', 'system:config', 500, 1, '/system/config', 4),

-- 按钮权限
(1001, '新增用户', 'user:create', 501, 2, '', 1),
(1002, '编辑用户', 'user:update', 501, 2, '', 2),
(1003, '删除用户', 'user:delete', 501, 2, '', 3),
(1004, '分配角色', 'user:assign', 501, 2, '', 4),

(2001, '新增商品', 'product:create', 201, 2, '', 1),
(2002, '编辑商品', 'product:update', 201, 2, '', 2),
(2003, '删除商品', 'product:delete', 201, 2, '', 3),

(3001, '新增仓库', 'warehouse:create', 202, 2, '', 1),
(3002, '编辑仓库', 'warehouse:update', 202, 2, '', 2),
(3003, '删除仓库', 'warehouse:delete', 202, 2, '', 3),

(4001, '创建入库单', 'inbound:create', 203, 2, '', 1),
(4002, '收货确认', 'inbound:receive', 203, 2, '', 2),
(4003, '上架确认', 'inbound:putaway', 203, 2, '', 3),
(4004, '审核入库', 'inbound:approve', 203, 2, '', 4),

(5001, '创建出库单', 'outbound:create', 204, 2, '', 1),
(5002, '审核出库', 'outbound:approve', 204, 2, '', 2),
(5003, '拣货确认', 'outbound:pick', 204, 2, '', 3),
(5004, '发货确认', 'outbound:ship', 204, 2, '', 4),

(6001, '库存调整', 'inventory:adjust', 205, 2, '', 1),
(6002, '库存查询', 'inventory:view', 205, 2, '', 2),

(7001, '创建调拨', 'transfer:create', 206, 2, '', 1),
(7002, '审核调拨', 'transfer:approve', 206, 2, '', 2),

(8001, '创建盘点', 'stocktake:create', 207, 2, '', 1),
(8002, '审核盘点', 'stocktake:approve', 207, 2, '', 2),
(8003, '库存调整', 'stocktake:adjust', 207, 2, '', 3);

-- 用户角色关联
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 3),
(4, 4, 4),
(5, 5, 5);

-- 角色权限关联 (超级管理员拥有所有权限)
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES
(1, 1, 100), (2, 1, 200), (3, 1, 300), (4, 1, 400), (5, 1, 500),
(6, 1, 201), (7, 1, 202), (8, 1, 203), (9, 1, 204), (10, 1, 205),
(11, 1, 206), (12, 1, 207), (13, 1, 301), (14, 1, 302), (15, 1, 303),
(16, 1, 401), (17, 1, 402), (18, 1, 501), (19, 1, 502), (20, 1, 503),
(21, 1, 504), (22, 1, 1001), (23, 1, 1002), (24, 1, 1003), (25, 1, 1004),
(26, 1, 2001), (27, 1, 2002), (28, 1, 2003), (29, 1, 3001), (30, 1, 3002),
(31, 1, 3003), (32, 1, 4001), (33, 1, 4002), (34, 1, 4003), (35, 1, 4004),
(36, 1, 5001), (37, 1, 5002), (38, 1, 5003), (39, 1, 5004), (40, 1, 6001),
(41, 1, 6002), (42, 1, 7001), (43, 1, 7002), (44, 1, 8001), (45, 1, 8002),
(46, 1, 8003);

-- 仓库主管权限
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES
(50, 2, 100), (51, 2, 200), (52, 2, 300), (53, 2, 400),
(54, 2, 201), (55, 2, 202), (56, 2, 203), (57, 2, 204),
(58, 2, 205), (59, 2, 206), (60, 2, 207), (61, 2, 301),
(62, 2, 302), (63, 2, 303), (64, 2, 401), (65, 2, 402),
(66, 2, 4004), (67, 2, 5002), (68, 2, 6001), (69, 2, 6002),
(70, 2, 7002), (71, 2, 8002), (72, 2, 8003);

-- ============================================
-- 商品中心初始数据
-- ============================================

-- 商品分类
INSERT INTO wms_category (id, category_name, parent_id, level) VALUES
(1, '电子产品', 0, 1),
(2, '工业零部件', 0, 1),
(3, '食品饮料', 0, 1),
(4, '办公用品', 0, 1),
(5, '电子配件', 1, 2),
(6, '电机', 2, 2),
(7, '轴承', 2, 2),
(8, '乳制品', 3, 2);

-- 供应商
INSERT INTO wms_supplier (id, supplier_code, supplier_name, contact_person, phone, status) VALUES
(1, 'SUP001', '深圳电子科技有限公司', '张经理', '13900139001', 1),
(2, 'SUP002', '上海精密机械有限公司', '李经理', '13900139002', 1),
(3, 'SUP003', '北京食品有限公司', '王经理', '13900139003', 1),
(4, 'SUP004', '广州工业设备有限公司', '赵经理', '13900139004', 1);

-- 商品
INSERT INTO wms_product (id, product_code, product_name, category_id, brand, status) VALUES
(1, 'PROD001', '伺服电机', 6, '西门子', 1),
(2, 'PROD002', '工业轴承', 7, 'SKF', 1),
(3, 'PROD003', '工业控制器', 5, '三菱', 1),
(4, 'PROD004', '纯牛奶', 8, '蒙牛', 1),
(5, 'PROD005', 'A4打印纸', 4, '得力', 1);

-- SKU
INSERT INTO wms_sku (id, sku_code, product_id, specification, unit, safety_stock, max_stock, outbound_strategy, status) VALUES
(1, 'SKU001-M20', 1, '20mm', '台', 10, 200, 'FIFO', 1),
(2, 'SKU001-M30', 1, '30mm', '台', 10, 200, 'FIFO', 1),
(3, 'SKU002-6204', 2, '6204-20mm', '个', 50, 1000, 'FIFO', 1),
(4, 'SKU002-6205', 2, '6205-25mm', '个', 50, 1000, 'FIFO', 1),
(5, 'SKU003-FX3U', 3, 'FX3U-32MT', '台', 5, 50, 'FIFO', 1),
(6, 'SKU004-ML250', 4, '250ml*24盒', '箱', 20, 500, 'FEFO', 1),
(7, 'SKU005-A4-500', 5, '500张/包', '包', 100, 2000, 'FIFO', 1);

-- 批次
INSERT INTO wms_batch (id, batch_no, sku_id, supplier_id, production_date, expiry_date, quality_status, quantity) VALUES
(1, 'BATCH20260801', 1, 2, '2026-08-01', NULL, 'PASSED', 100),
(2, 'BATCH20260802', 2, 2, '2026-08-02', NULL, 'PASSED', 80),
(3, 'BATCH20260801', 3, 2, '2026-08-01', NULL, 'PASSED', 50),
(4, 'BATCH20260801', 4, 2, '2026-08-01', NULL, 'PASSED', 30),
(5, 'BATCH20260701', 5, 1, '2026-07-01', NULL, 'PASSED', 20),
(6, 'BATCH20260801', 6, 3, '2026-08-01', '2026-09-01', 'PASSED', 100),
(7, 'BATCH20260802', 6, 3, '2026-08-02', '2026-09-02', 'PASSED', 80),
(8, 'BATCH20260801', 7, 4, '2026-08-01', NULL, 'PASSED', 200);

-- ============================================
-- 仓库中心初始数据
-- ============================================

-- 仓库
INSERT INTO wms_warehouse (id, warehouse_code, warehouse_name, address, contact_person, phone, total_capacity, status) VALUES
(1, 'WH001', '华东中心仓', '上海市浦东新区', '王仓管', '13800138010', 10000, 1),
(2, 'WH002', '华南配送仓', '广州市白云区', '李仓管', '13800138011', 8000, 1),
(3, 'WH003', '华北仓储中心', '北京市大兴区', '张仓管', '13800138012', 12000, 1);

-- 库区
INSERT INTO wms_zone (id, zone_code, zone_name, warehouse_id, zone_type, status) VALUES
(1, 'A', 'A区-电子区', 1, '常温', 1),
(2, 'B', 'B区-零部件区', 1, '常温', 1),
(3, 'C', 'C区-食品区', 1, '冷藏', 1),
(4, 'D', 'D区-大件区', 1, '常温', 1),
(5, 'E', 'A区-电子区', 2, '常温', 1),
(6, 'F', 'B区-零部件区', 2, '常温', 1),
(7, 'G', 'A区-综合区', 3, '常温', 1),
(8, 'H', 'B区-食品区', 3, '冷藏', 1);

-- 货架
INSERT INTO wms_rack (id, rack_code, rack_name, zone_id, floor_count, status) VALUES
(1, 'A01', 'A区01号货架', 1, 4, 1),
(2, 'A02', 'A区02号货架', 1, 4, 1),
(3, 'A03', 'A区03号货架', 1, 4, 1),
(4, 'B01', 'B区01号货架', 2, 4, 1),
(5, 'B02', 'B区02号货架', 2, 4, 1),
(6, 'C01', 'C区01号货架', 3, 3, 1),
(7, 'C02', 'C区02号货架', 3, 3, 1),
(8, 'D01', 'D区01号货架', 4, 3, 1),
(9, 'E01', 'E区01号货架', 5, 4, 1),
(10, 'F01', 'F区01号货架', 6, 4, 1),
(11, 'G01', 'G区01号货架', 7, 4, 1),
(12, 'H01', 'H区01号货架', 8, 3, 1);

-- 库位
INSERT INTO wms_location (id, location_code, rack_id, zone_id, warehouse_id, floor, position, max_weight, max_capacity, status) VALUES
-- A区货架
(1, 'A01-01-01', 1, 1, 1, 1, 1, 100.00, 50, 'PARTIAL'),
(2, 'A01-01-02', 1, 1, 1, 1, 2, 100.00, 50, 'PARTIAL'),
(3, 'A01-01-03', 1, 1, 1, 1, 3, 100.00, 50, 'EMPTY'),
(4, 'A01-02-01', 1, 1, 1, 2, 1, 100.00, 50, 'FULL'),
(5, 'A01-02-02', 1, 1, 1, 2, 2, 100.00, 50, 'PARTIAL'),
(6, 'A01-02-03', 1, 1, 1, 2, 3, 100.00, 50, 'EMPTY'),
(7, 'A01-03-01', 1, 1, 1, 3, 1, 100.00, 50, 'AVAILABLE'),
(8, 'A01-03-02', 1, 1, 1, 3, 2, 100.00, 50, 'AVAILABLE'),
(9, 'A02-01-01', 2, 1, 1, 1, 1, 100.00, 50, 'PARTIAL'),
(10, 'A02-01-02', 2, 1, 1, 1, 2, 100.00, 50, 'EMPTY'),
-- B区货架
(11, 'B01-01-01', 4, 2, 1, 1, 1, 200.00, 100, 'PARTIAL'),
(12, 'B01-01-02', 4, 2, 1, 1, 2, 200.00, 100, 'PARTIAL'),
(13, 'B01-01-03', 4, 2, 1, 1, 3, 200.00, 100, 'EMPTY'),
(14, 'B01-02-01', 4, 2, 1, 2, 1, 200.00, 100, 'FULL'),
(15, 'B01-02-02', 4, 2, 1, 2, 2, 200.00, 100, 'PARTIAL'),
-- C区货架
(16, 'C01-01-01', 6, 3, 1, 1, 1, 150.00, 80, 'PARTIAL'),
(17, 'C01-01-02', 6, 3, 1, 1, 2, 150.00, 80, 'PARTIAL'),
(18, 'C01-01-03', 6, 3, 1, 1, 3, 150.00, 80, 'EMPTY'),
-- D区货架
(19, 'D01-01-01', 8, 4, 1, 1, 1, 500.00, 200, 'PARTIAL'),
(20, 'D01-01-02', 8, 4, 1, 1, 2, 500.00, 200, 'EMPTY');

-- ============================================
-- 库存初始数据
-- ============================================

INSERT INTO wms_inventory (id, warehouse_id, location_id, sku_id, batch_id, total_qty, available_qty, locked_qty, damaged_qty, version) VALUES
(1, 1, 1, 1, 1, 50, 45, 5, 0, 1),
(2, 1, 2, 1, 1, 30, 28, 2, 0, 1),
(3, 1, 4, 2, 2, 40, 35, 5, 0, 1),
(4, 1, 5, 2, 2, 20, 18, 2, 0, 1),
(5, 1, 9, 3, 3, 30, 28, 2, 0, 1),
(6, 1, 11, 4, 4, 25, 23, 2, 0, 1),
(7, 1, 12, 4, 4, 15, 14, 1, 0, 1),
(8, 1, 16, 6, 6, 60, 55, 5, 0, 1),
(9, 1, 17, 6, 7, 40, 38, 2, 0, 1),
(10, 1, 19, 7, 8, 100, 95, 5, 0, 1);

-- 库存流水初始数据
INSERT INTO wms_inventory_transaction (id, transaction_no, sku_id, warehouse_id, location_id, batch_id, transaction_type, before_qty, change_qty, after_qty, source_order_no, user_id, username) VALUES
(1, 'TXN202609010001', 1, 1, 1, 1, 'PURCHASE_IN', 0, 50, 50, 'IN202609010001', 1, 'admin'),
(2, 'TXN202609010002', 1, 1, 2, 1, 'PURCHASE_IN', 0, 30, 30, 'IN202609010001', 1, 'admin'),
(3, 'TXN202609010003', 2, 1, 4, 2, 'PURCHASE_IN', 0, 40, 40, 'IN202609010002', 1, 'admin'),
(4, 'TXN202609010004', 2, 1, 5, 2, 'PURCHASE_IN', 0, 20, 20, 'IN202609010002', 1, 'admin'),
(5, 'TXN202609010005', 6, 1, 16, 6, 'PURCHASE_IN', 0, 60, 60, 'IN202609010003', 1, 'admin');

-- ============================================
-- 入库单初始数据
-- ============================================

INSERT INTO wms_inbound_order (id, order_no, order_type, warehouse_id, supplier_id, status, total_qty, received_qty, putaway_qty, created_by) VALUES
(1, 'IN202609050001', 'PURCHASE_IN', 1, 2, 'PUTAWAY', 100, 100, 80, 1),
(2, 'IN202609050002', 'PURCHASE_IN', 1, 1, 'RECEIVED', 50, 50, 0, 1),
(3, 'IN202609050003', 'PURCHASE_IN', 1, 3, 'RECEIVING', 80, 0, 0, 1);

INSERT INTO wms_inbound_order_item (id, order_id, sku_id, expected_qty, received_qty, putaway_qty, batch_no) VALUES
(1, 1, 1, 80, 80, 80, 'BATCH20260801'),
(2, 1, 2, 20, 20, 20, 'BATCH20260802'),
(3, 2, 5, 50, 50, 0, 'BATCH20260801'),
(4, 3, 6, 50, 0, 0, 'BATCH20260801'),
(5, 3, 7, 30, 0, 0, 'BATCH20260802');

-- ============================================
-- 出库单初始数据
-- ============================================

INSERT INTO wms_outbound_order (id, order_no, order_type, warehouse_id, customer_name, status, total_qty, picked_qty, shipped_qty, created_by) VALUES
(1, 'OUT202609050001', 'SALE_OUT', 1, '上海制造有限公司', 'COMPLETED', 30, 30, 30, 1),
(2, 'OUT202609050002', 'SALE_OUT', 1, '北京科技有限公司', 'PICKING', 20, 10, 0, 1),
(3, 'OUT202609050003', 'MATERIAL_OUT', 1, '生产车间', 'CREATED', 15, 0, 0, 1);

INSERT INTO wms_outbound_order_item (id, order_id, sku_id, required_qty, allocated_qty, picked_qty, locked_qty) VALUES
(1, 1, 1, 10, 10, 10, 10),
(2, 1, 3, 10, 10, 10, 10),
(3, 1, 6, 10, 10, 10, 10),
(4, 2, 2, 10, 10, 10, 10),
(5, 2, 4, 10, 10, 0, 10),
(6, 3, 4, 10, 0, 0, 0),
(7, 3, 7, 5, 0, 0, 0);

-- ============================================
-- 预警初始数据
-- ============================================

INSERT INTO wms_inventory_alert (id, alert_type, alert_level, sku_id, warehouse_id, title, content, current_value, threshold_value, suggestion) VALUES
(1, 'LOW_STOCK', 'HIGH', 5, 1, '伺服电机M30库存偏低', '当前可用库存低于安全库存', '18', '30', '建议立即补货'),
(2, 'EXPIRING', 'MEDIUM', 6, 1, '纯牛奶即将过期', '批次BATCH20260801将在30天内过期', '30天', '30天', '建议优先出库'),
(3, 'SLOW_MOVING', 'LOW', 7, 1, 'A4打印纸库存周转较慢', '该SKU近60天无出库记录', '60天', '60天', '建议检查是否需要调拨或促销');
