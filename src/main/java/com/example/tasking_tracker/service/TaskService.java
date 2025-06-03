package com.example.tasking_tracker.service;

import com.example.tasking_tracker.entity.Task;
import com.example.tasking_tracker.entity.TaskActivity;
import com.example.tasking_tracker.entity.User;
import com.example.tasking_tracker.entity.Project;
import com.example.tasking_tracker.enums.TaskStatus;
import com.example.tasking_tracker.exception.ResourceNotFoundException;
import com.example.tasking_tracker.repository.TaskActivityRepository;
import com.example.tasking_tracker.repository.TaskRepository;
import com.example.tasking_tracker.repository.ProjectRepository;
import com.example.tasking_tracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private  final TaskActivityRepository taskActivityRepository;


@Transactional
    public Task createTask(Long projectId, Task task) {
        // 1) Verify project exists (404 if missing)
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
    // 2) Ensure task.getAssignee() is not null
    if (task.getAssignee() == null || task.getAssignee().getId() == null) {
        throw new IllegalArgumentException("Assignee must not be null");
    }
        // 2) Verify assignee exists (404 if missing)
        Long assigneeId = task.getAssignee().getId();
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", assigneeId));

        // 3) Set relationships and save
        task.setProject(project);
        task.setAssignee(assignee);
        Task savedTask = taskRepository.save(task);

        // 4) Record audit: "CREATED" by the same assignee
        TaskActivity creationActivity = TaskActivity.builder()
                .task(savedTask)
                .performedBy(assignee)
                .action("CREATED")
                .createdAt(LocalDateTime.now())
                .build();
        taskActivityRepository.save(creationActivity);

        return savedTask;

    }


    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }


    public List<Task> getTasksByAssignee(Long userId) {
        return taskRepository.findByAssigneeId(userId);
    }

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


    @Transactional
    public Task updateTask(Long taskId, Task taskRequest) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        // Determine actor: by default, use the existing assignee
        User actor = existing.getAssignee();

        // If a new assignee is provided, verify and set it as the actor
        if (taskRequest.getAssignee() != null) {
            Long newAssigneeId = taskRequest.getAssignee().getId();
            User newAssignee = userRepository.findById(newAssigneeId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", newAssigneeId));
            existing.setAssignee(newAssignee);
            actor = newAssignee;
        }

        // Update other fields
        existing.setTitle(taskRequest.getTitle());
        existing.setDescription(taskRequest.getDescription());
        existing.setStatus(taskRequest.getStatus());
        existing.setPriority(taskRequest.getPriority());
        existing.setDueDate(taskRequest.getDueDate());

        Task updated = taskRepository.save(existing);

        // Record audit: "UPDATED" by the determined actor
        TaskActivity updateActivity = TaskActivity.builder()
                .task(updated)
                .performedBy(actor)
                .action("UPDATED")
                .createdAt(LocalDateTime.now())
                .build();
        taskActivityRepository.save(updateActivity);

        return updated;
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        // Actor is the Task’s current assignee
        User actor = existing.getAssignee();

        // Record audit: "DELETED"
        TaskActivity deleteActivity = TaskActivity.builder()
                .task(existing)
                .performedBy(actor)
                .action("DELETED")
                .createdAt(LocalDateTime.now())
                .build();
        taskActivityRepository.save(deleteActivity);

        // Actually delete the Task
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
