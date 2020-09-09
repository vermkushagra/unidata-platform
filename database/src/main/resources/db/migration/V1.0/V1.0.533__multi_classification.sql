DO $$
BEGIN
  IF NOT EXISTS(SELECT 1 FROM schema_version WHERE script ILIKE '%multi_classification%') THEN
    drop index if exists uq_etalons_classifiers_name_etalon_id_record;
    drop index if exists ix_etalons_classifiers_name_etalon_id_record;
    create  index ix_etalons_classifiers_name_etalon_id_record on etalons_classifiers using btree (name collate pg_catalog."default", etalon_id_record) where status = 'ACTIVE';

  END IF;
END$$;
