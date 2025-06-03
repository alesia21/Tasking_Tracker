package com.example.tasking_tracker.repository;

import com.example.tasking_tracker.entity.Task;
import com.example.tasking_tracker.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor { //Supproting for dynamic Specification queries.

    // Find all tasks in a given project (paginated)
    Page<Task> findByProjectId(Long projectId, Pageable pageable);

   List<Task> findByAssigneeId(Long userId);

    //  Find all tasks in a given project filtered by TaskStatus (paginated)
    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);

    // 3Find all tasks that are due on a specific date (paginated)
    Page<Task> findByDueDate(LocalDate dueDate, Pageable pageable);

    //  Find all tasks assigned to a specific user (paginated)
    Page<Task> findByAssigneeId(Long userId, Pageable pageable);

    //Find tasks by the assignee’s username AND the project’s name
    @Query("SELECT t FROM Task t WHERE t.assignee.username = :username AND t.project.name = :projectName")
    List<Task> findTasksByAssigneeUsernameAndProjectName(@Param("username") String username,
                                                         @Param("projectName") String projectName);

}
