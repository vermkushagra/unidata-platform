-- Data upsert shift field (PRISTINE|REVISED)
alter table origins_vistory drop column if exists shift;
alter table origins_vistory add column shift character varying(256) NOT NULL DEFAULT 'PRISTINE';