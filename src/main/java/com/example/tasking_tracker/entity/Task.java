// com/tasktracker/entity/Task.java
package com.example.tasking_tracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import  com.example.tasking_tracker.enums.TaskPriority;
import  com.example.tasking_tracker.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import  com.example.tasking_tracker.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TaskPriority priority;

    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","tasks"})
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","projects","assignedTasks"})
    private User assignee;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}