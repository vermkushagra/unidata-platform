drop index if exists ix_origins_etalon_id;
create index ix_origins_etalon_id on origins (etalon_id);

drop index if exists ix_origins_vistory_origin_id;
create index ix_origins_vistory_origin_id on origins_vistory (origin_id);

drop index if exists ix_origins_relations_etalon_id;
create index ix_origins_relations_etalon_id on origins_relations (etalon_id);

drop index if exists ix_origins_relations_vistory_origin_id;
create index ix_origins_relations_vistory_origin_id on origins_relations_vistory (origin_id);
