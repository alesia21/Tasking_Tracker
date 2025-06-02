package com.example.tasking_tracker;


import com.example.tasking_tracker.entity.*;
import com.example.tasking_tracker.enums.TaskPriority;
import com.example.tasking_tracker.enums.TaskStatus;
import com.example.tasking_tracker.exception.ResourceNotFoundException;
import com.example.tasking_tracker.repository.*;
import com.example.tasking_tracker.service.TaskService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    private Task task;
    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).username("user1").email("test@test.com").password("pass").build();
        project = Project.builder().id(1L).name("Proj").owner(user).build();
        task = Task.builder()
                .id(1L)
                .title("Task Title")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now())
                .project(project)
                .assignee(user)
                .build();
    }

    @Test
    void shouldCreateTask() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task created = taskService.createTask(1L, task);

        assertThat(created).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Task Title");
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void shouldThrowWhenProjectNotFound() {
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> taskService.createTask(2L, task))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
