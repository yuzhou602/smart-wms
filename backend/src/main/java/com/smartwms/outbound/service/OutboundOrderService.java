package com.smartwms.outbound.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.outbound.entity.OutboundOrder;
import com.smartwms.outbound.entity.OutboundOrderItem;
import com.smartwms.outbound.vo.OutboundOrderVO;

import java.util.List;

public interface OutboundOrderService extends IService<OutboundOrder> {

    PageResult<OutboundOrderVO> listOutboundOrders(int page, int pageSize, String keyword, String status);

    OutboundOrderVO getOutboundOrderById(Long id);

    List<OutboundOrderItem> getOutboundOrderItems(Long orderId);

    OutboundOrderVO createOutboundOrder(OutboundOrder order, List<OutboundOrderItem> items);

    void approveOutboundOrder(Long orderId);

    void pickOutboundOrder(Long orderId);

    void shipOutboundOrder(Long orderId);
}
