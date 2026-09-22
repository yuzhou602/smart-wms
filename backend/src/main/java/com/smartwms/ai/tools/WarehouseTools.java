package com.smartwms.ai.tools;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartwms.inventory.entity.Inventory;
import com.smartwms.inventory.mapper.InventoryMapper;
import com.smartwms.inventory.entity.Batch;
import com.smartwms.inventory.mapper.BatchMapper;
import com.smartwms.inbound.entity.InboundOrder;
import com.smartwms.inbound.mapper.InboundOrderMapper;
import com.smartwms.outbound.entity.OutboundOrder;
import com.smartwms.outbound.mapper.OutboundOrderMapper;
import com.smartwms.task.entity.Task;
import com.smartwms.task.mapper.TaskMapper;
import com.smartwms.warehouse.entity.Warehouse;
import com.smartwms.warehouse.mapper.WarehouseMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WarehouseTools {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private BatchMapper batchMapper;

    @Autowired
    private InboundOrderMapper inboundOrderMapper;

    @Autowired
    private OutboundOrderMapper outboundOrderMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Tool(description = "查询库存信息，可以按SKU编码或仓库ID筛选。返回当前库存列表。")
    public List<Inventory> queryInventory(
            @ToolParam(description = "SKU编码，可选", required = false) String skuCode,
            @ToolParam(description = "仓库ID，可选", required = false) Long warehouseId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        if (warehouseId != null) {
            wrapper.eq(Inventory::getWarehouseId, warehouseId);
        }
        if (skuCode != null && !skuCode.isEmpty()) {
            wrapper.like(Inventory::getSkuId, skuCode);
        }
        wrapper.last("LIMIT 50");
        return inventoryMapper.selectList(wrapper);
    }

    @Tool(description = "查询低库存商品，返回库存数量低于安全库存的商品列表。")
    public List<Inventory> queryLowStock() {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("total_qty < 50");
        wrapper.last("LIMIT 20");
        return inventoryMapper.selectList(wrapper);
    }

    @Tool(description = "查询临期商品批次，返回30天内即将过期的批次。")
    public List<Batch> queryExpiringBatch() {
        LambdaQueryWrapper<Batch> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Batch::getExpiryDate, LocalDate.now().plusDays(30));
        wrapper.ge(Batch::getExpiryDate, LocalDate.now());
        wrapper.last("LIMIT 20");
        return batchMapper.selectList(wrapper);
    }

    @Tool(description = "查询各仓库的利用率情况。返回仓库名称和当前库存数量。")
    public Map<String, Object> queryWarehouseUtilization() {
        List<Warehouse> warehouses = warehouseMapper.selectList(null);
        Map<String, Object> result = new HashMap<>();
        result.put("warehouses", warehouses);
        result.put("totalWarehouses", warehouses.size());
        return result;
    }

    @Tool(description = "查询入库单列表，返回最近的入库单。")
    public List<InboundOrder> queryInboundOrders() {
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(InboundOrder::getCreatedAt);
        wrapper.last("LIMIT 10");
        return inboundOrderMapper.selectList(wrapper);
    }

    @Tool(description = "查询出库单列表，返回最近的出库单。")
    public List<OutboundOrder> queryOutboundOrders() {
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OutboundOrder::getCreatedAt);
        wrapper.last("LIMIT 10");
        return outboundOrderMapper.selectList(wrapper);
    }

    @Tool(description = "查询任务列表，可以按状态筛选。返回任务信息。")
    public List<Task> queryWarehouseTasks(
            @ToolParam(description = "任务状态，如PENDING、IN_PROGRESS、COMPLETED，可选", required = false) String status) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Task::getStatus, status);
        }
        wrapper.orderByDesc(Task::getCreatedAt);
        wrapper.last("LIMIT 20");
        return taskMapper.selectList(wrapper);
    }

    @Tool(description = "查询呆滞库存，返回60天内没有出入库记录的库存。")
    public List<Inventory> querySlowMovingInventory() {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("DATEDIFF(NOW(), updated_at) > 60");
        wrapper.last("LIMIT 20");
        return inventoryMapper.selectList(wrapper);
    }

    @Tool(description = "查询指定SKU的库存详情。返回该SKU在所有仓库的库存信息。")
    public List<Inventory> querySkuInventory(
            @ToolParam(description = "SKU ID") Long skuId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getSkuId, skuId);
        return inventoryMapper.selectList(wrapper);
    }
}
