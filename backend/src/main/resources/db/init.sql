-- MVP schema reference for replacing the in-memory services with a real database.
-- Default accounts are documented in backend/README.md; passwords must be stored as BCrypt hashes in production.

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    display_name VARCHAR(128) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS competition (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP,
    end_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS competition_problem (
    id BIGINT PRIMARY KEY,
    competition_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(64) NOT NULL,
    score INT NOT NULL DEFAULT 0,
    description TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'TODO',
    claimed_by BIGINT,
    solved INT NOT NULL DEFAULT 0,
    FOREIGN KEY (competition_id) REFERENCES competition(id),
    FOREIGN KEY (claimed_by) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS cooperation_log (
    id BIGINT PRIMARY KEY,
    competition_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (competition_id) REFERENCES competition(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS writeup_category (
    id BIGINT PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    parent_id BIGINT
);

CREATE TABLE IF NOT EXISTS writeup (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS writeup_tag (
    id BIGINT PRIMARY KEY,
    writeup_id BIGINT NOT NULL,
    tag VARCHAR(64) NOT NULL,
    FOREIGN KEY (writeup_id) REFERENCES writeup(id)
);

CREATE TABLE IF NOT EXISTS training_task (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(64),
    due_at TIMESTAMP,
    assignee_id BIGINT,
    FOREIGN KEY (assignee_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS training_submission (
    id BIGINT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'SUBMITTED',
    score INT,
    review TEXT,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES training_task(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    level VARCHAR(32) NOT NULL DEFAULT 'INFO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification_read (
    notification_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    read_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (notification_id, user_id),
    FOREIGN KEY (notification_id) REFERENCES notification(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(128) NOT NULL,
    detail TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);
