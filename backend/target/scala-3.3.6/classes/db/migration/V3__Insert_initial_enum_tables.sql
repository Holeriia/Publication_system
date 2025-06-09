-- типы организаций
INSERT INTO organisation_type (id, name) VALUES
    (gen_random_uuid(), 'образовательная орг-ция ВО'),
    (gen_random_uuid(), 'предприятие'),
    (gen_random_uuid(), 'федеральный орган исп. власти'),
    (gen_random_uuid(), 'образовательная орг-ция ДПО');

-- Города
INSERT INTO city (id, name) VALUES
    (gen_random_uuid(), 'Москва'),
    (gen_random_uuid(), 'Санкт-Петербург'),
    (gen_random_uuid(), 'Нижний Новгород'),
    (gen_random_uuid(), 'Дзержинск'),
    (gen_random_uuid(), 'Тюмень'),
    (gen_random_uuid(), 'Пенза'),
    (gen_random_uuid(), 'Ижевск'),
    (gen_random_uuid(), 'Таганрог'),
    (gen_random_uuid(), 'Сочи');

-- Типы достижений
INSERT INTO achievement_type (id, name) VALUES
    (gen_random_uuid(), 'публикация'),
    (gen_random_uuid(), 'методическая деятельность'),
    (gen_random_uuid(), 'патент или гос регистрация программы'),
    (gen_random_uuid(), 'повышение квалификации / курсы'),
    (gen_random_uuid(), 'другое');

-- Степени
INSERT INTO academic_degree (id, name) VALUES
    (gen_random_uuid(), 'кандидат наук'),
    (gen_random_uuid(), 'доктор наук');

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

-- Тип методичек
INSERT INTO methodical_type (id, name) VALUES
    (gen_random_uuid(), 'учебно-методическое пособие'),
    (gen_random_uuid(), 'учебное пособие'),
    (gen_random_uuid(), 'учебник'),
    (gen_random_uuid(), 'электронный курс');

-- Уровень конференции
INSERT INTO conference_level (id, name) VALUES
    (gen_random_uuid(), 'региональная'),
    (gen_random_uuid(), 'всероссийская'),
    (gen_random_uuid(), 'международная'),
    (gen_random_uuid(), 'межвузовская');

-- Тип ресурса публикации
INSERT INTO collection_type (id, name) VALUES
    (gen_random_uuid(), 'сборник'),
    (gen_random_uuid(), 'электронный ресурс');

-- Уровень конференции
INSERT INTO publication_level (id, name) VALUES
    (gen_random_uuid(), 'РИНЦ'),
    (gen_random_uuid(), 'ВАК'),
    (gen_random_uuid(), 'SCOPUS'),
    (gen_random_uuid(), 'Web of science');

-- Тип публикации
INSERT INTO publication_type (id, name) VALUES
    (gen_random_uuid(), 'тезисы'),
    (gen_random_uuid(), 'статья'),
    (gen_random_uuid(), 'монография'),
    (gen_random_uuid(), 'конкурсная работа');

-- Тип статуса готовности источника
INSERT INTO conference_data_status (id, name) VALUES
    (gen_random_uuid(), 'в печати'),
    (gen_random_uuid(), 'печатное'),
    (gen_random_uuid(), 'электронное');

-- Формат участия
INSERT INTO participation_format (id, name) VALUES
    (gen_random_uuid(), 'очное'),
    (gen_random_uuid(), 'заочное'),
    (gen_random_uuid(), 'дистанционное');