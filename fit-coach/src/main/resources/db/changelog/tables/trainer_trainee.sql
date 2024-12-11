--liquibase formatted sql

--changeset krasnopolskyi:1
CREATE TABLE IF NOT EXISTS trainer_trainee (
    trainer_id BIGINT NOT NULL,
    trainee_id BIGINT NOT NULL,

    FOREIGN KEY (trainer_id) REFERENCES trainer(id),
    FOREIGN KEY (trainee_id) REFERENCES trainee(id)
);
