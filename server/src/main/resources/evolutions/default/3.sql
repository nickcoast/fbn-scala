
-- !Ups

-- baby_names
-- allow nulls for baby_date and add baby_history column

-- add baby_history column. Using NOT NULL DEFAULT '' means that CSVs used for
-- postgres COPY must have "" for empty values otherwise interpreted as NULL
-- and rejected.
ALTER TABLE baby_names ADD COLUMN baby_history VARCHAR(1000) NOT NULL DEFAULT '';
CREATE INDEX IF NOT EXISTS baby_history_index ON baby_names USING BTREE (baby_history);

ALTER TABLE baby_names
    ALTER COLUMN baby_date DROP NOT NULL;
ALTER TABLE baby_names
    ALTER COLUMN baby_date SET DEFAULT NULL;

-- eras
-- allow NULL in start_date for names with unknown origin time
ALTER TABLE eras
    ALTER COLUMN start_date DROP NOT NULL;
ALTER TABLE eras
    ALTER COLUMN start_date SET DEFAULT NULL;


-- !Downs

-- baby_names
ALTER TABLE baby_names DROP COLUMN baby_history;
DROP INDEX IF EXISTS baby_history_index;
-- DROP INDEX baby_history_index; -- gets dropped automatically in above query

ALTER TABLE baby_names
    ALTER COLUMN baby_date SET NOT NULL;
ALTER TABLE baby_names
    ALTER COLUMN baby_date DROP DEFAULT;

-- eras

ALTER TABLE eras
    ALTER COLUMN start_date SET NOT NULL;
ALTER TABLE eras
    ALTER COLUMN start_date DROP DEFAULT;
