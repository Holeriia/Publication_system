-- Убедимся, что статус "пока отсутствует" есть в справочниках
INSERT INTO academic_degree (id, name)
SELECT gen_random_uuid(), 'пока отсутствует'
WHERE NOT EXISTS (
    SELECT 1 FROM academic_degree WHERE name = 'пока отсутствует'
);

INSERT INTO academic_title (id, name)
SELECT gen_random_uuid(), 'пока отсутствует'
WHERE NOT EXISTS (
    SELECT 1 FROM academic_title WHERE name = 'пока отсутствует'
);

INSERT INTO employee_post (id, name)
SELECT gen_random_uuid(), 'техник'
WHERE NOT EXISTS (
    SELECT 1 FROM employee_post WHERE name = 'техник'
);

-- Вставка сотрудника Серовой со статусами "пока отсутствует"
WITH degree AS (
    SELECT id FROM academic_degree WHERE name = 'пока отсутствует' LIMIT 1
), title AS (
    SELECT id FROM academic_title WHERE name = 'пока отсутствует' LIMIT 1
), post AS (
    SELECT id FROM employee_post WHERE name = 'техник' LIMIT 1
), ins_emp AS (
    INSERT INTO employee (
        id,
        first_name,
        last_name,
        patronymic,
        degree_id,
        title_id,
        post_id,
        exp,
        seniority,
        diploma_education
    )
    SELECT
        gen_random_uuid(),
        'Валерия',
        'Серова',
        'Павловна',
        degree.id,
        title.id,
        post.id,
        2,
        2,
        'возможно программист'
    FROM degree, title, post
    RETURNING id
)
-- Вставка автора на основе сотрудника
INSERT INTO author (
    id,
    first_name,
    last_name,
    patronymic,
    is_employee,
    employee_id,
    affiliation
)
SELECT
    gen_random_uuid(),
    'Валерия',
    'Серова',
    'Павловна',
    TRUE,
    ins_emp.id,
    NULL
FROM ins_emp;
