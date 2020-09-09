DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '% add_dimension_columns_for_statistic_counters %')
  THEN
    ALTER TABLE statistic_counters
      ADD COLUMN dimension1 text,
      ADD COLUMN dimension2 text,
      ADD COLUMN dimension3 text;
  END IF;
END$$;