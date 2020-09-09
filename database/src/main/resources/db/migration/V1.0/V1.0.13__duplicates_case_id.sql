-- Add tweo more columns
delete from duplicates;

alter table duplicates drop column if exists entity_id;
alter table duplicates add column entity_id varchar(512) not null;
alter table duplicates drop column if exists case_id;
alter table duplicates add column case_id bigint not null;

