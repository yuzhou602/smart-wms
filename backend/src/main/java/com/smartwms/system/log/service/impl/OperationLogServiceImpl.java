package com.smartwms.system.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.response.PageResult;
import com.smartwms.system.log.entity.OperationLog;
import com.smartwms.system.log.mapper.OperationLogMapper;
import com.smartwms.system.log.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public PageResult<OperationLog> listLogs(int page, int pageSize, String keyword, String module,
                                             java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(q -> q.like(OperationLog::getUsername, keyword)
                    .or().like(OperationLog::getDescription, keyword)
                    .or().like(OperationLog::getOperation, keyword));
        }
        if (StringUtils.hasText(module)) {
            wrapper.eq(OperationLog::getModule, module);
        }
        if (startTime != null) wrapper.ge(OperationLog::getCreatedAt, startTime);
        if (endTime != null) wrapper.le(OperationLog::getCreatedAt, endTime);
        wrapper.orderByDesc(OperationLog::getCreatedAt);

        Page<OperationLog> pageResult = operationLogMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<OperationLog> records = pageResult.getRecords();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    @Async
    public void saveLog(OperationLog log) {
        operationLogMapper.insert(log);
    }
}
