package com.smartwms.alert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.alert.entity.Alert;
import com.smartwms.alert.mapper.AlertMapper;
import com.smartwms.alert.service.AlertService;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl extends ServiceImpl<AlertMapper, Alert> implements AlertService {

    private final AlertMapper alertMapper;

    @Override
    public PageResult<Alert> listAlerts(int page, int pageSize, Integer isHandled, String alertType) {
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();

        if (isHandled != null) {
            wrapper.eq(Alert::getIsHandled, isHandled);
        }
        if (StringUtils.hasText(alertType)) {
            wrapper.eq(Alert::getAlertType, alertType);
        }
        wrapper.orderByDesc(Alert::getCreatedAt);

        Page<Alert> pageResult = alertMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<Alert> records = pageResult.getRecords();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public Alert getAlertById(Long id) {
        Alert alert = alertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return alert;
    }

    @Override
    public void handleAlert(Long id, Long userId, String handleResult) {
        if (!StringUtils.hasText(handleResult)) throw new BusinessException("处理结果不能为空");
        Alert alert = alertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (alert.getIsHandled() == 1) {
            throw new BusinessException("预警已处理");
        }

        alert.setIsHandled(1);
        alert.setHandledBy(userId);
        alert.setHandledAt(LocalDateTime.now());
        alert.setHandleResult(handleResult);
        alertMapper.updateById(alert);
    }

    @Override
    public java.util.Map<String, Long> getSummary() {
        java.util.Map<String, Long> summary = new java.util.LinkedHashMap<>();
        summary.put("high", countByLevel("HIGH", 0));
        summary.put("medium", countByLevel("MEDIUM", 0));
        summary.put("low", countByLevel("LOW", 0));
        summary.put("handled", alertMapper.selectCount(new LambdaQueryWrapper<Alert>().eq(Alert::getIsHandled, 1)));
        return summary;
    }

    private long countByLevel(String level, int handled) {
        return alertMapper.selectCount(new LambdaQueryWrapper<Alert>()
                .eq(Alert::getAlertLevel, level).eq(Alert::getIsHandled, handled));
    }
}
