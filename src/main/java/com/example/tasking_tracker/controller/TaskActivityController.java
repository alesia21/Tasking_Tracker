package com.example.tasking_tracker.controller;

import com.example.tasking_tracker.entity.Task;
import com.example.tasking_tracker.entity.TaskActivity;
import com.example.tasking_tracker.exception.ResourceNotFoundException;
import com.example.tasking_tracker.repository.TaskActivityRepository;
import com.example.tasking_tracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskActivityController {

    private final TaskRepository taskRepository;
    private final TaskActivityRepository taskActivityRepository;

    @GetMapping("/{taskId}/activities")
    public ResponseEntity<List<TaskActivity>> getTaskActivities(@PathVariable Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        List<TaskActivity> activities = taskActivityRepository.findByTaskOrderByCreatedAtDesc(task);
        return ResponseEntity.ok(activities);
    }
}
