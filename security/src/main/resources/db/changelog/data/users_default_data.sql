--liquibase formatted sql

--changeset krasnopolskyi:1
INSERT INTO user (first_name, last_name, username, password, is_active)
VALUES
    -- trainers
    ('Arnold', 'Schwarzenegger', 'arnold.schwarzenegger', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
    ('Usain', 'Bolt', 'usain.bolt', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
    ('Jillian', 'Michaels', 'jillian.michaels', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
    ('Rich', 'Froning', 'rich.froning', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
    ('Kayla', 'Itsines', 'kayla.itsines', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
    -- trainees
    ('John', 'Doe', 'john.doe', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
    ('Jane', 'Smith', 'jane.smith', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
    ('Mike', 'Tyson', 'mike.tyson', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE),
    ('Serena', 'Williams', 'serena.williams', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', TRUE);

--changeset krasnopolskyi:2
INSERT INTO user_roles (user_id, role)
VALUES
    -- trainers
    ((SELECT id FROM user WHERE username = 'arnold.schwarzenegger'),'TRAINER'),
    ((SELECT id FROM user WHERE username = 'usain.bolt'), 'TRAINER'),
    ((SELECT id FROM user WHERE username = 'jillian.michaels'), 'TRAINER'),
    ((SELECT id FROM user WHERE username = 'rich.froning'), 'TRAINER'),
    ((SELECT id FROM user WHERE username = 'kayla.itsines'), 'TRAINER'),
    -- trainees
    ((SELECT id FROM user WHERE username = 'john.doe'), 'TRAINEE'),
    ((SELECT id FROM user WHERE username = 'jane.smith'), 'TRAINEE'),
    ((SELECT id FROM user WHERE username = 'mike.tyson'), 'TRAINEE'),
    ((SELECT id FROM user WHERE username = 'serena.williams'), 'TRAINEE');
