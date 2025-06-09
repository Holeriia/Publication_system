-- 1. Создание организации Роспатент
INSERT INTO organisation (full_name, short_name, type_id, city_id)
SELECT
    'Федеральная служба по интеллектуальной собственности',
    'Роспатент',
    ot.id,
    c.id
FROM organisation_type ot, city c
WHERE ot.name = 'федеральный орган исп.власти'
  AND c.name = 'Москва'
RETURNING id;

-- 2. Добавление записи в pattents_and_registration
INSERT INTO pattents_and_registration (name, number, registration_date, note)
VALUES (
    'Клиент-серверное приложение для моделирования арифметико-логического устройства с программируемым устройством управления',
    '2024615978',
    '2024-03-14',
    NULL
)
RETURNING id;

-- 3. Добавление достижения с ссылкой на регистрацию программы
INSERT INTO achievent (type_id, pattents_and_registration_id, comment, date)
SELECT
    at.id,
    pr.id,
    'Государственная регистрация программы',
    pr.registration_date
FROM achievement_type at, pattents_and_registration pr
WHERE at.name = 'патент или гос регистрация программы'
  AND pr.number = '2024615978'
RETURNING id;

-- 4. Привязка авторов (Кулясов, Панкратьев, Серова) к достижению
WITH last_ach AS (
    SELECT id FROM achievent
    WHERE comment = 'Государственная регистрация программы' AND date = '2024-03-14'
    ORDER BY id DESC LIMIT 1
),
authors AS (
    SELECT id, last_name FROM author WHERE last_name IN ('Кулясов', 'Панкратьев', 'Серова')
)
INSERT INTO achievement_author (achievent_id, author_id, author_order)
SELECT
    (SELECT id FROM last_ach),
    a.id,
    ROW_NUMBER() OVER (ORDER BY a.last_name)
FROM authors a;



-- 1. Добавляем запись в other для Серовой
INSERT INTO other (name, description, file_path)
VALUES (
    'Создание системы достижений',
    'Революционное изобретение, которое заставляет сотрудников кафедры гордиться каждым шагом в науке и методике, включая неудачные попытки и вдохновляющие победы!',
    NULL
)
RETURNING id;

-- 2. Создаем достижение с ссылкой на запись в other
INSERT INTO achievent (type_id, other_id, comment, date)
SELECT
    at.id,
    o.id,
    'Первое достижение по системе достижений',
    CURRENT_DATE
FROM achievement_type at, other o
WHERE at.name = 'другое'
  AND o.name = 'Создание системы достижений'
ORDER BY o.id DESC
LIMIT 1
RETURNING id;

-- 3. Связываем достижение с автором Серовой
WITH last_ach AS (
    SELECT id FROM achievent
    WHERE comment = 'Первое достижение по системе достижений'
    ORDER BY id DESC LIMIT 1
),
author_serova AS (
    SELECT id FROM author WHERE last_name = 'Серова' LIMIT 1
)
INSERT INTO achievement_author (achievent_id, author_id, author_order)
SELECT
    (SELECT id FROM last_ach),
    (SELECT id FROM author_serova),
    1;

