package com.smartwms.transfer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.transfer.entity.TransferOrder;
import com.smartwms.transfer.entity.TransferOrderItem;
import com.smartwms.transfer.vo.TransferOrderVO;

import java.util.List;

public interface TransferOrderService extends IService<TransferOrder> {

    PageResult<TransferOrderVO> listTransferOrders(int page, int pageSize, String keyword, String status);

    TransferOrderVO getTransferOrderById(Long id);

    List<TransferOrderItem> getTransferOrderItems(Long orderId);

    TransferOrderVO createTransferOrder(TransferOrder order, List<TransferOrderItem> items);

    void approveTransferOrder(Long orderId);

    void executeTransferOrder(Long orderId);
}
