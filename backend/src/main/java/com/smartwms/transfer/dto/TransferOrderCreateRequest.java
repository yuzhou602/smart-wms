package com.smartwms.transfer.dto;

import com.smartwms.transfer.entity.TransferOrder;
import com.smartwms.transfer.entity.TransferOrderItem;
import lombok.Data;

import java.util.List;

@Data
public class TransferOrderCreateRequest {

    private TransferOrder order;

    private List<TransferOrderItem> items;
}
