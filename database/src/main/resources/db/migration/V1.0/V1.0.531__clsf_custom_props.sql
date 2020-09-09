DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%clsf_custom_props%') THEN
    ALTER TABLE clsf_node_attr ADD COLUMN custom_props TEXT;
    ALTER TABLE clsf_node ADD COLUMN custom_props TEXT;
  END IF;
END$$;
