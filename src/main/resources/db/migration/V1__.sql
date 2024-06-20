CREATE TABLE task
(
    id            INT AUTO_INCREMENT NOT NULL,
    created_time  datetime NULL,
    modified_time datetime NULL,
    name          VARCHAR(255) NULL,
    complete_date date NULL,
    status        SMALLINT NULL,
    user_id       INT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id)
);

CREATE TABLE task_detail
(
    id            INT AUTO_INCREMENT NOT NULL,
    created_time  datetime NULL,
    modified_time datetime NULL,
    name          VARCHAR(255) NULL,
    task_id       INT NULL,
    CONSTRAINT pk_taskdetail PRIMARY KEY (id)
);

CREATE TABLE user
(
    id            INT AUTO_INCREMENT NOT NULL,
    created_time  datetime NULL,
    modified_time datetime NULL,
    username      VARCHAR(255) NOT NULL,
    password      VARCHAR(255) NULL,
    first_name    VARCHAR(255) NULL,
    last_name     VARCHAR(255) NULL,
    email         VARCHAR(255) NOT NULL,
    token         VARCHAR(255) NULL,
    expiry_date   datetime NULL,
    status        SMALLINT NULL,
    `role`        VARCHAR(255) NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE user
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE user
    ADD CONSTRAINT uc_user_username UNIQUE (username);

ALTER TABLE task_detail
    ADD CONSTRAINT FK_TASKDETAIL_ON_TASKID FOREIGN KEY (task_id) REFERENCES task (id);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_USERID FOREIGN KEY (user_id) REFERENCES user (id);

