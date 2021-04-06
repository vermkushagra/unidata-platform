DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%add_security_labels_default_values_to_role%') THEN
    CREATE TABLE s_user_s_label_attribute_value(
      s_object_id INT NOT NULL
        CONSTRAINT fk_s_user_id REFERENCES s_user ON DELETE CASCADE ON UPDATE CASCADE,
      s_label_attribute_value_id INT NOT NULL
        CONSTRAINT fk_s_label_attribute_value_id REFERENCES s_label_attribute_value ON DELETE CASCADE ON UPDATE CASCADE,
      UNIQUE (s_object_id, s_label_attribute_value_id)
    );

    INSERT INTO s_user_s_label_attribute_value(s_object_id, s_label_attribute_value_id) SELECT s_user_id, id FROM s_label_attribute_value;

    ALTER TABLE s_label_attribute_value DROP COLUMN s_user_id;

    CREATE TABLE s_role_s_label_attribute_value(
      s_object_id INT NOT NULL
        CONSTRAINT fk_s_role_id REFERENCES s_role ON DELETE CASCADE ON UPDATE CASCADE,
      s_label_attribute_value_id INT NOT NULL
        CONSTRAINT fk_s_label_attribute_value_id REFERENCES s_label_attribute_value ON DELETE CASCADE ON UPDATE CASCADE,
      UNIQUE (s_object_id, s_label_attribute_value_id)
    );
  END IF;
END$$;
