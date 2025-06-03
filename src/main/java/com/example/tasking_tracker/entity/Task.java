package com.example.tasking_tracker.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;//allowing ignoring certain properties during JSON serialization
import  com.example.tasking_tracker.enums.TaskPriority;//importing the enum task priority values
import  com.example.tasking_tracker.enums.TaskStatus;//importing the enum for task status values.
import jakarta.persistence.*;//JPA annotation for mapping this class to a database table.
import jakarta.validation.constraints.NotBlank;//Import for Validating string fields are not blank
import jakarta.validation.constraints.NotNull;//Validation that string fields are not null.
import jakarta.validation.constraints.Size;//Validating string length.
import lombok.*;

import java.time.LocalDate;//import for date
import java.time.LocalDateTime;//import for date with time

@Entity//Marking this class as JPA entity.(will map to a database table)
@Table(name = "tasks")//Specifiying the table name in the db as tasks.
@Getter @Setter @NoArgsConstructor @AllArgsConstructor //Lombok generates getters and setters method for all fields//Generating a constructor with all fields//generating a no arg constructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})//Preventing serialization issues by ignoring Hibernate specific properties.
@Builder//Lombok that generates a builder pattern for object creation.
public class Task {

    @Id//Marking this field as the primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Database will autogenerate id values.
    private Long id;

    @NotBlank//Validation :: title must not be null or empty.
    @Size(min = 3, max = 100)//Validation : title length must be between 3 and 100 chars.
    private String title;

    private String description; //description of the task

    @Enumerated(EnumType.STRING) //Storing the enum values as a string in db.
    @NotNull//Validation :: status must not be null
    private TaskStatus status;//Status of the task

    @Enumerated(EnumType.STRING)//Storing the enum values as a string in db.
    @NotNull//Validation :: priority must not be null
    private TaskPriority priority;//Priority of the task.

    private LocalDate dueDate; //Date by which the task should be completed.



    @Column(name = "created_at", nullable = false, updatable = false) //Mapping to created_at column ;cannot be change once set.
    private LocalDateTime createdAt;;//Timestamp for when the task was created.

    @ManyToOne(fetch = FetchType.LAZY, optional = false)//Many tasks belong to one project // Loaded lazily; only when accessed//
    @JoinColumn(name = "project_id", nullable = false)//Foreign key column in tasks table referencing projects table.
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)//Many tasks can be assigned to one user; loaded lazily.
    //Optional=false :: meaning that  we can not save a task unless a valid project and valid assignee.
    //always. pointing to a non null Project or user.
    @JoinColumn(name = "assignee_id", nullable = false)
    //Foreign key column in tasks table referencing users table.
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private User assignee;

    @PrePersist
    protected void onCreate()  {//Calling this method before the entity is saved for the first time.
        this.createdAt = LocalDateTime.now(); //Automatically set the creation stamp when saving.
    }

}