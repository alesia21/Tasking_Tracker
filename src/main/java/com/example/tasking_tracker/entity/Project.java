package com.example.tasking_tracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;//Allows ignoring properties during JSON serialization
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;// Allows ignoring specific properties on related objects
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity//Marking this class as a JPA entity, mapped to a db table.
@Table(name = "projects")//Specifying that this entity maps to the projects table.
@Getter @Setter @NoArgsConstructor @AllArgsConstructor //Lombok generates a no argument constructor, all arg constructor.
@Builder//Enabling the builder for creation of objects.
public class Project {

    @Id//Marking this field as primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY)// the id values will be auto generated.
    private Long id;

    @NotBlank//Validation : name must not be null or empty.
    @Size(min = 3, max = 50) //Validation :name length must be between 3-50 chars.
    private String name;

    private String description;//Description of the project.

    private LocalDateTime createdAt;//Timestamp for  when the project was created.

   //Each project is owned by excatly one user /many projects can belong to one user / loading owner only when accessed.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)//Foreign key in this table is owner id
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    // Prevent Hibernate-specific properties from being serialized into JSON when returning this entity
    private User owner;

//Costum getter to include only owner id in the JSON response instead of full User content (object).
    @JsonProperty("ownerId")
    public Long getOwnerId() {
        return (owner != null ? owner.getId() : null);
        //checking if owner is not null,getting the id , otherwise return null
    }

//One project can have many tasks.
    //mappedBy= project , each task has a project field pointing back to Project entity
    //if a task is removed from the task list , it will be deleted from the db too.
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore//avoiding infinite recursion (preventing the task list from being sent to JSON)
    private List<Task> tasks; //List of all tasks belonging to the project.

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}

//whenever loading a Project, jpa collect all tasks object whose task.project references matches that projects id