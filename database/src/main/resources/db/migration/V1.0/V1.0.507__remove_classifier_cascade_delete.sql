DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%remove_classifier_cascade_delete%') THEN
    ALTER TABLE etalons_classifiers DROP CONSTRAINT IF EXISTS fk_etalons_classifiers_name;
    DROP index if EXISTS ix_etalons_classifiers_name;
    CREATE index ix_etalons_classifiers_name ON etalons_classifiers using btree (name);

    ALTER TABLE origins_classifiers DROP CONSTRAINT IF EXISTS fk_origins_classifiers_name;
    DROP index IF EXISTS ix_origins_classifiers_name;
    CREATE index ix_origins_classifiers_name ON origins_classifiers using btree (name);

    ALTER TABLE origins_classifiers DROP CONSTRAINT IF EXISTS fk_origins_classifiers_node_id;
    DROP index IF EXISTS ix_origins_classifiers_node_id;
    CREATE index ix_origins_classifiers_node_id ON origins_classifiers using btree (node_id);
  END IF;
END$$;
