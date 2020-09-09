DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%clsf_lookup%') THEN
    ALTER TABLE clsf_node_attr ALTER COLUMN data_type DROP NOT NULL;
    ALTER TABLE clsf_node_attr ADD COLUMN lookup_entity_type character varying(256);
    ALTER TABLE clsf_node_attr ADD COLUMN lookup_entity_data_type character varying(256);
  END IF;
END$$;
