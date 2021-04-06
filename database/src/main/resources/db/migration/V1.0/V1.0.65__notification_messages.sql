-- 1. Notification messages

drop table if exists message cascade;
create table message (
    message_id          bigint primary key,
    message             text null,
    delivered           boolean default false not null,
    type_id             smallint not null,
    send_date           timestamp with time zone null,
    create_date         timestamp with time zone default CURRENT_TIMESTAMP,
    last_attempt_date   timestamp with time zone null,
    failed_send         int not null
);

DROP SEQUENCE if exists message_id_seq;
CREATE SEQUENCE message_id_seq
  INCREMENT 1
  START 1000
  CACHE 1;
