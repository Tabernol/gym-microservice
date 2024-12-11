--liquibase formatted sql

--changeset krasnopolskyi:1
INSERT INTO user (first_name, last_name, username, password, is_active)
VALUES ('John', 'Doe', 'john.doe', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
       ('Jane', 'Smith', 'jane.smith', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
       ('Mike', 'Tyson', 'mike.tyson', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
       ('Serena', 'Williams', 'serena.williams', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE);

--changeset krasnopolskyi:2
INSERT INTO trainee (user_id, date_of_birth, address)
VALUES ((SELECT id FROM user WHERE username = 'john.doe'), '1990-05-15', '123 Main St, City, Country'),
       ((SELECT id FROM user WHERE username = 'jane.smith'), '1985-08-22', '456 Oak St, City, Country'),
       ((SELECT id FROM user WHERE username = 'mike.tyson'), '1966-06-30', '789 Pine St, City, Country'),
       ((SELECT id FROM user WHERE username = 'serena.williams'), '1981-09-26', '202 Cedar St, City, Country');

--changeset krasnopolskyi:3
INSERT INTO user_roles (user_id, role)
VALUES ((SELECT id FROM user WHERE username = 'john.doe'), 'TRAINEE'),
       ((SELECT id FROM user WHERE username = 'jane.smith'), 'TRAINEE'),
       ((SELECT id FROM user WHERE username = 'mike.tyson'), 'TRAINEE'),
       ((SELECT id FROM user WHERE username = 'serena.williams'), 'TRAINEE');
