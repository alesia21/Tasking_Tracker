package com.example.tasking_tracker.service;

import com.example.tasking_tracker.entity.Task;
import com.example.tasking_tracker.entity.User;
import com.example.tasking_tracker.entity.Project;
import com.example.tasking_tracker.enums.TaskStatus;
import com.example.tasking_tracker.exception.ResourceNotFoundException;
import com.example.tasking_tracker.repository.TaskRepository;
import com.example.tasking_tracker.repository.ProjectRepository;
import com.example.tasking_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /**
     * Create a new Task under the given projectId.
     * JSON must include valid "status" (TaskStatus) and "priority" (TaskPriority),
     * and an "assignee.id" referring to an existing User.
     */
    public Task createTask(Long projectId, Task task) {
        // 1) Verify project exists (404 if missing)
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        // 2) Verify assignee exists (404 if missing)
        Long assigneeId = task.getAssignee().getId();
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", assigneeId));

        // 3) Set relationships and save
        task.setProject(project);
        task.setAssignee(assignee);
        return taskRepository.save(task);
    }

    /**
     * Fetch a single Task by its ID (404 if not found).
     */
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
    }

    /**
     * Fetch all tasks in a project, optionally filtered by TaskStatus.
     * If `status` is null, returns all tasks in that project.
     */
    public Page<Task> getAllTasksInProject(Long projectId, TaskStatus status, Pageable pageable) {
        // 1) Verify project exists
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        // 2) If status provided, filter by it
        if (status != null) {
            return taskRepository.findByProjectIdAndStatus(projectId, status, pageable);
        }
        // 3) Otherwise, return all tasks for that project
        return taskRepository.findByProjectId(projectId, pageable);
    }

    /**
     * Update an existing Task (title, description, status, priority, dueDate,
     * and optionally reassign if "assignee.id" is provided).
     */
    public Task updateTask(Long taskId, Task taskRequest) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        existing.setTitle(taskRequest.getTitle());
        existing.setDescription(taskRequest.getDescription());
        existing.setStatus(taskRequest.getStatus());
        existing.setPriority(taskRequest.getPriority());
        existing.setDueDate(taskRequest.getDueDate());

        // If a new assignee is provided, verify and set it
        if (taskRequest.getAssignee() != null) {
            Long newAssigneeId = taskRequest.getAssignee().getId();
            User newAssignee = userRepository.findById(newAssigneeId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", newAssigneeId));
            existing.setAssignee(newAssignee);
        }

        return taskRepository.save(existing);
    }

    /**
     * Delete a Task by its ID (404 if not found).
     */
    public void deleteTask(Long taskId) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        taskRepository.delete(existing);
    }

    /**
     * Return all tasks whose dueDate == today, paginated.
     */
    public Page<Task> getTasksDueToday(Pageable pageable) {
        return taskRepository.findByDueDate(LocalDate.now(), pageable);
    }

    /**
     * Return all tasks assigned to a given user, paginated.
     */
    public List<Task> getTasksByAssigneeUsernameAndProjectName(String username, String projectName) {
        return taskRepository.findTasksByAssigneeUsernameAndProjectName(username, projectName);
    }
    public Page<Task> getTasksByUserId(Long userId, Pageable pageable) {
        return taskRepository.findByAssigneeId(userId, pageable);
    }

}
