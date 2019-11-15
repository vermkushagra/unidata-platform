create table if not exists configuration
(
    name varchar(255) not null
        constraint configuration_pkey
            primary key,
    value bytea
);

create unique index if not exists uq_configuration_name
    on configuration (name);
