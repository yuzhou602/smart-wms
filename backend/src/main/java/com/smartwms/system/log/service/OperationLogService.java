package com.smartwms.system.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.system.log.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {

    PageResult<OperationLog> listLogs(int page, int pageSize, String keyword, String module,
                                      java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);

    void saveLog(OperationLog log);
}
