DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%value_to_path_in_label_attribute%') THEN
    UPDATE s_label_attribute SET "path" = value WHERE "path" IS NOT NULL;
  END IF;
END$$;
