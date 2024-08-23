MERGE INTO mpa (id, name) KEY (id)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

MERGE INTO genres (id, name) KEY (id)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

-- MERGE INTO genres (id, name) KEY (id)
--     VALUES (1, 'Comedy'),
--            (2, 'Drama'),
--            (3, 'Cartoon'),
--            (4, 'Thriller'),
--            (5, 'Documentary'),
--            (6, 'Action');