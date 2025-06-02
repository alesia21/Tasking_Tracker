package com.example.tasking_tracker.repository;

import com.example.tasking_tracker.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // You can add custom query methods here if needed (e.g., findByOwnerId)
}
