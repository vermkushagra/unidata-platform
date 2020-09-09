DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%clsf_arrays%') THEN
    ALTER TABLE clsf_node_attr ADD COLUMN attr_type VARCHAR(32) DEFAULT 'SIMPLE';
    CREATE TABLE clsf_node_attrs_values(
      attr_id BIGINT NOT NULL
        CONSTRAINT fk_clsf_node_attr_id
        REFERENCES clsf_node_attr
        ON UPDATE CASCADE ON DELETE CASCADE,
      value VARCHAR(2044)
    );
    INSERT INTO clsf_node_attrs_values SELECT id, default_value FROM clsf_node_attr WHERE default_value IS NOT NULL;
    ALTER TABLE clsf_node_attr DROP COLUMN default_value;
  END IF;
END$$;
