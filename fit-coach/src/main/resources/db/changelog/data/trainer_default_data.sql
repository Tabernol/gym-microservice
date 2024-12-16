--liquibase formatted sql

--changeset krasnopolskyi:1
INSERT INTO user (id,first_name, last_name, username, is_active)
VALUES (1, 'Arnold', 'Schwarzenegger', 'arnold.schwarzenegger', TRUE),
       (2, 'Usain', 'Bolt', 'usain.bolt', TRUE),
       (3, 'Jillian', 'Michaels', 'jillian.michaels', TRUE),
       (4, 'Rich', 'Froning', 'rich.froning', TRUE),
       (5, 'Kayla', 'Itsines', 'kayla.itsines', TRUE);

--changeset krasnopolskyi:2
INSERT INTO trainer (user_id, specialization_id)
VALUES ((SELECT id FROM user WHERE username = 'arnold.schwarzenegger'),
        (SELECT id FROM training_type WHERE training_type_name = 'Bodybuilding')),

       ((SELECT id FROM user WHERE username = 'usain.bolt'),
        (SELECT id FROM training_type WHERE training_type_name = 'Cardio')),

       ((SELECT id FROM user WHERE username = 'jillian.michaels'),
        (SELECT id FROM training_type WHERE training_type_name = 'Weight Loss')),

       ((SELECT id FROM user WHERE username = 'rich.froning'),
        (SELECT id FROM training_type WHERE training_type_name = 'CrossFit')),

       ((SELECT id FROM user WHERE username = 'kayla.itsines'),
        (SELECT id FROM training_type WHERE training_type_name = 'HIIT'));

-- --changeset krasnopolskyi:3
-- INSERT INTO user_roles (user_id, role)
-- VALUES ((SELECT id FROM user WHERE username = 'arnold.schwarzenegger'),'TRAINER'),
--        ((SELECT id FROM user WHERE username = 'usain.bolt'), 'TRAINER'),
--        ((SELECT id FROM user WHERE username = 'jillian.michaels'), 'TRAINER'),
--        ((SELECT id FROM user WHERE username = 'rich.froning'), 'TRAINER'),
--        ((SELECT id FROM user WHERE username = 'kayla.itsines'), 'TRAINER');


