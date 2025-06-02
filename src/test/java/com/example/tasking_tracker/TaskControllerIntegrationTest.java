package com.example.tasking_tracker;
import com.example.tasking_tracker.entity.*;
import com.example.tasking_tracker.enums.TaskPriority;
import com.example.tasking_tracker.enums.TaskStatus;
import com.example.tasking_tracker.repository.ProjectRepository;
import com.example.tasking_tracker.repository.TaskRepository;
import com.example.tasking_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TaskControllerIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TaskRepository taskRepository;

    private TestEntityManager entityManager;
    private User assignee;
    private Project project;

    @BeforeEach
    void setupData() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        assignee = User.builder()
                .username("assignee")
                .email("assignee@example.com")
                .password("testpass123")
                .build();
        assignee = userRepository.save(assignee);

        project = Project.builder()
                .name("Test Project")
                .description("Sample project")
                .owner(assignee)
                .build();
        project = projectRepository.save(project);
    }

    @Test
    void testCreateAndFetchTask() {
        Task task = Task.builder()
                .title("Write Docs")
                .description("Document API")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now())
                .assignee(assignee)
                .project(project)
                .build();

        Task saved = taskRepository.save(task);
        assertThat(saved.getId()).isNotNull();

        Task fetched = taskRepository.findById(saved.getId()).orElse(null);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getTitle()).isEqualTo("Write Docs");
        assertThat(fetched.getAssignee().getId()).isEqualTo(assignee.getId());
    }

    @Test
    void testFindTasksByProjectAndStatus() {
        Task task1 = Task.builder()
                .title("Frontend")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now())
                .project(project)
                .assignee(assignee)
                .build();

        Task task2 = Task.builder()
                .title("Backend")
                .status(TaskStatus.COMPLETED)
                .priority(TaskPriority.LOW)
                .dueDate(LocalDate.now())
                .project(project)
                .assignee(assignee)
                .build();

        taskRepository.save(task1);
        taskRepository.save(task2);

        var tasksTodo = taskRepository.findByProjectIdAndStatus(project.getId(), TaskStatus.TODO, PageRequest.of(0, 10));
        assertThat(tasksTodo.getTotalElements()).isEqualTo(1);
        assertThat(tasksTodo.getContent().get(0).getTitle()).isEqualTo("Frontend");
    }


}
