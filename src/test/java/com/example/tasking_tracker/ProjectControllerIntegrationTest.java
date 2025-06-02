package com.example.tasking_tracker;

import com.example.tasking_tracker.entity.Project;
import com.example.tasking_tracker.entity.User;
import com.example.tasking_tracker.repository.ProjectRepository;
import com.example.tasking_tracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProjectController
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;

    private Long ownerId;

    @BeforeEach
    void setupUser() {
        userRepository.deleteAll(); // Pastron tabelën
        String uniqueUsername = "owner_" + UUID.randomUUID().toString().substring(0, 5);

        User owner = User.builder()
                .username(uniqueUsername)
                .email(uniqueUsername + "@example.com")
                .password("password123")
                .build();
        ownerId = userRepository.save(owner).getId();
    }

    @Test
    void testCreateProject() throws Exception {
        Project project = Project.builder()
                .name("Test Project")
                .description("Integration test project")
                .owner(User.builder().id(ownerId).build())
                .build();

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Integration test project"))
                .andExpect(jsonPath("$.owner.id").value(ownerId));
    }
}
