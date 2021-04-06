DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%migration_unique_name%') THEN
  -- YOUR MIGRATION CODE
  END IF;
END$$;
