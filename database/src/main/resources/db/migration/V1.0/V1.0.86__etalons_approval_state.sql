-- etalons
alter table etalons drop column if exists approval;
alter table etalons add column approval character varying(256) not null default 'APPROVED';
drop index if exists ix_etalons_approval;
create index ix_etalons_approval on etalons using btree (approval collate pg_catalog."default");

-- etalons relations
alter table etalons_relations drop column if exists approval;
alter table etalons_relations add column approval character varying(256) not null default 'APPROVED';
drop index if exists ix_etalons_relations_approval;
create index ix_etalons_relations_approval on etalons_relations using btree (approval collate pg_catalog."default");

delete from s_right_s_resource where s_right_id in (select id from s_right where name in ('ACCEPT', 'ACCEPT2'));
delete from s_right cascade where name in ('ACCEPT', 'ACCEPT2');