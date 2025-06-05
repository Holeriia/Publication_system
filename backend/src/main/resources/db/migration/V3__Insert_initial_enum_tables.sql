-- Степени
INSERT INTO academic_degree (id, name) VALUES
    (gen_random_uuid(), 'кандидат наук'),
    (gen_random_uuid(), 'доктор наук'),
    (gen_random_uuid(), 'пока отсутствует');

-- Звания
INSERT INTO academic_title (id, name) VALUES
    (gen_random_uuid(), 'доцент'),
    (gen_random_uuid(), 'профессор');

-- Должности
INSERT INTO employee_post (id, name) VALUES
    (gen_random_uuid(), 'техник'),
    (gen_random_uuid(), 'инженер'),
    (gen_random_uuid(), 'заведующий лабораторией'),
    (gen_random_uuid(), 'старший преподаватель'),
    (gen_random_uuid(), 'заведующий кафедрой'),
    (gen_random_uuid(), 'ассистент'),
    (gen_random_uuid(), 'доцент'),
    (gen_random_uuid(), 'профессор'),
    (gen_random_uuid(), 'почасовик'),
    (gen_random_uuid(), 'другое');

-- Города
INSERT INTO city (id, name) VALUES (gen_random_uuid(), 'Москва');
INSERT INTO city (id, name) VALUES (gen_random_uuid(), 'Санкт-Петербург');
INSERT INTO city (id, name) VALUES (gen_random_uuid(), 'Нижний Новгород');
INSERT INTO city (id, name) VALUES (gen_random_uuid(), 'Дзержинск');
INSERT INTO city (id, name) VALUES (gen_random_uuid(), 'Тюмень');