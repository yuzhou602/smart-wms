-- SmartWMS 数据库表结构
-- 版本: 1.0.0

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS smartwms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smartwms;

-- ============================================
-- 系统管理模块
-- ============================================

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT NOT NULL COMMENT '主键ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    type TINYINT DEFAULT 1 COMMENT '类型 1-菜单 2-按钮',
    path VARCHAR(200) COMMENT '路由路径',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id BIGINT NOT NULL COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 操作日志表
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    id BIGINT NOT NULL COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    module VARCHAR(50) COMMENT '模块',
    operation VARCHAR(50) COMMENT '操作',
    target VARCHAR(200) COMMENT '目标',
    method VARCHAR(200) COMMENT '请求方法',
    url VARCHAR(500) COMMENT '请求URL',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    result TINYINT COMMENT '结果 0-失败 1-成功',
    error_msg TEXT COMMENT '错误信息',
    duration BIGINT COMMENT '耗时(ms)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 通知表
DROP TABLE IF EXISTS sys_notification;
CREATE TABLE sys_notification (
    id BIGINT NOT NULL COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    type VARCHAR(20) COMMENT '类型',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- ============================================
-- 商品中心模块
-- ============================================

-- 商品分类表
DROP TABLE IF EXISTS wms_category;
CREATE TABLE wms_category (
    id BIGINT NOT NULL COMMENT '主键ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    level INT DEFAULT 1 COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 供应商表
DROP TABLE IF EXISTS wms_supplier;
CREATE TABLE wms_supplier (
    id BIGINT NOT NULL COMMENT '主键ID',
    supplier_code VARCHAR(50) NOT NULL COMMENT '供应商编码',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(200) COMMENT '地址',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_code (supplier_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 商品表
DROP TABLE IF EXISTS wms_product;
CREATE TABLE wms_product (
    id BIGINT NOT NULL COMMENT '主键ID',
    product_code VARCHAR(50) NOT NULL COMMENT '商品编码',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    category_id BIGINT COMMENT '分类ID',
    brand VARCHAR(100) COMMENT '品牌',
    description TEXT COMMENT '描述',
    image VARCHAR(255) COMMENT '图片',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (product_code),
    KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- SKU表
DROP TABLE IF EXISTS wms_sku;
CREATE TABLE wms_sku (
    id BIGINT NOT NULL COMMENT '主键ID',
    sku_code VARCHAR(50) NOT NULL COMMENT 'SKU编码',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    specification VARCHAR(200) COMMENT '规格',
    unit VARCHAR(20) COMMENT '单位',
    barcode VARCHAR(100) COMMENT '条码',
    weight DECIMAL(10,2) COMMENT '重量(kg)',
    volume DECIMAL(10,4) COMMENT '体积(m³)',
    safety_stock INT DEFAULT 0 COMMENT '安全库存',
    max_stock INT DEFAULT 0 COMMENT '最大库存',
    outbound_strategy VARCHAR(20) DEFAULT 'FIFO' COMMENT '出库策略 FIFO/FEFO/MANUAL',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sku_code (sku_code),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU表';

-- ============================================
-- 仓库中心模块
-- ============================================

-- 仓库表
DROP TABLE IF EXISTS wms_warehouse;
CREATE TABLE wms_warehouse (
    id BIGINT NOT NULL COMMENT '主键ID',
    warehouse_code VARCHAR(50) NOT NULL COMMENT '仓库编码',
    warehouse_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    address VARCHAR(200) COMMENT '地址',
    contact_person VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    total_capacity INT DEFAULT 0 COMMENT '总容量',
    used_capacity INT DEFAULT 0 COMMENT '已用容量',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouse_code (warehouse_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 库区表
DROP TABLE IF EXISTS wms_zone;
CREATE TABLE wms_zone (
    id BIGINT NOT NULL COMMENT '主键ID',
    zone_code VARCHAR(50) NOT NULL COMMENT '库区编码',
    zone_name VARCHAR(100) NOT NULL COMMENT '库区名称',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    zone_type VARCHAR(20) COMMENT '库区类型',
    temperature VARCHAR(20) COMMENT '温度要求',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_zone_code (zone_code),
    KEY idx_warehouse_id (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库区表';

-- 货架表
DROP TABLE IF EXISTS wms_rack;
CREATE TABLE wms_rack (
    id BIGINT NOT NULL COMMENT '主键ID',
    rack_code VARCHAR(50) NOT NULL COMMENT '货架编码',
    rack_name VARCHAR(100) NOT NULL COMMENT '货架名称',
    zone_id BIGINT NOT NULL COMMENT '库区ID',
    floor_count INT DEFAULT 1 COMMENT '层数',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rack_code (rack_code),
    KEY idx_zone_id (zone_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货架表';

-- 库位表
DROP TABLE IF EXISTS wms_location;
CREATE TABLE wms_location (
    id BIGINT NOT NULL COMMENT '主键ID',
    location_code VARCHAR(50) NOT NULL COMMENT '库位编码',
    rack_id BIGINT NOT NULL COMMENT '货架ID',
    zone_id BIGINT NOT NULL COMMENT '库区ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    floor INT COMMENT '层',
    position INT COMMENT '位',
    location_type VARCHAR(20) COMMENT '库位类型',
    max_weight DECIMAL(10,2) COMMENT '最大重量(kg)',
    max_capacity INT COMMENT '最大容量',
    used_capacity INT DEFAULT 0 COMMENT '已用容量',
    status VARCHAR(20) DEFAULT 'EMPTY' COMMENT '状态',
    is_disabled TINYINT DEFAULT 0 COMMENT '是否禁用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_location_code (location_code),
    KEY idx_rack_id (rack_id),
    KEY idx_zone_id (zone_id),
    KEY idx_warehouse_id (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位表';

-- ============================================
-- 批次管理模块
-- ============================================

-- 批次表
DROP TABLE IF EXISTS wms_batch;
CREATE TABLE wms_batch (
    id BIGINT NOT NULL COMMENT '主键ID',
    batch_no VARCHAR(50) NOT NULL COMMENT '批次号',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    supplier_id BIGINT COMMENT '供应商ID',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    quality_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '质检状态',
    quantity INT DEFAULT 0 COMMENT '批次数量',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sku_batch (sku_id, batch_no),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次表';

-- ============================================
-- 库存核心模块
-- ============================================

-- 库存表
DROP TABLE IF EXISTS wms_inventory;
CREATE TABLE wms_inventory (
    id BIGINT NOT NULL COMMENT '主键ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    location_id BIGINT NOT NULL COMMENT '库位ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    batch_id BIGINT COMMENT '批次ID',
    total_qty INT DEFAULT 0 COMMENT '总库存',
    available_qty INT DEFAULT 0 COMMENT '可用库存',
    locked_qty INT DEFAULT 0 COMMENT '锁定库存',
    damaged_qty INT DEFAULT 0 COMMENT '损坏库存',
    version INT DEFAULT 0 COMMENT '乐观锁版本',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventory (warehouse_id, location_id, sku_id, batch_id),
    KEY idx_warehouse_id (warehouse_id),
    KEY idx_location_id (location_id),
    KEY idx_sku_id (sku_id),
    KEY idx_batch_id (batch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 库存流水表
DROP TABLE IF EXISTS wms_inventory_transaction;
CREATE TABLE wms_inventory_transaction (
    id BIGINT NOT NULL COMMENT '主键ID',
    transaction_no VARCHAR(50) NOT NULL COMMENT '流水号',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    location_id BIGINT COMMENT '库位ID',
    batch_id BIGINT COMMENT '批次ID',
    transaction_type VARCHAR(30) NOT NULL COMMENT '流水类型',
    before_qty INT DEFAULT 0 COMMENT '操作前库存',
    change_qty INT NOT NULL COMMENT '变更数量',
    after_qty INT DEFAULT 0 COMMENT '操作后库存',
    source_order_no VARCHAR(50) COMMENT '来源单据号',
    user_id BIGINT COMMENT '操作人ID',
    username VARCHAR(50) COMMENT '操作人',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_transaction_no (transaction_no),
    KEY idx_sku_id (sku_id),
    KEY idx_warehouse_id (warehouse_id),
    KEY idx_source_order_no (source_order_no),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';

-- 库存锁定表
DROP TABLE IF EXISTS wms_inventory_lock;
CREATE TABLE wms_inventory_lock (
    id BIGINT NOT NULL COMMENT '主键ID',
    lock_no VARCHAR(50) NOT NULL COMMENT '锁定单号',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    location_id BIGINT COMMENT '库位ID',
    batch_id BIGINT COMMENT '批次ID',
    locked_qty INT NOT NULL COMMENT '锁定数量',
    source_order_no VARCHAR(50) COMMENT '来源单据号',
    source_order_type VARCHAR(20) COMMENT '来源单据类型',
    status VARCHAR(20) DEFAULT 'LOCKED' COMMENT '状态 LOCKED/RELEASED',
    user_id BIGINT COMMENT '操作人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_lock_no (lock_no),
    KEY idx_sku_id (sku_id),
    KEY idx_warehouse_id (warehouse_id),
    KEY idx_source_order_no (source_order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存锁定表';

-- ============================================
-- 入库管理模块
-- ============================================

-- 入库单表
DROP TABLE IF EXISTS wms_inbound_order;
CREATE TABLE wms_inbound_order (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL COMMENT '入库单号',
    order_type VARCHAR(20) NOT NULL COMMENT '入库类型',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    supplier_id BIGINT COMMENT '供应商ID',
    status VARCHAR(20) DEFAULT 'CREATED' COMMENT '状态',
    total_qty INT DEFAULT 0 COMMENT '总数量',
    received_qty INT DEFAULT 0 COMMENT '已收货数量',
    putaway_qty INT DEFAULT 0 COMMENT '已上架数量',
    expected_date DATE COMMENT '预计到货日期',
    actual_date DATE COMMENT '实际到货日期',
    remark VARCHAR(500) COMMENT '备注',
    created_by BIGINT COMMENT '创建人',
    approved_by BIGINT COMMENT '审批人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_warehouse_id (warehouse_id),
    KEY idx_supplier_id (supplier_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- 入库单明细表
DROP TABLE IF EXISTS wms_inbound_order_item;
CREATE TABLE wms_inbound_order_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '入库单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    expected_qty INT NOT NULL COMMENT '预期数量',
    received_qty INT DEFAULT 0 COMMENT '已收货数量',
    putaway_qty INT DEFAULT 0 COMMENT '已上架数量',
    batch_no VARCHAR(50) COMMENT '批次号',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单明细表';

-- 收货记录表
DROP TABLE IF EXISTS wms_receipt;
CREATE TABLE wms_receipt (
    id BIGINT NOT NULL COMMENT '主键ID',
    receipt_no VARCHAR(50) NOT NULL COMMENT '收货单号',
    order_id BIGINT NOT NULL COMMENT '入库单ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    total_qty INT DEFAULT 0 COMMENT '总数量',
    qualified_qty INT DEFAULT 0 COMMENT '合格数量',
    unqualified_qty INT DEFAULT 0 COMMENT '不合格数量',
    receiver_id BIGINT COMMENT '收货人ID',
    received_at DATETIME COMMENT '收货时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_receipt_no (receipt_no),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货记录表';

-- 收货明细表
DROP TABLE IF EXISTS wms_receipt_item;
CREATE TABLE wms_receipt_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    receipt_id BIGINT NOT NULL COMMENT '收货单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    expected_qty INT NOT NULL COMMENT '预期数量',
    actual_qty INT DEFAULT 0 COMMENT '实际数量',
    qualified_qty INT DEFAULT 0 COMMENT '合格数量',
    unqualified_qty INT DEFAULT 0 COMMENT '不合格数量',
    quality_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '质检状态',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_receipt_id (receipt_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货明细表';

-- 上架任务表
DROP TABLE IF EXISTS wms_putaway_task;
CREATE TABLE wms_putaway_task (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_no VARCHAR(50) NOT NULL COMMENT '任务号',
    order_id BIGINT NOT NULL COMMENT '入库单ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    total_qty INT DEFAULT 0 COMMENT '总数量',
    completed_qty INT DEFAULT 0 COMMENT '已完成数量',
    assignee_id BIGINT COMMENT '执行人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_no (task_no),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上架任务表';

-- 上架任务明细表
DROP TABLE IF EXISTS wms_putaway_task_item;
CREATE TABLE wms_putaway_task_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    batch_id BIGINT COMMENT '批次ID',
    qty INT NOT NULL COMMENT '数量',
    source_location_id BIGINT COMMENT '源库位ID',
    target_location_id BIGINT COMMENT '目标库位ID',
    recommend_location_id BIGINT COMMENT '推荐库位ID',
    recommend_score INT COMMENT '推荐评分',
    recommend_reason VARCHAR(500) COMMENT '推荐原因',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    completed_at DATETIME COMMENT '完成时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上架任务明细表';

-- ============================================
-- 出库管理模块
-- ============================================

-- 出库单表
DROP TABLE IF EXISTS wms_outbound_order;
CREATE TABLE wms_outbound_order (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL COMMENT '出库单号',
    order_type VARCHAR(20) NOT NULL COMMENT '出库类型',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    status VARCHAR(20) DEFAULT 'CREATED' COMMENT '状态',
    total_qty INT DEFAULT 0 COMMENT '总数量',
    picked_qty INT DEFAULT 0 COMMENT '已拣货数量',
    shipped_qty INT DEFAULT 0 COMMENT '已发货数量',
    expected_date DATE COMMENT '预计发货日期',
    actual_date DATE COMMENT '实际发货日期',
    remark VARCHAR(500) COMMENT '备注',
    created_by BIGINT COMMENT '创建人',
    approved_by BIGINT COMMENT '审批人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_warehouse_id (warehouse_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- 出库单明细表
DROP TABLE IF EXISTS wms_outbound_order_item;
CREATE TABLE wms_outbound_order_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '出库单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    required_qty INT NOT NULL COMMENT '需求数量',
    allocated_qty INT DEFAULT 0 COMMENT '已分配数量',
    picked_qty INT DEFAULT 0 COMMENT '已拣货数量',
    locked_qty INT DEFAULT 0 COMMENT '已锁定数量',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细表';

-- 拣货任务表
DROP TABLE IF EXISTS wms_picking_task;
CREATE TABLE wms_picking_task (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_no VARCHAR(50) NOT NULL COMMENT '任务号',
    order_id BIGINT NOT NULL COMMENT '出库单ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    total_qty INT DEFAULT 0 COMMENT '总数量',
    picked_qty INT DEFAULT 0 COMMENT '已拣货数量',
    assignee_id BIGINT COMMENT '执行人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_no (task_no),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拣货任务表';

-- 拣货任务明细表
DROP TABLE IF EXISTS wms_picking_task_item;
CREATE TABLE wms_picking_task_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    batch_id BIGINT COMMENT '批次ID',
    required_qty INT NOT NULL COMMENT '需求数量',
    picked_qty INT DEFAULT 0 COMMENT '已拣货数量',
    location_id BIGINT COMMENT '库位ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    completed_at DATETIME COMMENT '完成时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拣货任务明细表';

-- ============================================
-- 调拨管理模块
-- ============================================

-- 调拨单表
DROP TABLE IF EXISTS wms_transfer_order;
CREATE TABLE wms_transfer_order (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL COMMENT '调拨单号',
    source_warehouse_id BIGINT NOT NULL COMMENT '源仓库ID',
    target_warehouse_id BIGINT NOT NULL COMMENT '目标仓库ID',
    status VARCHAR(20) DEFAULT 'CREATED' COMMENT '状态',
    total_qty INT DEFAULT 0 COMMENT '总数量',
    shipped_qty INT DEFAULT 0 COMMENT '已发出数量',
    received_qty INT DEFAULT 0 COMMENT '已接收数量',
    remark VARCHAR(500) COMMENT '备注',
    created_by BIGINT COMMENT '创建人',
    approved_by BIGINT COMMENT '审批人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_source_warehouse_id (source_warehouse_id),
    KEY idx_target_warehouse_id (target_warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调拨单表';

-- 调拨单明细表
DROP TABLE IF EXISTS wms_transfer_order_item;
CREATE TABLE wms_transfer_order_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '调拨单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    batch_id BIGINT COMMENT '批次ID',
    qty INT NOT NULL COMMENT '数量',
    shipped_qty INT DEFAULT 0 COMMENT '已发出数量',
    received_qty INT DEFAULT 0 COMMENT '已接收数量',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调拨单明细表';

-- ============================================
-- 盘点管理模块
-- ============================================

-- 盘点计划表
DROP TABLE IF EXISTS wms_stocktake;
CREATE TABLE wms_stocktake (
    id BIGINT NOT NULL COMMENT '主键ID',
    stocktake_no VARCHAR(50) NOT NULL COMMENT '盘点单号',
    stocktake_type VARCHAR(20) NOT NULL COMMENT '盘点类型',
    warehouse_id BIGINT COMMENT '仓库ID',
    zone_id BIGINT COMMENT '库区ID',
    status VARCHAR(20) DEFAULT 'CREATED' COMMENT '状态',
    total_items INT DEFAULT 0 COMMENT '盘点项总数',
    completed_items INT DEFAULT 0 COMMENT '已完成项数',
    difference_items INT DEFAULT 0 COMMENT '差异项数',
    remark VARCHAR(500) COMMENT '备注',
    created_by BIGINT COMMENT '创建人',
    approved_by BIGINT COMMENT '审批人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_stocktake_no (stocktake_no),
    KEY idx_warehouse_id (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点计划表';

-- 盘点明细表
DROP TABLE IF EXISTS wms_stocktake_item;
CREATE TABLE wms_stocktake_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    stocktake_id BIGINT NOT NULL COMMENT '盘点ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    location_id BIGINT COMMENT '库位ID',
    batch_id BIGINT COMMENT '批次ID',
    system_qty INT DEFAULT 0 COMMENT '系统库存',
    actual_qty INT COMMENT '实际库存',
    difference_qty INT DEFAULT 0 COMMENT '差异数量',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_stocktake_id (stocktake_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点明细表';

-- ============================================
-- 任务中心模块
-- ============================================

-- 任务表
DROP TABLE IF EXISTS wms_task;
CREATE TABLE wms_task (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_no VARCHAR(50) NOT NULL COMMENT '任务号',
    task_type VARCHAR(20) NOT NULL COMMENT '任务类型',
    source_order_no VARCHAR(50) COMMENT '来源单据号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    priority INT DEFAULT 0 COMMENT '优先级',
    assignee_id BIGINT COMMENT '执行人ID',
    assignee_name VARCHAR(50) COMMENT '执行人姓名',
    started_at DATETIME COMMENT '开始时间',
    completed_at DATETIME COMMENT '完成时间',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_no (task_no),
    KEY idx_task_type (task_type),
    KEY idx_status (status),
    KEY idx_assignee_id (assignee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- ============================================
-- 预警中心模块
-- ============================================

-- 预警表
DROP TABLE IF EXISTS wms_inventory_alert;
CREATE TABLE wms_inventory_alert (
    id BIGINT NOT NULL COMMENT '主键ID',
    alert_type VARCHAR(30) NOT NULL COMMENT '预警类型',
    alert_level VARCHAR(20) NOT NULL COMMENT '预警级别',
    sku_id BIGINT COMMENT 'SKU ID',
    warehouse_id BIGINT COMMENT '仓库ID',
    location_id BIGINT COMMENT '库位ID',
    batch_id BIGINT COMMENT '批次ID',
    title VARCHAR(200) NOT NULL COMMENT '预警标题',
    content TEXT COMMENT '预警内容',
    current_value VARCHAR(100) COMMENT '当前值',
    threshold_value VARCHAR(100) COMMENT '阈值',
    suggestion VARCHAR(500) COMMENT '建议',
    is_handled TINYINT DEFAULT 0 COMMENT '是否已处理',
    handled_by BIGINT COMMENT '处理人',
    handled_at DATETIME COMMENT '处理时间',
    handle_result VARCHAR(500) COMMENT '处理结果',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_alert_type (alert_type),
    KEY idx_alert_level (alert_level),
    KEY idx_sku_id (sku_id),
    KEY idx_warehouse_id (warehouse_id),
    KEY idx_is_handled (is_handled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警表';

-- ============================================
-- AI 智能助手模块
-- ============================================

-- AI对话表
DROP TABLE IF EXISTS ai_conversation;
CREATE TABLE ai_conversation (
    id BIGINT NOT NULL COMMENT '主键ID',
    conversation_id VARCHAR(50) NOT NULL COMMENT '对话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) COMMENT '对话标题',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_conversation_id (conversation_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话表';

-- AI消息表
DROP TABLE IF EXISTS ai_message;
CREATE TABLE ai_message (
    id BIGINT NOT NULL COMMENT '主键ID',
    conversation_id VARCHAR(50) NOT NULL COMMENT '对话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色 user/assistant/system',
    content TEXT NOT NULL COMMENT '内容',
    tool_calls TEXT COMMENT '工具调用JSON',
    tokens INT DEFAULT 0 COMMENT 'Token数量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_conversation_id (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI消息表';

-- AI工具调用记录表
DROP TABLE IF EXISTS ai_tool_call;
CREATE TABLE ai_tool_call (
    id BIGINT NOT NULL COMMENT '主键ID',
    message_id BIGINT NOT NULL COMMENT '消息ID',
    tool_name VARCHAR(100) NOT NULL COMMENT '工具名称',
    arguments TEXT COMMENT '参数JSON',
    result TEXT COMMENT '结果JSON',
    duration BIGINT COMMENT '耗时(ms)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_message_id (message_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI工具调用记录表';

SET FOREIGN_KEY_CHECKS = 1;
