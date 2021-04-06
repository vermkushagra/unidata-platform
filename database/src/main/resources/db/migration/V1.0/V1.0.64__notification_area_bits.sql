-- 1. User events
drop table if exists user_event cascade;
create table user_event (
    id character(36) not null,
    user_id integer not null,
    type character varying(512) not null,
    content text,
    create_date timestamp with time zone not null default now(),
    update_date timestamp with time zone,
    created_by character varying(512) not null,
    updated_by character varying(512)
);

alter table user_event add constraint pk_user_event primary key (id);
alter table user_event add constraint fk_s_user_user_event foreign key (user_id) references s_user (id) match full
        on update cascade
        on delete cascade;

-- 2. Add links
alter table binary_data drop constraint if exists uq_binary_data;
alter table binary_data drop constraint if exists fk_user_event_binary_data;
alter table binary_data drop column if exists event_id;
alter table binary_data add column event_id character(36);
alter table binary_data add constraint fk_user_event_binary_data foreign key (event_id) references user_event (id) match full
    on update cascade
    on delete cascade;

alter table character_data drop constraint if exists uq_character_data;
alter table character_data drop constraint if exists fk_user_event_character_data;
alter table character_data drop column if exists event_id;
alter table character_data add column event_id character(36);
alter table character_data add constraint fk_user_event_character_data foreign key (event_id) references user_event (id) match full
    on update cascade
    on delete cascade;

