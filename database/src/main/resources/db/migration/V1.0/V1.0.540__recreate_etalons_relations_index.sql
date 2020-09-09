
drop index if exists ix_uq_etalons_relations_name_etalon_id_from_etalon_id_to;
create unique index ix_uq_etalons_relations_name_etalon_id_from_etalon_id_to on etalons_relations using btree
  (etalon_id_from, etalon_id_to, name collate pg_catalog."default");