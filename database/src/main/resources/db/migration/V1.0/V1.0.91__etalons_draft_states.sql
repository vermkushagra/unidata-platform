-- Etalons draft states
drop index if exists ix_etalons_draft_states_etalon_id;
drop table if exists etalons_draft_states;
create table if not exists etalons_draft_states (
    id bigserial not null,
    etalon_id character(36) not null,
    revision integer not null,
    status character varying(256) not null,
    create_date timestamp with time zone not null default current_timestamp,
    created_by character varying(512) not null,
    constraint etalons_draft_states_pkey primary key (id),
    constraint fk_etalons_draft_states_etalon_id foreign key (etalon_id)
      references etalons (id) match full
      on update cascade on delete cascade
);

create index ix_etalons_draft_states_etalon_id on etalons_draft_states using btree (etalon_id collate pg_catalog."default");

-- Etalons relations draft states 
drop index if exists ix_etalons_relations_draft_states_etalon_id;
drop table if exists etalons_relations_draft_states;
create table if not exists etalons_relations_draft_states (
    id bigserial not null,
    etalon_id character(36) not null,
    revision integer not null,
    status character varying(256) not null,
    create_date timestamp with time zone not null default current_timestamp,
    created_by character varying(512) not null,
    constraint etalons_relations_draft_states_pkey primary key (id),
    constraint fk_etalons_relations_draft_states_etalon_id foreign key (etalon_id)
      references etalons_relations (id) match full
      on update cascade on delete cascade
);

create index ix_etalons_relations_draft_states_etalon_id on etalons_relations_draft_states using btree (etalon_id collate pg_catalog."default");
