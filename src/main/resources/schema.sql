

-- 1) USERS table
CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- 2) PROJECTS table
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_project_owner
        FOREIGN KEY (owner_id) REFERENCES app_user(id)
        ON DELETE CASCADE
);

-- 3) TASKS table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    due_date DATE,
    created_at TIMESTAMP NOT NULL,
    project_id BIGINT NOT NULL,
    assignee_id BIGINT NOT NULL,
    CONSTRAINT fk_task_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_task_assignee
        FOREIGN KEY (assignee_id) REFERENCES app_user(id)
        ON DELETE CASCADE

);
CREATE TABLE IF NOT EXISTS task_activities
(
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id              BIGINT      NOT NULL,
    performed_by_user_id BIGINT      NOT NULL,
    action               VARCHAR(50) NOT NULL,
    created_at           TIMESTAMP   NOT NULL,
    CONSTRAINT fk_activity_task
        FOREIGN KEY (task_id) REFERENCES tasks (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_activity_user
        FOREIGN KEY (performed_by_user_id) REFERENCES app_user (id)
            ON DELETE CASCADE
);