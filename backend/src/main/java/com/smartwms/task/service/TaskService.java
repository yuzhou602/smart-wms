package com.smartwms.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.task.entity.Task;

public interface TaskService extends IService<Task> {

    PageResult<Task> listTasks(int page, int pageSize, String keyword, String status, String taskType, Long assigneeId);

    Task getTaskById(Long id);

    Task createTask(Task task);

    void assignTask(Long id, Long assigneeId);

    void completeTask(Long id);
}
