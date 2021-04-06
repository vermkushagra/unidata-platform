alter table etalons drop column if exists lock_id;
alter table origins drop column if exists lock_id;
alter table etalons_relations drop column if exists lock_id;
alter table origins_relations drop column if exists lock_id;
alter table etalons_classifiers drop column if exists lock_id;
alter table origins_classifiers drop column if exists lock_id;

-- Full default sequence.
-- drop sequence if exists global_lock_id_seq cascade;
do $$
declare
    needs_update boolean := false;
begin
	    needs_update := coalesce((SELECT false FROM information_schema.sequences WHERE sequence_name = 'global_lock_id_seq'), true);
	if (needs_update = true) then
        raise notice 'Create global sequence number';
		create sequence global_lock_id_seq increment by 1 minvalue -9223372036854775808 start with-9223372036854775808;
    else 
        raise notice 'Global sequence number have already creader';
    end if;
end$$ language plpgsql;
-- Records etalons locks
alter table etalons add column lock_id bigint not null default nextval('global_lock_id_seq');

-- Records origins locks
alter table origins add column lock_id bigint not null default nextval('global_lock_id_seq');

-- Relations etalons locks
alter table etalons_relations add column lock_id bigint not null default nextval('global_lock_id_seq');

-- Relations origins locks
alter table origins_relations add column lock_id bigint not null default nextval('global_lock_id_seq');

-- Classifiers etalons locks
alter table etalons_classifiers add column lock_id bigint not null default nextval('global_lock_id_seq');

-- Classifiers origins locks
alter table origins_classifiers add column lock_id bigint not null default nextval('global_lock_id_seq');

-- Drop old etalons_locks
drop table if exists etalons_locks;

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
    _shift character varying(256)) 
returns setof void as $$
begin
    perform pg_advisory_xact_lock(lock_id) from origins where id = _origin_id;
    insert into origins_vistory (id, origin_id, operation_id, revision, valid_from, valid_to, data, created_by, create_date, status, approval, shift)
    values (_id, _origin_id, _operation_id,
        coalesce((select max(prev.revision) + 1 from origins_vistory prev where prev.origin_id = _origin_id), 1), 
        _valid_from, _valid_to, _data, _created_by, coalesce(_create_date, now()), _status, _approval, _shift);
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
    _approval character varying(256)) 
returns setof void  as $$
begin
    perform pg_advisory_xact_lock(lock_id) from origins_relations where id = _origin_id;
    insert into origins_relations_vistory (id, origin_id, operation_d, revision, valid_from, valid_to, data, created_by, status, approval)
    values (_id, _origin_id, _operation_id,
        coalesce((select max(prev.revision) + 1 from origins_relations_vistory prev where prev.origin_id = _origin_id), 1), 
        _valid_from, _valid_to, _data, _created_by, _status, _approval);
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
    _approval character varying(256)) 
returns setof void as $$
begin
    perform pg_advisory_xact_lock(lock_id) from origins_classifiers where id = _origin_id;
    insert into origins_classifiers_vistory (id, origin_id, operation_id, revision, valid_from, valid_to, data, created_by, status, approval)
    values (_id, _origin_id, _operation_id,
        coalesce((select max(prev.revision) + 1 from origins_classifiers_vistory prev where prev.origin_id = _origin_id), 1), 
        _valid_from, _valid_to, _data, _created_by, _status, _approval);
end
$$ language plpgsql;
