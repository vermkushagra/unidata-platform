create table if not exists job
(
    id bigserial not null
        constraint job_id_key
            unique,
    name varchar(100) not null
        constraint job_name_key
            unique,
    cron_expr text,
    job_name_ref text not null,
    descr text,
    create_date timestamp with time zone default now() not null,
    update_date timestamp with time zone,
    created_by varchar(256) not null,
    updated_by varchar(256),
    enabled boolean not null,
    error boolean default false not null,
    tags text[]
);

create table if not exists job_parameter
(
    id bigserial not null
        constraint job_parameter_id_key
            unique,
    job_id bigint not null
        constraint fk_job_id
            references job (id),
    name text not null,
    create_date timestamp with time zone default now() not null,
    update_date timestamp with time zone,
    created_by varchar(256) not null,
    updated_by varchar(256),
    val_string text,
    val_date timestamp with time zone,
    val_long bigint,
    val_double double precision,
    val_boolean boolean,
    val_arr_string text[],
    val_arr_date timestamp with time zone[],
    val_arr_long bigint[],
    val_arr_double double precision[],
    val_arr_boolean boolean[]
);

create unique index if not exists job_parameter_job_id_name_uindex
    on job_parameter (job_id, name);

create table if not exists job_batch_job_instance
(
    job_id bigint not null
        constraint fk_job_id
            references job (id),
    job_instance_id bigint not null,--references unidata_batch_job.batch_job_instance
    create_date timestamp with time zone default now() not null,
    update_date timestamp with time zone,
    created_by varchar(256) not null,
    updated_by varchar(256)
);

create table if not exists job_trigger
(
    id bigserial not null
        constraint job_trigger_id_key
            unique,
    finish_job_id bigint not null
        constraint finish_job_id_fk
            references job (id),
    start_job_id bigint not null
        constraint start_job_id_fk
            references job (id),
    success_rule boolean default true not null,
    name varchar(100) not null
        constraint name_trigger_unique
            unique,
    description varchar(255),
    create_date timestamp with time zone default now() not null,
    update_date timestamp with time zone,
    created_by varchar(256) not null,
    updated_by varchar(256)
);
