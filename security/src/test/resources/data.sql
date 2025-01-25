-- Inserting data into the 'app_user' table
INSERT INTO app_user (first_name, last_name, username, password, is_active)
VALUES
    -- trainers
    ('Arnold', 'Schwarzenegger', 'arnold.schwarzenegger', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', 1),
    ('Usain', 'Bolt', 'usain.bolt', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', 1),
    ('Jillian', 'Michaels', 'jillian.michaels', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', 1),
    ('Rich', 'Froning', 'rich.froning', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', 1),
    ('Kayla', 'Itsines', 'kayla.itsines', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', 1),
    -- trainees
    ('John', 'Doe', 'john.doe', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', 1),
    ('Jane', 'Smith', 'jane.smith', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', 1),
    ('Mike', 'Tyson', 'mike.tyson', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', 1),
    ('Serena', 'Williams', 'serena.williams', '$2a$10$AiRb/bWb1ThKjKMqL6SGO.QXIqdstaQv5EAaykVYtREioaxt7TQKS', 1);

-- Inserting data into the 'user_roles' table
INSERT INTO user_roles (user_id, role)
VALUES
    -- trainers
    ((SELECT id FROM app_user WHERE username = 'arnold.schwarzenegger'), 'TRAINER'),
    ((SELECT id FROM app_user WHERE username = 'usain.bolt'), 'TRAINER'),
    ((SELECT id FROM app_user WHERE username = 'jillian.michaels'), 'TRAINER'),
    ((SELECT id FROM app_user WHERE username = 'rich.froning'), 'TRAINER'),
    ((SELECT id FROM app_user WHERE username = 'kayla.itsines'), 'TRAINER'),
    -- trainees
    ((SELECT id FROM app_user WHERE username = 'john.doe'), 'TRAINEE'),
    ((SELECT id FROM app_user WHERE username = 'jane.smith'), 'TRAINEE'),
    ((SELECT id FROM app_user WHERE username = 'mike.tyson'), 'TRAINEE'),
    ((SELECT id FROM app_user WHERE username = 'serena.williams'), 'TRAINEE');
