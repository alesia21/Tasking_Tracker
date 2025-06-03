package com.example.tasking_tracker.repository;

import com.example.tasking_tracker.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}
//Project is the entity type
//by extending JpaRepository we get methods like save() ; findbyid()  ; findAll() ;deletebyid () and so on.
