create type approval_state as enum ('PENDING', 'APPROVED', 'DECLINED');

create table if not exists user_event
(
    id uuid not null
        constraint pk_user_event_id
            primary key,
    user_id integer not null
        constraint fk_s_user_user_event
            references s_user
            on update cascade on delete cascade,
    type varchar(512) not null,
    content text,
    create_date timestamp with time zone default now() not null,
    update_date timestamp with time zone,
    created_by varchar(512) not null,
    updated_by varchar(512),
    details text
);

create table if not exists binary_data
(
    id            uuid                                                       not null
        constraint pk_binary_data_id
            primary key,
    classifier_id uuid,
    record_id     uuid,
    data          bytea,
    field         varchar(1024),
    filename      varchar(2048),
    mime_type     varchar(512),
    create_date   timestamp with time zone default now()                     not null,
    update_date   timestamp with time zone,
    created_by    varchar(512)                                               not null,
    updated_by    varchar(512),
    size          bigint,
    status        approval_state           default 'PENDING'::approval_state not null,
    event_id      uuid
        constraint fk_binary_data_event_id
            references user_event
            on update cascade on delete cascade
);


create index if not exists idx_binary_data_etalon_id
    on binary_data (classifier_id);

create index if not exists idx_binary_data_origin_id
    on binary_data (record_id);

create index if not exists idx_binary_data_event_id
    on binary_data (event_id);

comment on column binary_data.classifier_id is 'it was link to origins_classifiers';
comment on column binary_data.record_id is 'it was link to origins';

create table if not exists character_data
(
    id            uuid                                                       not null
        constraint pk_character_data_id
            primary key,
    classifier_id uuid,
    record_id     uuid,
    field         varchar(1024),
    data          text,
    filename      varchar(2048),
    mime_type     varchar(512),
    create_date   timestamp with time zone default now()                     not null,
    update_date   timestamp with time zone,
    created_by    varchar(512)                                               not null,
    updated_by    varchar(512),
    size          bigint,
    status        approval_state           default 'PENDING'::approval_state not null,
    event_id      uuid
        constraint fk_character_data_event_id
            references user_event
            on update cascade on delete cascade
);

comment on column character_data.classifier_id is 'it was link to origins_classifiers';
comment on column character_data.record_id is 'it was link to origins';

create index if not exists idx_character_data_etalon_id
    on character_data (classifier_id);

create index if not exists idx_character_data_event_id
    on character_data (event_id);

create index if not exists idx_character_data_origin_id
    on character_data (record_id);
