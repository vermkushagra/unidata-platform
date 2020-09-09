DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%sandbox_tables%') THEN
    CREATE TABLE IF NOT EXISTS sandbox_data_records(
      id SERIAL PRIMARY KEY,
      entity_name VARCHAR(256) NOT NULL,
      data BYTEA NOT NULL
    );
    IF to_regclass('public.ix_sandbox_data_records_entity_name') IS NULL THEN
      CREATE INDEX ix_sandbox_data_records_entity_name ON sandbox_data_records USING BTREE (entity_name);
    END IF;
  END IF;
END$$;
