package com.example.tasking_tracker.repository;



import com.example.tasking_tracker.entity.TaskActivity;
import com.example.tasking_tracker.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {

    List<TaskActivity> findByTaskOrderByCreatedAtDesc(Task task);
}
