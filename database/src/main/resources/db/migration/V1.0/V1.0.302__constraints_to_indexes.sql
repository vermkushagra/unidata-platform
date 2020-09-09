alter table origins drop constraint uq_origins_external_id_source_system;
create unique index ix_uq_origins_external_id_source_system_name on origins using btree (external_id, source_system, name);

alter table origins_vistory drop constraint uq_origins_vistory;
create unique index ix_uq_origins_vistory_origin_id_revision on origins_vistory using btree (origin_id, revision);

alter table etalons_relations drop constraint uq_etalons_relations_name_etalon_id_from_etalon_id_to;
create unique index ix_uq_etalons_relations_name_etalon_id_from_etalon_id_to on etalons_relations using btree (name, etalon_id_from, etalon_id_to);

alter table origins_relations drop constraint uq_origins_relations_name_origin_id_from_origin_id_to;
create unique index ix_uq_origins_relations_name_origin_id_from_origin_id_to on origins_relations using btree (name, origin_id_from, origin_id_to);

alter table origins_relations_vistory drop constraint uq_origins_relations_vistory;
create unique index ix_uq_origins_relations_vistory_origin_id_revision on origins_relations_vistory using btree (origin_id, revision);

alter table origins_classifiers_vistory drop constraint uq_origins_classifiers_vistory;
create unique index ix_uq_origins_classifiers_vistory on origins_classifiers_vistory using btree (origin_id, revision);