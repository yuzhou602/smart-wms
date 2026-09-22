package com.smartwms.alert.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.alert.entity.Alert;
import com.smartwms.common.response.PageResult;

public interface AlertService extends IService<Alert> {

    PageResult<Alert> listAlerts(int page, int pageSize, Integer isHandled, String alertType);

    Alert getAlertById(Long id);

    void handleAlert(Long id, Long userId, String handleResult);

    java.util.Map<String, Long> getSummary();
}
