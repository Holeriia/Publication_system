-- 1. Найдём id нужного типа достижения "методическая деятельность"
WITH ach_type AS (
    SELECT id FROM achievement_type WHERE name = 'методическая деятельность' LIMIT 1
),
-- 2. Найдём id типа методической деятельности "электронный курс"
meth_type AS (
    SELECT id FROM methodical_type WHERE name = 'электронный курс' LIMIT 1
),
-- 3. Вставим запись в methodical_activity
insert_methodical AS (
    INSERT INTO methodical_activity (name, type_id, number_of_pages, file_path)
    SELECT
      'Электронный курс в системе электронного обучения НГТУ (Moodle) "Организация ЭВМ"',
      (SELECT id FROM meth_type),
      NULL,
      NULL
    RETURNING id
),
-- 4. Вставим запись в achievent, связав с methodical_activity
insert_achiev AS (
    INSERT INTO achievent (type_id, methodical_activity_id, date, comment)
    SELECT
      (SELECT id FROM ach_type),
      (SELECT id FROM insert_methodical),
      DATE '2025-05-01',
      'ЭЛ курс'
    RETURNING id
)
-- 5. Вставим связи авторов с достижением, предполагая, что авторы есть в таблице author
INSERT INTO achievement_author (achievent_id, author_id, author_order)
SELECT
  (SELECT id FROM insert_achiev),
  a.id,
  ROW_NUMBER() OVER ()
FROM author a
WHERE a.last_name IN ('Зеленский', 'Кулясов', 'Серова')
ORDER BY a.last_name;
