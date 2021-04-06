-- 1. Add columns
alter table origins_vistory add column major integer not null default 0;
alter table origins_vistory add column minor integer not null default 0;

alter table origins_relations_vistory add column major integer not null default 0;
alter table origins_relations_vistory add column minor integer not null default 0;

alter table origins_classifiers_vistory add column major integer not null default 0;
alter table origins_classifiers_vistory add column minor integer not null default 0;

-- 2. Modify functions
drop function if exists upsert_record_vistory(
    _id character(36), 
    _origin_id character(36),
    _operation_id character(36),
    _valid_from timestamp with time zone, 
    _valid_to timestamp with time zone, 
    _data text, 
    _created_by character varying(256), 
    _create_date timestamp with time zone, 
    _status character varying(256), 
    _approval character varying(256), 
    _shift character varying(256));
    
create or replace function upsert_record_vistory (
    _id character(36), 
    _origin_id character(36),
    _operation_id character(36),
    _valid_from timestamp with time zone, 
    _valid_to timestamp with time zone, 
    _data text, 
    _created_by character varying(256), 
    _create_date timestamp with time zone, 
    _status character varying(256), 
    _approval character varying(256), 
    _shift character varying(256),
    _major integer,
    _minor integer) 
returns setof void as $$
begin
    perform pg_advisory_xact_lock(gsn) from origins where id = _origin_id;
    insert into origins_vistory (id, origin_id, operation_id, revision, valid_from, valid_to, data, created_by, create_date, status, approval, shift, major, minor)
    values (_id, _origin_id, _operation_id,
        coalesce((select max(prev.revision) + 1 from origins_vistory prev where prev.origin_id = _origin_id), 1), 
        _valid_from, _valid_to, _data, _created_by, coalesce(_create_date, now()), _status, _approval, _shift, _major, _minor);
end
$$ language plpgsql;

drop function if exists upsert_relation_vistory(
    _id character(36), 
    _origin_id character(36),
    _operation_id character(36),
    _valid_from timestamp with time zone, 
    _valid_to timestamp with time zone, 
    _data text, 
    _created_by character varying(256), 
    _status character varying(256), 
    _approval character varying(256));

create or replace function upsert_relation_vistory (
    _id character(36), 
    _origin_id character(36),
    _operation_id character(36),
    _valid_from timestamp with time zone, 
    _valid_to timestamp with time zone, 
    _data text, 
    _created_by character varying(256), 
    _status character varying(256), 
    _approval character varying(256),
    _major integer,
    _minor integer) 
returns setof void  as $$
begin
    perform pg_advisory_xact_lock(gsn) from origins_relations where id = _origin_id;
    insert into origins_relations_vistory (id, origin_id, operation_id, revision, valid_from, valid_to, data, created_by, status, approval, major, minor)
    values (_id, _origin_id, _operation_id,
        coalesce((select max(prev.revision) + 1 from origins_relations_vistory prev where prev.origin_id = _origin_id), 1), 
        _valid_from, _valid_to, _data, _created_by, _status, _approval, _major, _minor);
end
$$ language plpgsql;

drop function if exists upsert_classifier_vistory(
    _id character(36), 
    _origin_id character(36),
    _operation_id character(36),
    _valid_from timestamp with time zone, 
    _valid_to timestamp with time zone, 
    _data text, 
    _created_by character varying(256), 
    _status character varying(256), 
    _approval character varying(256));

create or replace function upsert_classifier_vistory (
    _id character(36), 
    _origin_id character(36),
    _operation_id character(36),
    _valid_from timestamp with time zone, 
    _valid_to timestamp with time zone, 
    _data text, 
    _created_by character varying(256), 
    _status character varying(256), 
    _approval character varying(256),
    _major integer,
    _minor integer) 
returns setof void as $$
begin
    perform pg_advisory_xact_lock(gsn) from origins_classifiers where id = _origin_id;
    insert into origins_classifiers_vistory (id, origin_id, operation_id, revision, valid_from, valid_to, data, created_by, status, approval, major, minor)
    values (_id, _origin_id, _operation_id,
        coalesce((select max(prev.revision) + 1 from origins_classifiers_vistory prev where prev.origin_id = _origin_id), 1), 
        _valid_from, _valid_to, _data, _created_by, _status, _approval, _major, _minor);
end
$$ language plpgsql;