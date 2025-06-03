package com.example.tasking_tracker.controller;

import com.example.tasking_tracker.entity.Task;
import com.example.tasking_tracker.entity.TaskSpecifications;
import com.example.tasking_tracker.enums.TaskStatus;
import com.example.tasking_tracker.repository.TaskRepository;
import com.example.tasking_tracker.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    private final TaskService taskService;


    @PostMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<Task> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody Task taskRequest
    ) {
        Task created = taskService.createTask(projectId, taskRequest);
        return ResponseEntity.ok(created);
    }


    @GetMapping("/api/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<List<Task>> getTasksByUser(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksByAssignee(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<List<Task>> getAllTasksInProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        List<Task> content = taskService
                .getAllTasksInProject(projectId, status, pageable)
                .getContent();
        return ResponseEntity.ok(content);
    }



    @PutMapping("/api/tasks/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody Task taskRequest
    ) {
        Task updated = taskService.updateTask(id, taskRequest);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/tasks/{id}
     * Delete a task by ID.
     */
    @DeleteMapping("/api/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/tasks/due-today?page=0&size=10
     * Return only the List<Task> for tasks whose dueDate == today.
     */
    @GetMapping("/api/tasks/due-today")
    public ResponseEntity<List<Task>> getTasksDueToday(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("priority").descending());
        List<Task> content = taskService
                .getTasksDueToday(pageable)
                .getContent();
        return ResponseEntity.ok(content);
    }

    /**
     * GET /api/users/{userId}/tasks?page=0&size=10
     * Return only the List<Task> for tasks assigned to a given user.
     */
    @GetMapping("/api/users/{userId}/tasks")
    public ResponseEntity<List<Task>> getTasksByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        List<Task> content = taskService
                .getTasksByUserId(userId, pageable)
                .getContent();
        return ResponseEntity.ok(content);
    }
    @GetMapping("/api/tasks/search")
    public List<Task> searchTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBefore
    ) {
        Specification<Task> spec = TaskSpecifications.hasStatus(status);

        if (priority != null) {
            spec = spec.and(TaskSpecifications.hasPriority(priority));
        }

        if (dueBefore != null) {
            spec = spec.and(TaskSpecifications.dueBefore(dueBefore));
        }

        return taskRepository.findAll(spec);
    }

    @GetMapping("/api/tasks/search-by-username-and-project")
    public ResponseEntity<List<Task>> getTasksByUsernameAndProjectName(
            @RequestParam String username,
            @RequestParam String projectName
    ) {
        List<Task> tasks = taskService.getTasksByAssigneeUsernameAndProjectName(username, projectName);
        return ResponseEntity.ok(tasks);
    }



}
