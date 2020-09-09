DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%clsf_serialized_values%') THEN
    ALTER TABLE clsf_node_attr ADD COLUMN default_value TEXT;
    UPDATE clsf_node_attr SET default_value = v.value
      FROM (SELECT v.attr_id, json_agg(v.value)::text AS value FROM clsf_node_attrs_values v GROUP BY v.attr_id) AS v
      WHERE id = v.attr_id;
    UPDATE clsf_node_attr SET default_value = default_value::json->0 WHERE attr_type = 'SIMPLE' AND default_value IS NOT NULL;
    DROP TABLE clsf_node_attrs_values;
  END IF;
END$$;
