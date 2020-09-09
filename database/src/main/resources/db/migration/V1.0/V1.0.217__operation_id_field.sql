ALTER TABLE origins_classifiers_vistory drop column if exists operation_id;
ALTER TABLE origins_classifiers_vistory add column operation_id character(36) not null default '-1';

ALTER TABLE origins_relations_vistory drop column if exists operation_id;
ALTER TABLE origins_relations_vistory add column operation_id character(36) not null default '-1';

ALTER TABLE origins_vistory drop column if exists operation_id;
ALTER TABLE origins_vistory add column operation_id character(36) not null default '-1';

ALTER TABLE dq_errors drop column if exists operation_id;
ALTER TABLE dq_errors add column operation_id character(36) not null default '-1';

-- Index: ix_origins_classifiers_vistory_operation_id
drop index if exists ix_origins_classifiers_vistory_operation_id;
create index ix_origins_classifiers_vistory_operation_id on origins_classifiers_vistory using btree (operation_id collate pg_catalog."default");

-- Index: ix_origins_relations_vistory_operation_id
drop index if exists ix_origins_relations_vistory_operation_id;
create index ix_origins_relations_vistory_operation_id on origins_relations_vistory using btree (operation_id collate pg_catalog."default");

-- Index: ix_origins_vistory_operation_id
drop index if exists ix_origins_vistory_operation_id;
create index ix_origins_vistory_operation_id on origins_vistory using btree (operation_id collate pg_catalog."default");

-- Index: ix_dq_errors_operation_id
drop index if exists ix_dq_errors_operation_id;
create index ix_dq_errors_operation_id on dq_errors using btree (operation_id collate pg_catalog."default");
