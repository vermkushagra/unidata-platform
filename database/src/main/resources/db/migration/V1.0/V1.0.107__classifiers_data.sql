alter table origins_relations_vistory drop constraint if exists fk_origins_relations_origin_id;
alter table origins_relations_vistory drop constraint if exists fk_origins_relations_vistory_origin_id;
alter table origins_relations_vistory add constraint fk_origins_relations_vistory_origin_id foreign key (origin_id) references origins_relations (id) match full on update no action on delete cascade;

-- Drop
drop table if exists origins_classifiers_vistory;
drop table if exists origins_classifiers;
drop table if exists etalons_classifiers;

-- Etalons
create table etalons_classifiers (
  id character(36) not null,
  name character varying(2044) not null, -- classifer name
  etalon_id_record character(36) not null,
  version integer not null,
  create_date timestamp with time zone not null default now(),
  update_date timestamp with time zone,
  created_by character varying(256) not null,
  updated_by character varying(256),
  status character varying(256) not null default 'ACTIVE'::character varying,
  approval character varying(256) NOT NULL DEFAULT 'APPROVED'::character varying,
  constraint pk_etalons_classifiers_pkey primary key (id),
  constraint fk_etalons_classifiers_name foreign key (name)
      references clsf (name) match full
      on update no action on delete cascade,
  constraint fk_etalons_classifiers_etalon_id_record foreign key (etalon_id_record)
      references etalons (id) match full
      on update no action on delete cascade,
  constraint uq_etalons_classifiers_name_etalon_id_record unique (name, etalon_id_record)
);

-- Index: ix_etalons_classifiers_status
drop index if exists ix_etalons_classifiers_status;
create index ix_etalons_classifiers_status on etalons_classifiers using btree (status collate pg_catalog."default");

-- Index: ix_etalons_relations_approval
drop index if exists ix_etalons_classifiers_approval;
create index ix_etalons_classifiers_approval on etalons_classifiers using btree (approval collate pg_catalog."default");

-- Origins
create table origins_classifiers
(
  id character(36) not null,
  etalon_id character(36) not null,
  name character varying(2044) not null, 
  node_id character varying(2044) not null,
  origin_id_record character(36) not null,
  version integer not null,
  source_system character varying(256) not null,
  create_date timestamp with time zone not null default now(),
  update_date timestamp with time zone,
  created_by character varying(256) not null,
  updated_by character varying(256),
  status character varying(256) NOT NULL DEFAULT 'ACTIVE'::character varying,
  constraint pk_origins_classifiers_pkey primary key (id),
  constraint fk_origins_classifiers_etalon_id foreign key (etalon_id)
      references etalons_classifiers (id) match full
      on update no action on delete cascade,
  constraint fk_origins_classifiers_name foreign key (name)
      references clsf (name) match full
      on update no action on delete cascade,
  constraint fk_origins_classifiers_node_id foreign key (node_id)
      references clsf_node (node_id) match full
      on update no action on delete cascade,
  constraint fk_origins_classifiers_origin_id_record foreign key (origin_id_record)
      references origins (id) match full
      on update no action on delete cascade,
  constraint uq_origins_classifiers_name_node_id_origin_id_record unique (name, node_id, origin_id_record)
);

-- Index: ix_origins_classifiers_etalon_id
drop index if exists ix_origins_classifiers_etalon_id;
create index ix_origins_classifiers_etalon_id on origins_classifiers using btree (etalon_id collate pg_catalog."default");

-- Index: ix_origins_classifiers_status
drop index if exists ix_origins_classifiers_status;
create index ix_origins_classifiers_status on origins_classifiers using btree (status collate pg_catalog."default");

-- Vistory
create table origins_classifiers_vistory (
  id character(36) not null,
  origin_id character(36) not null,
  revision integer not null,
  valid_from timestamp with time zone,
  valid_to timestamp with time zone,
  data text,
  create_date timestamp with time zone not null default now(),
  created_by character varying(256) not null,
  status character varying(256) not null default 'ACTIVE'::character varying,
  approval character varying(256) NOT NULL DEFAULT 'APPROVED'::character varying,
  constraint pk_origins_classifiers_vistory primary key (id),
  constraint fk_origins_classifiers_vistory_origin_id foreign key (origin_id)
      references origins_classifiers (id) match full
      on update no action on delete cascade,
  constraint uq_origins_classifiers_vistory unique (origin_id, revision)
);

-- Index: ix_origins_classifiers_vistory_origin_id
drop index if exists ix_origins_classifiers_vistory_origin_id;
create index ix_origins_classifiers_vistory_origin_id on origins_classifiers_vistory using btree (origin_id collate pg_catalog."default");

-- Index: ix_origins_classifiers_status
drop index if exists ix_origins_classifiers_vistory_status;
create index ix_origins_classifiers_vistory_status on origins_classifiers_vistory using btree (status collate pg_catalog."default");

-- Index: ix_origins_classifiers_vistory_valid_from_valid_to
drop index if exists ix_origins_classifiers_vistory_valid_from_valid_to;
create index ix_origins_classifiers_vistory_valid_from_valid_to on origins_classifiers_vistory using btree (valid_from, valid_to);

-- Index: ix_origins_classifiers_vistory_approval
drop index if exists ix_origins_classifiers_vistory_approval;
create index ix_origins_classifiers_vistory_approval on origins_classifiers_vistory using btree (approval collate pg_catalog."default");

