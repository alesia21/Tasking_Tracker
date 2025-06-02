package com.example.tasking_tracker;

import com.example.tasking_tracker.controller.UserController;
import com.example.tasking_tracker.entity.User;
import com.example.tasking_tracker.repository.UserRepository;
import com.example.tasking_tracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserControllerIntegrationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_andGetById() {
        User user = User.builder()
                .username("alesia")
                .email("alesia@example.com")
                .password("password123")
                .build();

        ResponseEntity<User> created = userController.createUser(user);
        assertThat(created.getStatusCode().is2xxSuccessful()).isTrue();

        Long id = created.getBody().getId();

        ResponseEntity<User> found = userController.getUserById(id);
        assertThat(found.getBody()).isNotNull();
        assertThat(found.getBody().getUsername()).isEqualTo("alesia");
    }

    @Test
    void getAllUsers_shouldReturnPaginated() {
        userRepository.save(User.builder()
                .username("john")
                .email("john@example.com")
                .password("password1234")
                .build());

        ResponseEntity<List<User>> result = userController.getAllUsers(0, 5);
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotEmpty();
    }
}
