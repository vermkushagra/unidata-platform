alter table s_resource drop column if exists category;
alter table s_resource add column category character varying(128) not null default 'META_MODEL';
update s_resource set category = 'SYSTEM' where r_type = 'SYSTEM';
