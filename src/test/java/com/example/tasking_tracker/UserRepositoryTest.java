package com.example.tasking_tracker;


import com.example.tasking_tracker.entity.User;
import com.example.tasking_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindById() {
        User user = User.builder()
                .username("jona")
                .email("jona@example.com")
                .password("12345678")
                .build();

        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("jona", found.get().getUsername());
    }
}
