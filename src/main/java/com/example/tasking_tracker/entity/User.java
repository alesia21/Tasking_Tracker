package com.example.tasking_tracker.entity;

import com.example.tasking_tracker.entity.Project;
import com.fasterxml.jackson.annotation.JsonIgnore; //using json ignore to prevent serialization of certain fields in order to avoid infinite loops.
import jakarta.persistence.*;//jpa annotation for mapping
import jakarta.validation.constraints.*;//validation api for input constraint
//lombok annotation to generate constructor with all the fields
//generating an inner builder class with methods for setting the fields and a build method
//it also adds a method to the class so it can create an instance of the class without calling the constructor
import lombok.*;//lombok that generates setters,getters,equals,toString etc.
import jakarta.persistence.OneToMany;
//lombok that generates a non argument constructor required by jpa

import java.time.LocalDateTime;
import java.util.List;

@Entity//marking the class user as a JPA  entity which maps to a database table
@Table(name = "app_user")//specifying the table name in the database
@Data
@NoArgsConstructor
@AllArgsConstructor//lombok generating a constructor with arguments for all fields.
@Builder//building an inner static class build containing the methods for setting all the fields and a build method to create an instance of the class
public class User {

    @Id//denoting the primary key of the entity user
    @GeneratedValue(strategy = GenerationType.IDENTITY) //by this annotation we configure auto incrementing for the database table primary key.
    private Long id;

    @NotBlank //validation that username must not be null or empty .
    @Size(min = 3)//validation that username must be at least 3 characters long.
    private String username;

    @NotBlank//validation that email must not be null or empty.
    @Email//validation that email must conform to standard email format  for example alesia@example.com
    private String email;//email for users needed for login

    @NotBlank//validation that password must not be null or empty
    @Size(min = 8)//validation that password must be at least 8 characters long
    private String password;

    private LocalDateTime createdAt = LocalDateTime.now();//by using LocalDateTime we get the time that the user was created.

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)//one user can own many projects // automatically applies save, update, delete, etc. on the parent entity to its child entities in this case
    //child entities are two collections projects and assignedTasks
    @JsonIgnore//have used json ignore to avoid infinite loops of projects.
    private List<Project> projects;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL)//one user can be assigned many tasks.
    @JsonIgnore//have used json ignore to avoid infinite loops of tasks
    private List<Task> assignedTasks;
}