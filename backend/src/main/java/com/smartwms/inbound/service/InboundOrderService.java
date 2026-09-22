package com.smartwms.inbound.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.inbound.entity.InboundOrder;
import com.smartwms.inbound.entity.InboundOrderItem;
import com.smartwms.inbound.vo.InboundOrderVO;

import java.util.List;

public interface InboundOrderService extends IService<InboundOrder> {

    PageResult<InboundOrderVO> listInboundOrders(int page, int pageSize, String keyword, String status);

    InboundOrderVO getInboundOrderById(Long id);

    List<InboundOrderItem> getInboundOrderItems(Long orderId);

    InboundOrderVO createInboundOrder(InboundOrder order, List<InboundOrderItem> items);

    void receiveInboundOrder(Long orderId);

    void putawayInboundOrder(Long orderId);
}
