package com.example.tasking_tracker;

import com.example.tasking_tracker.entity.Project;
import com.example.tasking_tracker.entity.User;
import com.example.tasking_tracker.exception.ResourceNotFoundException;
import com.example.tasking_tracker.repository.ProjectRepository;
import com.example.tasking_tracker.repository.UserRepository;
import com.example.tasking_tracker.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProject_shouldSaveWithValidOwner() {
        User owner = new User();
        owner.setId(1L);

        Project input = new Project();
        input.setName("Test Project");
        input.setOwner(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenReturn(input);

        Project saved = projectService.createProject(input);

        assertEquals("Test Project", saved.getName());
        verify(projectRepository).save(input);
    }

    @Test
    void getProjectById_shouldReturnProject() {
        Project project = new Project();
        project.setId(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Project result = projectService.getProjectById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getProjectById_shouldThrow_whenNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(99L));
    }

    @Test
    void getAllProjects_shouldReturnPage() {
        Page<Project> page = new PageImpl<>(List.of(new Project(), new Project()));
        Pageable pageable = PageRequest.of(0, 10);

        when(projectRepository.findAll(pageable)).thenReturn(page);

        Page<Project> result = projectService.getAllProjects(pageable);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void updateProject_shouldModifyNameAndDescription() {
        Project existing = new Project();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setDescription("Old Desc");

        Project incoming = new Project();
        incoming.setName("New Name");
        incoming.setDescription("New Desc");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenReturn(existing);

        Project result = projectService.updateProject(1L, incoming);

        assertEquals("New Name", result.getName());
        assertEquals("New Desc", result.getDescription());
    }

    @Test
    void deleteProject_shouldRemoveIt() {
        Project existing = new Project();
        existing.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));

        projectService.deleteProject(1L);
        verify(projectRepository).delete(existing);
    }
}
