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
    _approval character varying(256)) 
returns setof void  as $$
begin
    perform pg_advisory_xact_lock(lock_id) from origins_relations where id = _origin_id;
    insert into origins_relations_vistory (id, origin_id, operation_id, revision, valid_from, valid_to, data, created_by, status, approval)
    values (_id, _origin_id, _operation_id,
        coalesce((select max(prev.revision) + 1 from origins_relations_vistory prev where prev.origin_id = _origin_id), 1), 
        _valid_from, _valid_to, _data, _created_by, _status, _approval);
end
$$ language plpgsql;