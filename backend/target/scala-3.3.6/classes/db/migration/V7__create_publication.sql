-- 1. Организация НГТУ (если не существует)
WITH org_ins AS (
  INSERT INTO organisation (id, full_name, short_name, type_id, city_id)
  SELECT
      gen_random_uuid(),
      'Федеральное государственное бюджетное образовательное учреждение высшего образования "Нижегородский государственный технический университет им. Р.Е. Алексеева"',
      'НГТУ',
      ot.id,
      c.id
  FROM organisation_type ot, city c
  WHERE ot.name = 'образовательная орг-ция ВО'
    AND c.name = 'Нижний Новгород'
    AND NOT EXISTS (
      SELECT 1 FROM organisation WHERE short_name = 'НГТУ'
    )
  RETURNING id
),
org AS (
  SELECT id FROM org_ins
  UNION ALL
  SELECT id FROM organisation WHERE short_name = 'НГТУ'
  LIMIT 1
),

-- 2. Конференция БТН-2025
conf_ins AS (
  INSERT INTO conference (id, name, short_name, level_id, organisation_id, date, participants_count)
  SELECT
      gen_random_uuid(),
      'XXIV Всероссийская молодежная научно-техническая конференция «БУДУЩЕЕ ТЕХНИЧЕСКОЙ НАУКИ», посвященная 80-летию Победы в Великой Отечественной войне и 80-летию атомной промышленности России',
      'БТН-2025',
      cl.id,
      (SELECT id FROM org),
      DATE '2025-05-29',
      NULL
  FROM conference_level cl
  WHERE cl.name = 'всероссийская'
    AND NOT EXISTS (
      SELECT 1 FROM conference WHERE short_name = 'БТН-2025'
    )
  RETURNING id
),
conf AS (
  SELECT id FROM conf_ins
  UNION ALL
  SELECT id FROM conference WHERE short_name = 'БТН-2025'
  LIMIT 1
),

-- 3. Сборник материалов конференции
coll_ins AS (
  INSERT INTO collection_data (id, name, type_id, file_path, link)
  SELECT
      gen_random_uuid(),
      'Сборник материалов XXIV Всероссийской молодежной научно-технической конференции, посвященной 80-летию Победы в Великой Отечественной войне и 80-летию атомной промышленности России',
      ct.id,
      NULL,
      'https://www.nntu.ru/frontend/web/ngtu/files/nauka/konf/btn/sbornik-materialov-btn-2025.pdf'
  FROM collection_type ct
  WHERE ct.name = 'сборник'
    AND NOT EXISTS (
      SELECT 1 FROM collection_data WHERE link = 'https://www.nntu.ru/frontend/web/ngtu/files/nauka/konf/btn/sbornik-materialov-btn-2025.pdf'
    )
  RETURNING id
),
coll AS (
  SELECT id FROM coll_ins
  UNION ALL
  SELECT id FROM collection_data WHERE link = 'https://www.nntu.ru/frontend/web/ngtu/files/nauka/konf/btn/sbornik-materialov-btn-2025.pdf'
  LIMIT 1
),

-- 4. Связь конференции и сборника (conference_data)
conf_data_ins AS (
  INSERT INTO conference_data (id, conference_id, collection_id, status_id, participation_format_id)
  SELECT
      gen_random_uuid(),
      (SELECT id FROM conf),
      (SELECT id FROM coll),
      status.id,
      fmt.id
  FROM conference_data_status status, participation_format fmt
  WHERE status.name = 'печатное'
    AND fmt.name = 'очное'
    AND NOT EXISTS (
      SELECT 1 FROM conference_data
      WHERE conference_id = (SELECT id FROM conf)
        AND collection_id = (SELECT id FROM coll)
    )
  RETURNING id
),

-- 5. Публикация (здесь нужно указать корректное имя уровня публикации вместо NULL)
pub_ins AS (
  INSERT INTO publication (id, name, level_id, type_id, number_of_pages)
  SELECT
      gen_random_uuid(),
      'АВТОМАТИЗИРОВАННОЕ ПОСТРОЕНИЕ ГРАФА СВЯЗЕЙ МЕЖДУ ДИСЦИПЛИНАМИ НА ОСНОВЕ РПД',
      NULL,  -- уровень публикации отсутствует
      pt.id,
      2
  FROM publication_type pt
  WHERE pt.name = 'тезисы'
    AND NOT EXISTS (
      SELECT 1 FROM publication WHERE name = 'АВТОМАТИЗИРОВАННОЕ ПОСТРОЕНИЕ ГРАФА СВЯЗЕЙ МЕЖДУ ДИСЦИПЛИНАМИ НА ОСНОВЕ РПД'
    )
  RETURNING id
),
pub AS (
  SELECT id FROM pub_ins
  UNION ALL
  SELECT id FROM publication WHERE name = 'АВТОМАТИЗИРОВАННОЕ ПОСТРОЕНИЕ ГРАФА СВЯЗЕЙ МЕЖДУ ДИСЦИПЛИНАМИ НА ОСНОВЕ РПД'
  LIMIT 1
),

-- 6. Связь публикации и сборника (publication_data)
pub_data_ins AS (
  INSERT INTO publication_data (id, publication_id, collection_id, number_pages)
  SELECT
      gen_random_uuid(),
      (SELECT id FROM pub),
      (SELECT id FROM coll),
      '71-72'
  WHERE NOT EXISTS (
    SELECT 1 FROM publication_data
    WHERE publication_id = (SELECT id FROM pub)
      AND collection_id = (SELECT id FROM coll)
  )
  RETURNING id
),

-- 7. Достижение для Серовой (3-е место, диплом)
ach_ins AS (
  INSERT INTO achievent (id, type_id, publication_id, comment, date)
  SELECT
      gen_random_uuid(),
      at.id,
      (SELECT id FROM pub),
      'Диплом 3-е место',
      DATE '2025-05-29'
  FROM achievement_type at
  WHERE at.name = 'публикация'
    AND NOT EXISTS (
      SELECT 1 FROM achievent
      WHERE publication_id = (SELECT id FROM pub)
        AND type_id = at.id
        AND comment = 'Диплом 3-е место'
        AND date = DATE '2025-05-29'
    )
  RETURNING id
),
last_ach AS (
  SELECT id FROM ach_ins
  UNION ALL
  SELECT id FROM achievent
  WHERE comment = 'Диплом 3-е место' AND date = DATE '2025-05-29'
  LIMIT 1
),

-- 8. Привязка авторов к достижению
auths AS (
  SELECT id, last_name FROM author WHERE last_name IN ('Серова', 'Шапкина', 'Жевнерчук')
)
INSERT INTO achievement_author (id, achievent_id, author_id, author_order)
SELECT
    gen_random_uuid(),
    (SELECT id FROM last_ach),
    a.id,
    ROW_NUMBER() OVER (ORDER BY a.last_name)
FROM auths a
WHERE NOT EXISTS (
  SELECT 1 FROM achievement_author
  WHERE achievent_id = (SELECT id FROM last_ach)
    AND author_id = a.id
);
