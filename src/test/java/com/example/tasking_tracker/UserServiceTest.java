package com.example.tasking_tracker;



import com.example.tasking_tracker.entity.User;
import com.example.tasking_tracker.exception.ResourceNotFoundException;
import com.example.tasking_tracker.repository.UserRepository;
import com.example.tasking_tracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldSaveUser() {
        User user = new User();
        user.setUsername("alesia");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("alesia", result.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alesia");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals("alesia", result.getUsername());
    }

    @Test
    void getUserById_shouldThrowException_whenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void getAllUsers_shouldReturnPaginatedList() {
        Page<User> page = new PageImpl<>(List.of(new User(), new User()));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getAllUsers(pageable);

        assertEquals(2, result.getContent().size());
    }




}
