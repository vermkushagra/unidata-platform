create sequence meta_draft_id_seq;

create sequence meta_model_revision_seq;

create sequence meta_process_assignment_id_seq;

create sequence meta_storage_revision_seq;

create sequence meta_ui_revision_seq;

create table if not exists meta_storage
(
    id varchar(255) not null
        constraint pk_meta_storage_pkey
            primary key,
    name text not null,
    create_date timestamp default now() not null,
    update_date timestamp default now(),
    created_by text not null,
    updated_by text
);

create table if not exists meta_model
(
    id varchar(255) not null,
    storage_fkey varchar(255) not null
        constraint fk_storage
            references meta_storage,
    type varchar(128) not null,
    version integer not null,
    data text not null,
    create_date timestamp default now() not null,
    update_date timestamp,
    created_by text not null,
    updated_by text,
    constraint meta_model_pkey
        primary key (id, storage_fkey)
);

create table if not exists meta_process_assignment
(
    id bigserial not null
        constraint pk_meta_process_assignment
            primary key,
    name varchar(128) not null,
    type varchar(128) not null,
    process_name varchar(265),
    create_date timestamp with time zone default now(),
    update_date timestamp with time zone,
    created_by varchar(256) not null,
    updated_by varchar(256),
    trigger_type varchar(128),
    constraint uq_meta_process_assignment
        unique (name, type)
);

create table if not exists meta_draft
(
    id bigserial not null
        constraint idx_meta_draft_unique
            unique,
    type varchar(2044) not null,
    value bytea not null,
    name varchar(100),
    created_at timestamp with time zone not null,
    created_by varchar(100) not null,
    updated_at timestamp with time zone,
    updated_by varchar(100),
    version integer,
    active boolean default false not null
);

CREATE TABLE IF NOT EXISTS measurement_values (
    id         varchar(63) not null,
    name       varchar(63) not null,
    short_name varchar(31) not null,
    constraint pk_measurement_values_id primary key (id)
);


create table if not exists measurement_units (
    id         varchar(63)  not null,
    name       varchar(63)  not null,
    short_name varchar(31)  not null,
    function   varchar(255) not null,
    value_id   varchar(63)  not null,
    base       boolean      not null,
    unit_order integer not null default 0,
    constraint pk_measurement_units_id primary key (value_id, id),
    constraint fk_measurement_values_value_id foreign key (value_id) references measurement_values (id) match full on delete cascade
);

