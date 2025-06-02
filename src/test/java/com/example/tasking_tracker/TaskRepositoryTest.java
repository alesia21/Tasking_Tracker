package com.example.tasking_tracker;

import com.example.tasking_tracker.entity.*;
import com.example.tasking_tracker.enums.*;
import com.example.tasking_tracker.repository.ProjectRepository;
import com.example.tasking_tracker.repository.TaskRepository;
import com.example.tasking_tracker.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

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
                .username("assignee")
                .email("a@b.com")
                .password("pass1234")
                .build());

        project = projectRepository.save(Project.builder()
                .name("Task Test Project")
                .owner(user)
                .build());
    }

    @Test
    void shouldFindByAssigneeId() {
        Task task = Task.builder()
                .title("Task")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .dueDate(LocalDate.now())
                .project(project)
                .assignee(user)
                .build();

        taskRepository.save(task);

        List<Task> tasks = taskRepository.findByAssigneeId(user.getId(), Pageable.unpaged()).getContent();
        assertThat(tasks).hasSize(1);
    }
}
