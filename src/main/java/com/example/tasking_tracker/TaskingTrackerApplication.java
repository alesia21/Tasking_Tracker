package com.example.tasking_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TaskingTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskingTrackerApplication.class, args);
    }

}
