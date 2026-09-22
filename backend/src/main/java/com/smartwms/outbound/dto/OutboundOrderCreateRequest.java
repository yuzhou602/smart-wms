package com.smartwms.outbound.dto;

import com.smartwms.outbound.entity.OutboundOrder;
import com.smartwms.outbound.entity.OutboundOrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OutboundOrderCreateRequest {

    private OutboundOrder order;

    private List<OutboundOrderItem> items;
}
