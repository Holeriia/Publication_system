INSERT INTO employee (id, last_name, first_name, patronymic, degree_id, title_id, post_id,
                      university_start_date, industry_start_date, diploma_education)
VALUES
(gen_random_uuid(), 'Серова', 'Валерия', 'Павловна',
 NULL,  -- степень отсутствует
 NULL,  -- звание отсутствует
 (SELECT id FROM employee_post WHERE name = 'техник'),
 '2023-10-01',
 '2023-10-01',
 'бакалавр по направлению "Информатика и вычислительная техника"'
),

(gen_random_uuid(), 'Жевнерчук', 'Дмитрий', 'Валериевич',
 (SELECT id FROM academic_degree WHERE name = 'доктор наук'),
 (SELECT id FROM academic_title WHERE name = 'доцент'),
 (SELECT id FROM employee_post WHERE name = 'профессор'),
 '2013-09-01',
 '2013-09-01',
 'хороший человек'
),

(gen_random_uuid(), 'Кулясов', 'Павел', 'Сергеевич',
 (SELECT id FROM academic_degree WHERE name = 'кандидат наук'),
 NULL,
 (SELECT id FROM employee_post WHERE name = 'доцент'),
 '2009-12-01',
 '2015-02-01',
 'магистр по направлению "Информатика и вычислительная техника"'
),

(gen_random_uuid(), 'Зеленский', 'Владимир', 'Павлович',
 (SELECT id FROM academic_degree WHERE name = 'кандидат наук'),
 NULL,
 (SELECT id FROM employee_post WHERE name = 'доцент'),
 NULL,
 NULL,
 NULL
);

INSERT INTO author (id, first_name, last_name, patronymic, is_employee, employee_id, affiliation)
SELECT
  gen_random_uuid(),
  first_name,
  last_name,
  patronymic,
  TRUE,
  id,
  NULL  -- affiliation пока не указан, можно потом заполнить
FROM employee
WHERE NOT EXISTS (
  SELECT 1 FROM author a WHERE a.employee_id = employee.id
);

INSERT INTO author (id, first_name, last_name, patronymic, is_employee, employee_id, affiliation)
SELECT gen_random_uuid(), 'Ирина', 'Шапкина', 'Эдуардовна', FALSE, NULL, NULL
WHERE NOT EXISTS (
  SELECT 1 FROM author WHERE first_name = 'Ирина' AND last_name = 'Шапкина' AND patronymic = 'Эдуардовна'
);

INSERT INTO author (id, first_name, last_name, patronymic, is_employee, employee_id, affiliation)
SELECT gen_random_uuid(), 'Иван', 'Панкратьев', 'Александрович', FALSE, NULL, NULL
WHERE NOT EXISTS (
  SELECT 1 FROM author WHERE first_name = 'Иван' AND last_name = 'Панкратьев' AND patronymic = 'Александрович'
);