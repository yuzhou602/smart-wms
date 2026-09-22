package com.smartwms.task.controller;

import com.smartwms.common.response.PageResult;
import com.smartwms.common.response.R;
import com.smartwms.task.entity.Task;
import com.smartwms.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任务管理", description = "任务CRUD操作")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('task:center')")
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "分页查询任务列表")
    @GetMapping
    public R<PageResult<Task>> listTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) Long assigneeId) {
        return R.ok(taskService.listTasks(page, pageSize, keyword, status, taskType, assigneeId));
    }

    @Operation(summary = "获取任务详情")
    @GetMapping("/{id}")
    public R<Task> getTaskById(@PathVariable Long id) {
        return R.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "创建任务")
    @PostMapping
    public R<Task> createTask(@RequestBody Task task) {
        return R.ok(taskService.createTask(task));
    }

    @Operation(summary = "分配任务")
    @PostMapping("/{id}/assign")
    public R<Void> assignTask(@PathVariable Long id, @RequestBody AssignRequest request) {
        taskService.assignTask(id, request.getAssigneeId());
        return R.ok();
    }

    @Operation(summary = "完成任务")
    @PostMapping("/{id}/complete")
    public R<Void> completeTask(@PathVariable Long id) {
        taskService.completeTask(id);
        return R.ok();
    }

    @Data
    public static class AssignRequest {
        private Long assigneeId;
    }
}
