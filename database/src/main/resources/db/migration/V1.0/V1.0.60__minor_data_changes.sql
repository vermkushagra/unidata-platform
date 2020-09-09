set session schema 'public';

alter table etalons drop column if exists data;
alter table origins add column is_enrichment boolean default false;