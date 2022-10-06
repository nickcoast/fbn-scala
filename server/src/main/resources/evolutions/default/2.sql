
-- !Ups


-- Babynames
CREATE TABLE baby_names(
    id SERIAL PRIMARY KEY,
    baby_name VARCHAR(100) NOT NULL,
    baby_date TIMESTAMPTZ NOT NULL,
    baby_era_id INT NULL DEFAULT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT baby_name_unique UNIQUE (baby_name)
);
--CREATE UNIQUE INDEX baby_name_unique ON baby_names USING BTREE (baby_name);
CREATE INDEX baby_date_index ON baby_names USING BTREE (baby_date);
CREATE INDEX baby_era_id_index ON baby_names USING BTREE (baby_era_id);


-- baby eras
CREATE TABLE eras (
    id SERIAL PRIMARY KEY,
    era_name VARCHAR(100) NOT NULL,
    story TEXT NULL DEFAULT NULL,
    start_date TIMESTAMPTZ NOT NULL,
    end_date TIMESTAMPTZ NULL DEFAULT NULL, -- the future is unknown!
    -- location(s) - TODO: create locations table, probably many-to-many
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT era_name_unique UNIQUE (era_name)
);
CREATE INDEX era_name_index ON eras USING BTREE (era_name);
CREATE INDEX era_start_date_index ON eras USING BTREE (start_date);
CREATE INDEX era_end_date_index ON eras USING BTREE (end_date);

CREATE TABLE parent_names (
    id SERIAL PRIMARY KEY,
    parent_name VARCHAR(63) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT parent_names_unique UNIQUE (parent_name)
);

CREATE TABLE parent_fact_categories (
    id SERIAL PRIMARY KEY, -- use this id as well as user-inputted value to help get babyname
    category_name VARCHAR(63) NOT NULL,
    category_desc VARCHAR(63) NULL DEFAULT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT parent_fact_categories_name_unique UNIQUE (category_name)
);

INSERT INTO parent_fact_categories (category_name, category_desc)
VALUES ('Favorite Color',DEFAULT),
       ('Least Favorite Color',DEFAULT),
       ('Favorite Irrational Number',DEFAULT),
       ('Least Favorite Irrational Number',DEFAULT),
       ('Kurt Russell is your favorite actor (yes)',DEFAULT),
       ('Are you forklift certified?',DEFAULT),
       ('Apples or Oranges?',DEFAULT);

-- store results of parents' babyname search. Probably won't use this for first implementation
CREATE TABLE baby_parents (
    id SERIAL PRIMARY KEY ,
    parent_name_id_1 INT NOT NULL, -- lower id parent name
    parent_name_id_2 INT NOT NULL, -- higher id parent name
    -- parent_name_order_reversed BOOL NOT NULL DEFAULT 0, -- if
    baby_name_id INT NOT NULL,
    baby_order INT NOT NULL DEFAULT 1,
    CONSTRAINT baby_parents_baby_name_fk FOREIGN KEY (baby_name_id) REFERENCES baby_names(id),
    CONSTRAINT parent_id_1_lt_parent_id_2
        CHECK ( parent_name_id_1 < baby_parents.parent_name_id_2),
    CONSTRAINT baby_parents_parent_name_1_fk FOREIGN KEY (parent_name_id_1) REFERENCES parent_names(id),
    CONSTRAINT baby_parents_parent_name_2_fk FOREIGN KEY (parent_name_id_2) REFERENCES parent_names(id)
);
CREATE UNIQUE INDEX baby_parents_unique ON baby_parents (parent_name_id_1,parent_name_id_2);


-- baby queries log. won't use in first implementation.
CREATE TABLE baby_log (
    baby_log_id SERIAL PRIMARY KEY,
    baby_parents_id INT NOT NULL,
    baby_name_id INT NOT NULL,
    request_ip inet NULL DEFAULT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT baby_log_baby_parents_fk FOREIGN KEY (baby_parents_id) REFERENCES baby_parents(id),
    CONSTRAINT baby_log_baby_name_fk FOREIGN KEY (baby_name_id) REFERENCES baby_names(id)
);




-- !Downs

DROP TABLE baby_names;
DROP TABLE eras;
DROP TABLE parent_names;
DROP TABLE parent_fact_categories;
DROP TABLE baby_parents;
DROP TABLE baby_log;
