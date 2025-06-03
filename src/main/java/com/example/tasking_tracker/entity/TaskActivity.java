package com.example.tasking_tracker.entity;
import jakarta.persistence.*; //Importing JPA annotations for mapping to the db.
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskActivity {

    @Id//Marking this field as a primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //db will auto generate id values.
    private Long id;

//Each TaskActivity is linked to excatly one Task
    //Many activities can belong to one task // loading task only when needed.
    @ManyToOne(fetch = FetchType.LAZY)
    //Specifying the foreign key column task id in taskActivity table, cannot be null.
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;//Task on which activity is performed.


    //Each TaskActivity records which user performed the action.
    //Many activities can be performed by one user ; load User  only  when needed.
    @ManyToOne(fetch = FetchType.LAZY)
    //Specifying the foregin key , which cannot be null
    @JoinColumn(name = "performed_by_user_id", nullable = false)
    private User performedBy; //the user who performed the activity.


    @Column(nullable = false, length = 50)
    private String action;


    @Column(nullable = false)
    private LocalDateTime createdAt;
}
