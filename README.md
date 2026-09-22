# SmartWMS - 智仓云智能仓储管理系统

AI-Driven Intelligent Warehouse Management System

## 项目介绍

SmartWMS 是一个现代化的企业级智能仓储管理系统，采用前后端分离架构，集成了 AI 智能助手功能，适用于中小型制造企业、电商仓储中心、企业内部物料仓库等场景。

### 核心特性

- **完整仓储业务链**：入库、上架、出库、拣货、盘点、调拨全流程
- **多级库存模型**：仓库 → 库区 → 货架 → 库位 → SKU → 批次
- **库存锁定机制**：支持库存预占、锁定、释放，避免超卖
- **FIFO/FEFO策略**：先进先出、先到期先出，支持多出库策略
- **智能库位推荐**：基于规则+权重的库位推荐算法
- **AI智能助手**：自然语言查询仓储数据，智能分析库存风险
- **数据驾驶舱**：实时监控仓库运营状态，多维度数据分析
- **RBAC权限体系**：完善的用户、角色、权限管理

## 技术架构

### 前端技术栈

- Vue 3 + TypeScript
- Vite
- Element Plus
- Tailwind CSS
- ECharts
- Pinia
- Vue Router

### 后端技术栈

- Java 21
- Spring Boot 3.2.x
- Spring Security + JWT
- MyBatis Plus
- MySQL 8.0
- Redis
- Springdoc OpenAPI

## 项目结构

```
smart-wms/
├── backend/                    # 后端项目
│   ├── src/main/java/com/smartwms/
│   │   ├── common/            # 通用模块（异常、响应、工具）
│   │   ├── security/          # 安全模块（JWT、认证）
│   │   ├── system/            # 系统管理（用户、角色、权限）
│   │   ├── product/           # 商品中心
│   │   ├── warehouse/         # 仓库中心
│   │   ├── inventory/         # 库存核心
│   │   ├── inbound/           # 入库管理
│   │   ├── outbound/          # 出库管理
│   │   ├── transfer/          # 调拨管理
│   │   ├── stocktake/         # 盘点管理
│   │   ├── task/              # 任务中心
│   │   ├── alert/             # 预警中心
│   │   ├── analytics/         # 数据分析
│   │   └── ai/                # AI智能助手
│   └── src/main/resources/
│       ├── application.yml
│       └── db/
│           ├── schema.sql     # 数据库表结构
│           └── data.sql       # 初始数据
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 公共组件
│   │   ├── layouts/           # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia状态管理
│   │   ├── styles/            # 全局样式
│   │   ├── types/             # TypeScript类型
│   │   ├── utils/             # 工具函数
│   │   └── views/             # 页面视图
│   └── package.json
└── docker-compose.yml         # Docker编排
```

## 快速开始

### 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Redis 7+

### 本地开发

#### 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端服务启动在 http://localhost:8080/api/v1

#### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务启动在 http://localhost:3000

### Docker启动

```bash
cp .env.example .env
# 编辑 .env，设置强数据库密码、Redis 密码和 JWT 密钥
docker-compose up -d
```

服务启动后：
- 前端：http://localhost
- 后端API：http://localhost:8080/api/v1
- Swagger文档：http://localhost:8080/api/v1/swagger-ui.html

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| warehouse_manager | admin123 | 仓库主管 |
| inbound_operator | admin123 | 入库员 |
| outbound_operator | admin123 | 出库员 |

> 上述账号仅用于首次初始化和本地演示。首次登录后请立即通过右上角用户菜单修改密码，生产环境禁止继续使用默认密码。

## API文档

启动后端服务后，访问 Swagger 文档：
http://localhost:8080/api/v1/swagger-ui.html

## 数据库设计

系统包含以下核心表：

- **系统管理**：sys_user, sys_role, sys_permission
- **商品中心**：wms_product, wms_sku, wms_category
- **仓库中心**：wms_warehouse, wms_zone, wms_rack, wms_location
- **库存核心**：wms_inventory, wms_inventory_transaction, wms_inventory_lock
- **入库管理**：wms_inbound_order, wms_inbound_order_item, wms_putaway_task
- **出库管理**：wms_outbound_order, wms_outbound_order_item, wms_picking_task
- **调拨管理**：wms_transfer_order, wms_transfer_order_item
- **盘点管理**：wms_stocktake, wms_stocktake_item
- **预警中心**：wms_inventory_alert
- **AI助手**：ai_conversation, ai_message, ai_tool_call

## 核心业务流程

### 入库流程

```
创建入库单 → 收货确认 → 质检 → 生成上架任务 → AI推荐库位 → 上架 → 库存增加 → 生成流水
```

### 出库流程

```
创建出库单 → 审核 → 库存分配 → 锁定库存(FIFO/FEFO) → 生成拣货任务 → 拣货 → 复核 → 发货 → 库存扣减 → 生成流水
```

### 盘点流程

```
创建盘点计划 → 选择仓库/库区 → 生成盘点任务 → 实际盘点 → 录入数量 → 计算差异 → 主管审核 → 库存调整
```

## AI智能助手

系统集成了 AI Warehouse Copilot，支持以下功能：

- 自然语言查询库存数据
- 库存风险智能分析
- 库位推荐解释
- 仓储异常分析
- 数据趋势预测

### AI工具函数

- `queryInventory()` - 查询库存
- `querySkuInventory()` - 查询SKU库存
- `queryWarehouseUtilization()` - 查询仓库利用率
- `queryLowStock()` - 查询低库存
- `queryExpiringBatch()` - 查询临期批次
- `querySlowMovingInventory()` - 查询呆滞库存
- `queryInboundOrders()` - 查询入库单
- `queryOutboundOrders()` - 查询出库单
- `queryInventoryTransactions()` - 查询库存流水
- `queryWarehouseTasks()` - 查询任务

## 项目亮点

1. **多维库存模型**：支持仓库+库位+SKU+批次的精细化库存管理
2. **并发控制**：Redis分布式锁 + 数据库乐观锁，确保库存操作安全
3. **FIFO/FEFO策略**：支持先进先出和先到期先出的出库策略
4. **智能库位推荐**：基于规则+权重的库位推荐算法
5. **事件驱动架构**：库存变更触发预警、统计、通知
6. **AI Agent**：集成Spring AI，支持自然语言查询
7. **完整审计日志**：所有关键操作可追溯

## License

MIT
