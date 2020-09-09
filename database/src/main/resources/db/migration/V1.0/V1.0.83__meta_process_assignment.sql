create table meta_process_assignment (
    id           bigserial unique not null,
    name         character varying(128) not null,
    type         character varying(128) not null,
    process_name character varying(265),
    create_date  timestamp with time zone default now(),
    update_date  timestamp with time zone,
    created_by   character varying(256) NOT NULL,
    updated_by   character varying(256),
    constraint pk_meta_process_assignment primary key (id),
    constraint uq_meta_process_assignment unique (name, type)
);