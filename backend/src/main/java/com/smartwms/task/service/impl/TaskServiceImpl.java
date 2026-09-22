package com.smartwms.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.PageResult;
import com.smartwms.common.utils.OrderNumberGenerator;
import com.smartwms.task.entity.Task;
import com.smartwms.task.mapper.TaskMapper;
import com.smartwms.task.service.TaskService;
import com.smartwms.system.user.entity.User;
import com.smartwms.system.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final TaskMapper taskMapper;
    private final UserMapper userMapper;

    @Override
    public PageResult<Task> listTasks(int page, int pageSize, String keyword, String status, String taskType, Long assigneeId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(q -> q.like(Task::getTaskNo, keyword)
                    .or().like(Task::getSourceOrderNo, keyword)
                    .or().like(Task::getAssigneeName, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Task::getStatus, status);
        }
        if (StringUtils.hasText(taskType)) {
            wrapper.eq(Task::getTaskType, taskType);
        }
        if (assigneeId != null) {
            wrapper.eq(Task::getAssigneeId, assigneeId);
        }
        wrapper.orderByDesc(Task::getCreatedAt);

        Page<Task> pageResult = taskMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<Task> records = pageResult.getRecords();

        return PageResult.of(records, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public Task getTaskById(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        if (!StringUtils.hasText(task.getTaskType()) || task.getWarehouseId() == null)
            throw new BusinessException("任务类型和仓库不能为空");
        task.setTaskNo(OrderNumberGenerator.generateTaskOrder());
        task.setStatus("PENDING");
        task.setId(null);
        task.setAssigneeId(null);
        task.setAssigneeName(null);
        task.setStartedAt(null);
        task.setCompletedAt(null);
        if (task.getPriority() == null) task.setPriority(0);
        taskMapper.insert(task);
        return task;
    }

    @Override
    public void assignTask(Long id, Long assigneeId) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!"PENDING".equals(task.getStatus())) {
            throw new BusinessException("任务状态不允许分配");
        }

        User assignee = userMapper.selectById(assigneeId);
        if (assignee == null || assignee.getStatus() != 1) throw new BusinessException("执行人不存在或已停用");
        task.setAssigneeId(assigneeId);
        task.setAssigneeName(StringUtils.hasText(assignee.getRealName()) ? assignee.getRealName() : assignee.getUsername());
        task.setStartedAt(LocalDateTime.now());
        task.setStatus("IN_PROGRESS");
        taskMapper.updateById(task);
    }

    @Override
    public void completeTask(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!"IN_PROGRESS".equals(task.getStatus())) {
            throw new BusinessException("任务状态不允许完成");
        }

        task.setCompletedAt(LocalDateTime.now());
        task.setStatus("COMPLETED");
        taskMapper.updateById(task);
    }
}
