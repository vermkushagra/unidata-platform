DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%clsf_attrs_order%') THEN
    ALTER TABLE clsf_node_attr ADD COLUMN "order" INT DEFAULT 0;
  END IF;
END$$;
