-- Таблица organisation
CREATE TABLE organisation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name TEXT NOT NULL,
    short_name TEXT NOT NULL,
    type_id UUID REFERENCES organisation_type(id),
    city_id UUID REFERENCES city(id)
);

-- Таблица employee (обновлённая)
CREATE TABLE employee (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    patronymic TEXT,

    degree_id UUID REFERENCES academic_degree(id),
    title_id UUID REFERENCES academic_title(id),
    post_id UUID REFERENCES employee_post(id),

    -- Дата приёма в вуз
    university_start_date DATE,
    -- Дата приёма на предприятие
    industry_start_date DATE,
    -- Комментарии по трудовой истории
    experience_comment TEXT,
    -- Образование по диплому
    diploma_education TEXT
);

-- Таблица author
CREATE TABLE author (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    patronymic TEXT,
    is_employee BOOLEAN NOT NULL,
    employee_id UUID REFERENCES employee(id),
    affiliation TEXT
);

-- Таблица other
CREATE TABLE other (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    description TEXT,
    file_path TEXT
);

-- Таблица professional_development
CREATE TABLE professional_development (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    date_start DATE,
    date_end DATE,
    organisation_id UUID REFERENCES organisation(id)
);

-- Таблица methodical_activity
CREATE TABLE methodical_activity (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    type_id UUID REFERENCES methodical_type(id),
    number_of_pages INT,
    file_path TEXT
);

-- Таблица pattents_and_registration
CREATE TABLE pattents_and_registration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    number TEXT,
    registration_date DATE,
    note TEXT
);

-- Таблица conference (обновлённая)
CREATE TABLE conference (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name TEXT NOT NULL,
    short_name TEXT,
    level_id UUID REFERENCES conference_level(id),
    organisation_id UUID REFERENCES organisation(id),
    date DATE,
    regulation_file TEXT,

    -- Численность участников (необязательное поле)
    participants_count INT
);

-- Таблица collection_data
CREATE TABLE collection_data (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    type_id UUID REFERENCES collection_type(id),
    file_path TEXT,
    link TEXT
);

-- Таблица publication
CREATE TABLE publication (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    level_id UUID REFERENCES publication_level(id),
    type_id UUID REFERENCES publication_type(id),
    number_of_pages INT,
    number_ez TEXT,
    number_ek TEXT,
    file_path TEXT
);

-- Таблица conference_data с форматом участия
CREATE TABLE conference_data (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conference_id UUID REFERENCES conference(id),
    collection_id UUID REFERENCES collection_data(id),
    status_id UUID REFERENCES conference_data_status(id),
    participation_format_id UUID REFERENCES participation_format(id)
);

-- Таблица publication_data
CREATE TABLE publication_data (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    publication_id UUID REFERENCES publication(id),
    collection_id UUID REFERENCES collection_data(id),
    number_pages TEXT,
    link TEXT
);

-- Таблица achievent (обновлённая)
CREATE TABLE achievent (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type_id UUID REFERENCES achievement_type(id) NOT NULL,

    -- Ссылки на конкретный объект достижения (только одна может быть заполнена)
    publication_id UUID REFERENCES publication(id),
    other_id UUID REFERENCES other(id),
    methodical_activity_id UUID REFERENCES methodical_activity(id),
    professional_development_id UUID REFERENCES professional_development(id),
    pattents_and_registration_id UUID REFERENCES pattents_and_registration(id),

    -- Комментарий (например: "1-е место на конференции")
    comment TEXT,

    -- Путь к грамоте или файлу награды
    reward_file TEXT,

    -- Дата достижения
    date DATE,

    -- Ограничение: только одна из ссылок может быть заполнена
    CHECK (
        (publication_id IS NOT NULL)::int +
        (other_id IS NOT NULL)::int +
        (methodical_activity_id IS NOT NULL)::int +
        (professional_development_id IS NOT NULL)::int +
        (pattents_and_registration_id IS NOT NULL)::int = 1
    )
);

-- Таблица achievement_author (упрощённая)
CREATE TABLE achievement_author (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    achievent_id UUID REFERENCES achievent(id) ON DELETE CASCADE,
    author_id UUID REFERENCES author(id) ON DELETE CASCADE,

    -- Очерёдность автора
    author_order INT
);


-- Индексы
CREATE UNIQUE INDEX ux_achievement_author_achievent_author ON achievement_author(achievent_id, author_id);

CREATE INDEX idx_organisation_type ON organisation(type_id);
CREATE INDEX idx_organisation_city ON organisation(city_id);

CREATE INDEX idx_employee_degree ON employee(degree_id);
CREATE INDEX idx_employee_title ON employee(title_id);
CREATE INDEX idx_employee_post ON employee(post_id);

CREATE INDEX idx_author_employee ON author(employee_id);

CREATE INDEX idx_achievent_type ON achievent(type_id);

CREATE INDEX idx_conference_level ON conference(level_id);
CREATE INDEX idx_conference_organisation ON conference(organisation_id);

CREATE INDEX idx_collection_type ON collection_data(type_id);

CREATE INDEX idx_publication_level ON publication(level_id);
CREATE INDEX idx_publication_type ON publication(type_id);

CREATE INDEX idx_confdata_conference ON conference_data(conference_id);
CREATE INDEX idx_confdata_collection ON conference_data(collection_id);
CREATE INDEX idx_confdata_status ON conference_data(status_id);
CREATE INDEX idx_confdata_participation_format ON conference_data(participation_format_id);

CREATE INDEX idx_pubdata_publication ON publication_data(publication_id);
CREATE INDEX idx_pubdata_collection ON publication_data(collection_id);

CREATE INDEX idx_profdev_organisation ON professional_development(organisation_id);
CREATE INDEX idx_methodical_type ON methodical_activity(type_id);

CREATE INDEX idx_achievement_author_achievent ON achievement_author(achievent_id);
CREATE INDEX idx_achievement_author_author ON achievement_author(author_id);