package com.smartwms.inbound.dto;

import com.smartwms.inbound.entity.InboundOrder;
import com.smartwms.inbound.entity.InboundOrderItem;
import lombok.Data;

import java.util.List;

@Data
public class InboundOrderCreateRequest {

    private InboundOrder order;

    private List<InboundOrderItem> items;
}
