--liquibase formatted sql

--changeset krasnopolskyi:1
INSERT INTO training_type (training_type_name) VALUES
    ('Bodybuilding'),
    ('Cardio'),
    ('Weight Loss'),
    ('CrossFit'),
    ('HIIT');
