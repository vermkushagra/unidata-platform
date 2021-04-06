-- Old stuff
-- Binary data
alter table binary_data drop constraint if exists fk_etalons_binary_data;
alter table binary_data drop constraint if exists fk_origins_binary_data;
alter table binary_data add constraint fk_etalons_binary_data foreign key (etalon_id) references etalons (id) match full on update cascade on delete cascade;
alter table binary_data add constraint fk_origins_binary_data foreign key (origin_id) references origins (id) match full on update cascade on delete cascade;
create index ix_binary_data_etalon_id on binary_data using btree (etalon_id collate pg_catalog."default");
create index ix_binary_data_origin_id on binary_data using btree (origin_id collate pg_catalog."default");
create index ix_binary_data_event_id on binary_data using btree (event_id collate pg_catalog."default");

-- Character data
alter table character_data drop constraint if exists fk_etalons_character_data;
alter table character_data drop constraint if exists fk_origins_character_data;
alter table character_data add constraint fk_etalons_character_data foreign key (etalon_id) references etalons (id) match full on update cascade on delete cascade;
alter table character_data add constraint fk_origins_character_data foreign key (origin_id) references origins (id) match full on update cascade on delete cascade;
create index ix_character_data_etalon_id on character_data using btree (etalon_id collate pg_catalog."default");
create index ix_character_data_origin_id on character_data using btree (origin_id collate pg_catalog."default");
create index ix_character_data_event_id on character_data using btree (event_id collate pg_catalog."default");

-- Lock table
drop table if exists etalons_locks;
drop sequence if exists etalons_locks_id_seq;
create sequence etalons_locks_id_seq;
create table etalons_locks (
    id bigint not null default nextval('etalons_locks_id_seq'),
    etalon_id character(36) not null,
    constraint etalons_locks_pkey primary key (id),
    constraint fk_etalons_locks_etalon_id foreign key (etalon_id) references etalons (id) match full 
        on update cascade
        on delete cascade
);

alter sequence etalons_locks_id_seq owned by etalons_locks.id;
create index ix_etalons_locks_etalon_id on etalons_locks using btree (etalon_id collate pg_catalog."default");

-- Etalons merge history
drop table if exists etalons_transitions cascade;
create table etalons_transitions (
    id character(36) not null,
    etalon_id character(36) not null,
    operation_id character varying(512),
    type character varying(255) not null,
    revision integer not null,
    create_date timestamp with time zone not null default now(),
    created_by character varying(512) not null,
    constraint etalons_transitions_pkey primary key (id),
    constraint fk_etalons_transitions_etalon_id foreign key (etalon_id) references etalons (id) match full 
        on update cascade
        on delete cascade
);

create index ix_etalons_transitions_etalon_id on etalons_transitions using btree (etalon_id collate pg_catalog."default");

-- Duplicates
drop table if exists duplicates;
create table duplicates
(
    etalon_transition_id character(36) not null,
    duplicate_id character(36) not null,
    is_auto boolean,
    constraint duplicates_pkey primary key (etalon_transition_id, duplicate_id),
    constraint fk_duplicates_etalon_transition_id foreign key (etalon_transition_id) references etalons_transitions (id) match full 
        on update cascade 
        on delete cascade,
    constraint fk_duplicates_duplicate_id foreign key (duplicate_id) references etalons (id) match full 
        on update cascade 
        on delete cascade
);

-- Origins merge history
drop table if exists origins_transitions cascade;
create table origins_transitions (
    etalon_transition_id character(36) not null,
    origin_id character(36) not null,
    constraint origins_transitions_pkey primary key (etalon_transition_id, origin_id),
    constraint fk_origins_transitions_etalon_transition_id foreign key (etalon_transition_id) references etalons_transitions (id) match full 
        on update no action 
        on delete cascade,
    constraint fk_origins_transitions_origin_id foreign key (origin_id) references origins (id) match full 
        on update no action
        on delete cascade
);
