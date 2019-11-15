-- Records
-- drop type record_origin_key cascade;
create type record_origin_key as (
    id uuid,
    initial_owner uuid,
    status record_status,
    enrichment boolean,
    revision integer,
    source_system varchar(256),
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    external_id varchar(512)
);

-- drop type record_key cascade;
create type record_key as (
    shard integer,
    lsn bigint,
    id uuid,
    name varchar(256),
    status record_status,
    state approval_state,
    approved boolean,
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    origin_keys record_origin_key[]
);

-- Record keys
create or replace function ud_fetch_record_keys(_etalon_id uuid)
returns record_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _shard int4 := ud_get_record_etalon_shard_number(_etalon_id);
    _result record_key;
begin

    select 
        _shard,
        e.lsn,
        _etalon_id,
        e.name,
        e.status, 
        e.approval, 
        coalesce((   
            select 
                true 
            from 
                record_vistory v, record_origins o
            where 
                o.etalon_id = _etalon_id 
            and o.shard = _shard
            and v.origin_id = o.id 
            and v.shard = _shard
            and v.approval = 'APPROVED'::approval_state
            fetch first 1 rows only), false),
        e.create_date,
        e.created_by,
        m.update_date, 
        m.updated_by,
        (   select array_agg((
                o.id,
                o.initial_owner,
                o.status,
                o.enrichment,
                (select max(v.revision) from record_vistory v where v.origin_id = o.id and v.shard = _shard),
                o.source_system,
                o.create_date,
                o.created_by,
                o.update_date,
                o.updated_by,
                o.external_id)::record_origin_key)
            from 
                record_origins o 
            where 
                o.etalon_id = _etalon_id 
            and o.shard = _shard) as origin_keys
    into
        _result.shard,
        _result.lsn,
        _result.id,
        _result.name,
        _result.status,
        _result.state,
        _result.approved,
        _result.create_date,
        _result.created_by,
        _result.update_date,
        _result.updated_by,
        _result.origin_keys
    from 
        record_etalons e, lateral ud_fetch_record_update_mark(e.id, _shard) m
    where
        e.shard = _shard
    and e.id = _etalon_id;
        
    return _result;
end;
$BODY$;

-- Keys by ext. id
create or replace function ud_fetch_record_keys(_external_id varchar(512), _source_system varchar(256), _name varchar(256))
returns record_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _etalon_id uuid;
begin
    -- External key
    select etalon_id into _etalon_id from record_external_keys 
    where 
        ext_key = ud_get_external_keys_compact(_external_id, _name, _source_system)
    and ext_shard = ud_get_external_keys_shard_number(_external_id, _name, _source_system);

    return ud_fetch_record_keys(_etalon_id);
end;
$BODY$;

-- Keys by LSN
create or replace function ud_fetch_record_keys(_shard integer, _lsn bigint)
returns record_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result record_key;
begin

    select 
        _shard,
        _lsn,
        e.id,
        e.name,
        e.status, 
        e.approval, 
        coalesce((   
            select 
                true 
            from 
                record_vistory v, record_origins o
            where 
                o.etalon_id = e.id 
            and o.shard = _shard
            and v.origin_id = o.id 
            and v.shard = _shard
            and v.approval = 'APPROVED'::approval_state
            fetch first 1 rows only), false),
        e.create_date,
        e.created_by,
        m.update_date, 
        m.updated_by,
        (   select 
                array_agg((
                    o.id,
                    o.initial_owner,
                    o.status,
                    o.enrichment,
                    (select max(v.revision) from record_vistory v where v.origin_id = o.id and v.shard = _shard),
                    o.source_system,
                    o.create_date,
                    o.created_by,
                    o.update_date,
                    o.updated_by,
                    o.external_id)::record_origin_key)
            from 
                record_origins o 
            where 
                o.etalon_id = e.id 
            and o.shard = _shard) as origin_keys
    into
        _result.shard,
        _result.lsn,
        _result.id,
        _result.name,
        _result.status,
        _result.state,
        _result.approved,
        _result.create_date,
        _result.created_by,
        _result.update_date,
        _result.updated_by,
        _result.origin_keys
    from 
        record_etalons e, lateral ud_fetch_record_update_mark(e.id, _shard) m
    where
        e.shard = _shard
    and e.lsn = _lsn;

    return _result;
end;
$BODY$;

-- Relations
-- drop type relation_origin_key cascade;
create type relation_origin_key as (
    id uuid,
    initial_owner uuid,
    status record_status,
    enrichment boolean,
    revision integer,
    source_system varchar(256),
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    from_key uuid,
    to_key uuid
);

-- drop type relation_key cascade;
create type relation_key as (
    shard integer,
    lsn bigint,
    id uuid,
    name varchar(256),
    status record_status,
    state approval_state,
    approved boolean,
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    type relation_type,
    from_key record_key,
    to_key record_key,
    origin_keys relation_origin_key[]
);

create or replace function ud_fetch_relation_keys(_etalon_id uuid)
returns relation_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _shard int4 := ud_get_relation_etalon_shard_number(_etalon_id);
    _result relation_key;
begin

    select 
        _shard,
        e.lsn,
        e.id, 
        e.name,
        e.reltype,
        e.status, 
        e.approval,
        case when e.reltype = 'CONTAINS'::relation_type then (t).approved
        else
            coalesce((select true from relation_vistory v, relation_origins o
                where 
                    o.etalon_id = _etalon_id
                and o.shard = _shard
                and v.origin_id = o.id 
                and v.shard = _shard
                and v.approval = 'APPROVED'::approval_state
                fetch first 1 rows only), false)
        end as approved, 
        e.create_date, 
        e.created_by, 
        m.update_date, 
        m.updated_by,
        f,
        t,
        (   select array_agg((
                o.id,
                o.initial_owner,
                o.status,
                o.enrichment,
                (select max(v.revision) from relation_vistory v where v.origin_id = o.id and v.shard = _shard),
                o.source_system,
                o.create_date,
                o.created_by,
                o.update_date,
                o.updated_by,
                o.origin_id_from,
                o.origin_id_to)::relation_origin_key)
            from 
                relation_origins o 
            where 
                o.etalon_id = _etalon_id
            and o.shard = _shard) as origin_keys
    into
        _result.shard,
        _result.lsn,
        _result.id,
        _result.name,
        _result.status,
        _result.state,
        _result.approved,
        _result.create_date,
        _result.created_by,
        _result.update_date,
        _result.updated_by,
        _result.type,
        _result.from_key,
        _result.to_key,
        _result.origin_keys
    from 
        relation_etalons e, 
        lateral ud_fetch_relation_update_mark(e.id, _shard) m,
        lateral ud_fetch_record_keys(e.etalon_id_from) f,
        lateral ud_fetch_record_keys(e.etalon_id_to) t
    where
        e.id = _etalon_id
    and e.shard = _shard;

    return _result;
end;
$BODY$;

create or replace function ud_fetch_relation_keys(_shard integer, _lsn bigint)
returns relation_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result relation_key;
begin

    select 
        _shard,
        _lsn,
        e.id, 
        e.name,
        e.reltype,
        e.status, 
        e.approval,
        case when e.reltype = 'CONTAINS'::relation_type then (t).approved
        else
            coalesce((select true from relation_vistory v, relation_origins o
                where 
                    o.etalon_id = e.id
                and o.shard = _shard
                and v.origin_id = o.id 
                and v.shard = _shard
                and v.approval = 'APPROVED'::approval_state
                fetch first 1 rows only), false)
        end as approved, 
        e.create_date, 
        e.created_by, 
        m.update_date, 
        m.updated_by,
        f,
        t,
        (   select array_agg((
                o.id,
                o.initial_owner,
                o.status,
                o.enrichment,
                (select max(v.revision) from relation_vistory v where v.origin_id = o.id and v.shard = _shard),
                o.source_system,
                o.create_date,
                o.created_by,
                o.update_date,
                o.updated_by,
                o.origin_id_from,
                o.origin_id_to)::relation_origin_key)
            from 
                relation_origins o 
            where 
                o.etalon_id = e.id
            and o.shard = _shard) as origin_keys
    into
        _result.shard,
        _result.lsn,
        _result.id,
        _result.name,
        _result.status,
        _result.state,
        _result.approved,
        _result.create_date,
        _result.created_by,
        _result.update_date,
        _result.updated_by,
        _result.type,
        _result.from_key,
        _result.to_key,
        _result.origin_keys
    from 
        relation_etalons e, 
    lateral ud_fetch_relation_update_mark(e.id, _shard) m, 
    lateral ud_fetch_record_keys(e.etalon_id_from) f, 
    lateral ud_fetch_record_keys(e.etalon_id_to) t
    where
        e.shard = _shard
    and e.lsn = _lsn;

    return _result;
end;
$BODY$;

create or replace function ud_fetch_relation_keys(
    _ext_id_from varchar, 
    _source_system_from varchar, 
    _entity_name_from varchar,
    _ext_id_to varchar, 
    _source_system_to varchar, 
    _entity_name_to varchar,
    _relation_name varchar)
returns relation_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result relation_key;
    _from_id uuid;
    _to_id uuid;
    _relation_id uuid;
    
begin
    
    select
        -- From
        (select etalon_id from record_external_keys
         where 
             ext_key = ud_get_external_keys_compact(_ext_id_from, _entity_name_from, _source_system_from) 
         and ext_shard = ud_get_external_keys_shard_number(_ext_id_from, _entity_name_from, _source_system_from)),
        -- To
        (select etalon_id from record_external_keys 
         where 
             ext_key = ud_get_external_keys_compact(_ext_id_to, _entity_name_to, _source_system_to)
         and ext_shard = ud_get_external_keys_shard_number(_ext_id_to, _entity_name_to, _source_system_to))
    into
        _from_id,
        _to_id;

    select etalon_id into _relation_id from relation_from_keys where from_id = _from_id and to_id = _to_id and name = _relation_name;
    return ud_fetch_relation_keys(_relation_id);
end;
$BODY$;

create or replace function ud_fetch_relation_keys(
    _shard_from integer, 
    _lsn_from bigint, 
    _shard_to integer, 
    _lsn_to bigint, 
    _relation_name varchar)
returns relation_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result relation_key;
    _from_id uuid;
    _to_id uuid;
    _relation_id uuid;
    
begin

    select
        (select id from record_etalons where shard = _shard_from and lsn = _lsn_from), 
        (select id from record_etalons where shard = _shard_to and lsn = _lsn_to)
    into
        _from_id,
        _to_id;

    select etalon_id into _relation_id from relation_from_keys where from_id = _from_id and to_id = _to_id and name = _relation_name;
    return ud_fetch_relation_keys(_relation_id);
end;
$BODY$;

create or replace function ud_fetch_relation_keys(_from_id uuid, _to_id uuid, _relation_name varchar)
returns relation_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result relation_key;
    _relation_id uuid;
begin

    select etalon_id into _relation_id from relation_from_keys where from_id = _from_id and to_id = _to_id and name = _relation_name;
    return ud_fetch_relation_keys(_relation_id);
end;
$BODY$;

-- Classifiers
-- drop type classifier_origin_key cascade;
create type classifier_origin_key as (
    id uuid,
    initial_owner uuid,
    status record_status,
    enrichment boolean,
    revision integer,
    source_system varchar(256),
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    node_id uuid,
    record_key uuid
);

-- drop type record_key cascade;
create type classifier_key as (
    shard integer,
    lsn bigint,
    id uuid,
    name varchar(256),
    status record_status,
    state approval_state,
    approved boolean,
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    record_key record_key,
    origin_keys classifier_origin_key[]
);

create or replace function ud_fetch_classifier_keys(_etalon_id uuid)
returns classifier_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _shard int4 := ud_get_classifier_etalon_shard_number(_etalon_id);
    _result classifier_key;
begin
    
    select 
        _shard,
        e.lsn,
        e.id, 
        e.name,
        e.status, 
        e.approval, 
        coalesce((select true from classifier_vistory v, classifier_origins o
            where 
                o.etalon_id = _etalon_id
            and o.shard = _shard
            and v.origin_id = o.id 
            and v.shard = _shard
            and v.approval = 'APPROVED'::approval_state
            fetch first 1 rows only), false), 
        e.create_date, 
        e.created_by, 
        m.update_date, 
        m.updated_by,
        ud_fetch_record_keys(e.etalon_id_record),
        (   select 
                array_agg((
                    o.id,
                    o.initial_owner,
                    o.status,
                    o.enrichment,
                    (select max(v.revision) from relation_vistory v where v.origin_id = o.id and v.shard = _shard),
                    o.source_system,
                    o.node_id,
                    o.create_date,
                    o.created_by,
                    o.update_date,
                    o.updated_by,
                    o.origin_id_record)::classifier_origin_key)
            from 
                classifier_origins o 
            where 
                o.etalon_id = _etalon_id
            and o.shard = _shard) as origin_keys
    into
        _result.shard,
        _result.lsn,
        _result.id,
        _result.name,
        _result.status,
        _result.state,
        _result.approved,
        _result.create_date,
        _result.created_by,
        _result.update_date,
        _result.updated_by,
        _result.record_key,
        _result.origin_keys
    from 
        classifier_etalons e, lateral ud_fetch_classifier_update_mark(e.id, _shard) m
    where
        e.shard = _shard
    and e.id = _etalon_id;
        
    return _result;
end;
$BODY$;

create or replace function ud_fetch_classifier_keys(_shard integer, _lsn bigint)
returns classifier_key
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result classifier_key;
begin
    
    select 
        _shard,
        _lsn,
        e.id, 
        e.name,
        e.status, 
        e.approval, 
        coalesce((select true from classifier_vistory v, classifier_origins o
            where 
                o.etalon_id = e.id
            and o.shard = _shard
            and v.origin_id = o.id 
            and v.shard = _shard
            and v.approval = 'APPROVED'::approval_state
            fetch first 1 rows only), false), 
        e.create_date, 
        e.created_by, 
        m.update_date, 
        m.updated_by,
        r,
        (   select 
                array_agg((
                    o.id,
                    o.initial_owner,
                    o.status,
                    o.enrichment,
                    (select max(v.revision) from relation_vistory v where v.origin_id = o.id and v.shard = _shard),
                    o.source_system,
                    o.node_id,
                    o.create_date,
                    o.created_by,
                    o.update_date,
                    o.updated_by,
                    o.origin_id_record)::classifier_origin_key)
            from 
                classifier_origins o 
            where 
                o.etalon_id = e.id
            and o.shard = _shard) as origin_keys
    into
        _result.shard,
        _result.lsn,
        _result.id,
        _result.name,
        _result.status,
        _result.state,
        _result.approved,
        _result.create_date,
        _result.created_by,
        _result.update_date,
        _result.updated_by,
        _result.record_key,
        _result.origin_keys
    from 
        classifier_etalons e, 
    lateral ud_fetch_classifier_update_mark(e.id, _shard) m,
    lateral ud_fetch_record_keys(e.etalon_id_record) r
    where
        e.shard = _shard
    and e.lsn = _lsn;

    return _result;
end;
$BODY$;

