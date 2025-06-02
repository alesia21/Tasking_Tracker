package com.example.tasking_tracker.repository;

import com.example.tasking_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
}