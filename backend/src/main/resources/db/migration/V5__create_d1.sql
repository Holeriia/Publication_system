WITH
org_type AS (
  SELECT id FROM organisation_type WHERE name = 'образовательная орг-ция ДПО' LIMIT 1
),
city_id AS (
  SELECT id FROM city WHERE name = 'Москва' LIMIT 1
),
ins_org AS (
  INSERT INTO organisation (id, full_name, short_name, type_id, city_id)
  SELECT gen_random_uuid(),
         'Автономная некоммерческая организация дополнительного профессионального образования "Учебный центр РРС"',
         'Учебный центр РРС',
         (SELECT id FROM org_type),
         (SELECT id FROM city_id)
  WHERE NOT EXISTS (
    SELECT 1 FROM organisation WHERE short_name = 'Учебный центр РРС'
  )
  RETURNING id
),
org AS (
  SELECT id FROM organisation WHERE short_name = 'Учебный центр РРС' LIMIT 1
),
ins_prof_dev AS (
  INSERT INTO professional_development (id, name, date_start, date_end, organisation_id)
  SELECT gen_random_uuid(),
         'Дополнительная профессиональная программа повышения квалификации "Разработка приложений на платформе Jmix"',
         DATE '2024-12-10',
         DATE '2024-12-13',
         (SELECT id FROM org)
  WHERE NOT EXISTS (
    SELECT 1 FROM professional_development
    WHERE name = 'Дополнительная профессиональная программа повышения квалификации "Разработка приложений на платформе Jmix"'
      AND organisation_id = (SELECT id FROM org)
  )
  RETURNING id
),
ach_type AS (
  SELECT id FROM achievement_type WHERE name = 'повышение квалификации / курсы' LIMIT 1
),
prof_dev AS (
  SELECT id FROM ins_prof_dev
  UNION
  SELECT id FROM professional_development
  WHERE name = 'Дополнительная профессиональная программа повышения квалификации "Разработка приложений на платформе Jmix"'
    AND organisation_id = (SELECT id FROM org)
  LIMIT 1
),

ins_achievent AS (
  INSERT INTO achievent (
      id, type_id, professional_development_id, publication_id, other_id,
      methodical_activity_id, pattents_and_registration_id, date, comment
  )
  SELECT
      gen_random_uuid(),
      (SELECT id FROM ach_type),
      (SELECT id FROM prof_dev),
      NULL,
      NULL,
      NULL,
      NULL,
      DATE '2024-12-10',
      'Успешное прохождение курса Jmix'
  WHERE NOT EXISTS (
    SELECT 1 FROM achievent
    WHERE professional_development_id = (SELECT id FROM prof_dev)
      AND type_id = (SELECT id FROM ach_type)
      AND date = DATE '2024-12-10'
  )
  RETURNING id
),
ach AS (
  SELECT id FROM achievent
  WHERE professional_development_id = (SELECT id FROM prof_dev)
    AND type_id = (SELECT id FROM ach_type)
    AND date = DATE '2024-12-10'
  LIMIT 1
),
author_serova AS (
  SELECT a.id FROM author a
  JOIN employee e ON a.employee_id = e.id
  WHERE e.last_name = 'Серова' LIMIT 1
),
author_shapkina AS (
  SELECT a.id FROM author a
  WHERE a.last_name = 'Шапкина' LIMIT 1
),
author_zhevnerchuk AS (
  SELECT a.id FROM author a
  JOIN employee e ON a.employee_id = e.id
  WHERE e.last_name = 'Жевнерчук' LIMIT 1
),
ins_authors AS (
  INSERT INTO achievement_author (id, achievent_id, author_id, author_order)
  SELECT gen_random_uuid(), (SELECT id FROM ach), a.id, a.ord
  FROM (
    VALUES
      ((SELECT id FROM author_serova), 2),
      ((SELECT id FROM author_shapkina), 3),
      ((SELECT id FROM author_zhevnerchuk), 1)
  ) AS a(id, ord)
  WHERE NOT EXISTS (
    SELECT 1 FROM achievement_author
    WHERE achievent_id = (SELECT id FROM ach)
      AND author_id = a.id
  )
  AND a.id IS NOT NULL
)
SELECT 'done';
