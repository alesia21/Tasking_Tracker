package com.example.tasking_tracker;


import com.example.tasking_tracker.entity.Project;
import com.example.tasking_tracker.entity.User;
import com.example.tasking_tracker.repository.ProjectRepository;
import com.example.tasking_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void saveAndFindById() {
        User owner = userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("secretpass")
                .build());

        Project project = Project.builder()
                .name("Test Project")
                .description("Desc")
                .owner(owner)
                .build();

        Project saved = projectRepository.save(project);

        assertNotNull(saved.getId());
        assertEquals("Test Project", saved.getName());

        Project found = projectRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
    }
}
