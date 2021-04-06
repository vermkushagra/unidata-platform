-- Records approval state
alter table origins_vistory drop column if exists approval;
alter table origins_vistory add column approval character varying (256) not null default 'APPROVED';
-- Relations approval state
alter table origins_relations_vistory drop column if exists approval;
alter table origins_relations_vistory add column approval character varying (256) not null default 'APPROVED';