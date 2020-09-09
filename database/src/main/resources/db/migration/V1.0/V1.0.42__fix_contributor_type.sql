-- contributor
drop type if exists contributor cascade; 
create type contributor as (
    origin_id character(36),
    revision integer,
    source_system character varying(256),
    status character varying(256),
    approval character varying(256),
    owner character varying(256),
    last_update character varying(256)
);
