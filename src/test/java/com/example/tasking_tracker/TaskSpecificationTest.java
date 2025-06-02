package com.example.tasking_tracker;

import com.example.tasking_tracker.entity.*;
import com.example.tasking_tracker.enums.*;
import com.example.tasking_tracker.repository.*;
import com.example.tasking_tracker.entity.TaskSpecifications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaskSpecificationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .build());

        project = projectRepository.save(Project.builder()
                .name("Test Project")
                .owner(user)
                .build());

        Task t1 = Task.builder()
                .title("Urgent Bug Fix")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now())
                .project(project)
                .assignee(user)
                .build();

        Task t2 = Task.builder()
                .title("Feature Implementation")
                .status(TaskStatus.COMPLETED)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(3))
                .project(project)
                .assignee(user)
                .build();

        Task t3 = Task.builder()
                .title("Code Cleanup")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .dueDate(LocalDate.now().minusDays(1))
                .project(project)
                .assignee(user)
                .build();

        taskRepository.saveAll(List.of(t1, t2, t3));
    }

    @Test
    void testHasStatusSpecification() {
        Specification<Task> spec = TaskSpecifications.hasStatus("TODO");
        List<Task> result = taskRepository.findAll(spec);
        assertThat(result).hasSize(2);
    }

    @Test
    void testHasPrioritySpecification() {
        Specification<Task> spec = TaskSpecifications.hasPriority("HIGH");
        List<Task> result = taskRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Urgent Bug Fix");
    }




    @Test
    void testCombinedSpecification() {
        Specification<Task> spec =
                TaskSpecifications.hasStatus("TODO")
                        .and(TaskSpecifications.hasPriority("LOW"))
                        .and(TaskSpecifications.dueBefore(LocalDate.now().plusDays(1)));

        List<Task> result = taskRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Code Cleanup");
    }

}
