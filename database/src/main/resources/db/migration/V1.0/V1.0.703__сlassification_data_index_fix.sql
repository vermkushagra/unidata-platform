do $$
begin
  if not exists(select 1 from schema_version where script ilike '%сlassification_data_index_fix%') then

    drop index if exists ix_etalons_classifiers_name_etalon_id_record;
    create index ix_etalons_classifiers_name_etalon_id_record on etalons_classifiers using btree 
    (etalon_id_record, name collate pg_catalog."default");

    drop index if exists uq_origins_classifiers_name_node_id_origin_id_record;
    create unique index uq_origins_classifiers_name_node_id_origin_id_record on origins_classifiers using btree
    (origin_id_record, node_id collate pg_catalog."default", name collate pg_catalog."default");

  end if;
end$$;
