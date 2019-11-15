create type record_vistory_data as (
    id uuid,
    origin_id uuid,
    shard int4,
    revision int4,
    valid_from timestamptz,
    valid_to timestamptz,
    create_date timestamptz,
    created_by varchar,
    status record_status,
    approval approval_state,
    shift data_shift,
    operation_type operation_type,
    operation_id text,
    data_a text,
    data_b bytea,
    major int4,
    minor int4
);

create type record_timeline as (
    keys record_key,
    vistory_data record_vistory_data[]
);

create or replace function ud_fetch_record_timeline(
    _etalon_id uuid,
    _fetch_keys boolean,
    _fetch_data boolean,
    _user_name character varying, 
    _is_approver boolean)
returns record_timeline
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _shard int4 := ud_get_record_etalon_shard_number(_etalon_id);
    _result record_timeline;
begin
    
    --------------------- Recursive timeline
    with recursive t (id, origin_id, valid_from, valid_to, revision, status, approval, last_update) as (
  
        select v.id, v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.create_date
        from record_vistory v,
            ( select  i.origin_id, max(i.revision) as revision
              from record_origins o, record_vistory i
              where 
                  o.shard = _shard
              and o.etalon_id = _etalon_id
              and i.shard = _shard
              and i.origin_id = o.id
              and i.status <> 'MERGED'
              and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
              group by i.origin_id ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        and v.shard = _shard
        --------------------- Recursive sub select without duplicates
        union
        select v.id, v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.create_date
        from record_vistory v, t tt
        where 
            v.origin_id = tt.origin_id
        and v.shard = _shard
        and v.revision = 
            ( select max(i.revision) as revision from record_vistory i
              where
                  i.origin_id = tt.origin_id
              and i.shard = _shard
              and (coalesce(i.valid_from, '-infinity') < coalesce(tt.valid_from, '-infinity')
                  or coalesce(i.valid_to, 'infinity') > coalesce(tt.valid_to, 'infinity'))
              and i.revision < tt.revision
              and i.status <> 'MERGED'
              and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))) )
    )
    select
        case when _fetch_keys then ud_fetch_record_keys(_etalon_id) else null end,
        (   select array_agg((
                v.id,
                v.origin_id,
                v.shard,
                v.revision,
                v.valid_from,
                v.valid_to,
                v.create_date,
                v.created_by,
                v.status,
                v.approval,
                v.shift,
                v.operation_type,
                v.operation_id,
                case when _fetch_data then v.data_a else null end,
                case when _fetch_data then v.data_b else null end,
                v.major,
                v.minor
            )::record_vistory_data)
            from t, record_vistory v
            where 
                v.id = t.id
            and v.shard = _shard
            and not exists 
                (  select true from t tt
                   where t.origin_id = tt.origin_id
                   and t.revision < tt.revision
                   and (  
                       coalesce(t.valid_from, '-infinity') between coalesce(tt.valid_from, '-infinity') and coalesce(tt.valid_to, 'infinity') 
                       and coalesce(t.valid_to, 'infinity') between coalesce(tt.valid_from, '-infinity') and coalesce(tt.valid_to, 'infinity')
                   ) 
                )
            )
    into
        _result.keys,
        _result.vistory_data
    from record_etalons e
    where 
        e.shard = _shard
    and e.id = _etalon_id;

    return _result;
end;
$BODY$;

create or replace function ud_fetch_record_timeline(
    _external_id varchar(512), 
    _source_system varchar(256), 
    _name varchar(256),
    _fetch_keys boolean,
    _fetch_data boolean,
    _user_name character varying, 
    _is_approver boolean)
returns record_timeline
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

    return ud_fetch_record_timeline(_etalon_id, _fetch_keys, _fetch_data, _user_name, _is_approver);
end;
$BODY$;

create or replace function ud_fetch_record_timeline(
    _shard int4, 
    _lsn int8, 
    _fetch_keys boolean,
    _fetch_data boolean,
    _user_name character varying, 
    _is_approver boolean)
returns record_timeline
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result record_timeline;
begin
    --------------------- Recursive timeline
    with recursive t (id, origin_id, valid_from, valid_to, revision, status, approval, last_update) as (
  
        select v.id, v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.create_date
        from record_vistory v,
            ( select  i.origin_id, max(i.revision) as revision
              from record_etalons e, record_origins o, record_vistory i
              where 
                  e.shard = _shard
              and e.lsn = _lsn
              and o.shard = _shard
              and o.etalon_id = e.id
              and i.shard = _shard
              and i.origin_id = o.id
              and i.status <> 'MERGED'
              and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
              group by i.origin_id ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        and v.shard = _shard
        --------------------- Recursive sub select without duplicates
        union
        select v.id, v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.create_date
        from record_vistory v, t tt
        where 
            v.origin_id = tt.origin_id
        and v.shard = _shard
        and v.revision = 
            ( select max(i.revision) as revision from record_vistory i
              where
                  i.origin_id = tt.origin_id
              and i.shard = _shard
              and (coalesce(i.valid_from, '-infinity') < coalesce(tt.valid_from, '-infinity')
                  or coalesce(i.valid_to, 'infinity') > coalesce(tt.valid_to, 'infinity'))
              and i.revision < tt.revision
              and i.status <> 'MERGED'
              and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))) )
    )
    select
        case when _fetch_keys then ud_fetch_record_keys(e.id) else null end,
        (   select array_agg((
                v.id,
                v.origin_id,
                v.shard,
                v.revision,
                v.valid_from,
                v.valid_to,
                v.create_date,
                v.created_by,
                v.status,
                v.approval,
                v.shift,
                v.operation_type,
                v.operation_id,
                case when _fetch_data then v.data_a else null end,
                case when _fetch_data then v.data_b else null end,
                v.major,
                v.minor
            )::record_vistory_data)
            from t, record_vistory v
            where 
                v.id = t.id
            and v.shard = _shard
            and not exists 
                (  select true from t tt
                   where t.origin_id = tt.origin_id
                   and t.revision < tt.revision
                   and (  
                       coalesce(t.valid_from, '-infinity') between coalesce(tt.valid_from, '-infinity') and coalesce(tt.valid_to, 'infinity') 
                       and coalesce(t.valid_to, 'infinity') between coalesce(tt.valid_from, '-infinity') and coalesce(tt.valid_to, 'infinity')
                   ) 
                )
            )
    into
        _result.keys,
        _result.vistory_data
    from record_etalons e
    where 
        e.shard = _shard
    and e.lsn = _lsn;

    return _result;
end;
$BODY$;

create or replace function ud_fetch_record_interval(
    _etalon_id uuid,
    _fetch_keys boolean,
    _point timestamptz,
    _lud timestamptz,
    _operation_id character varying,
    _user_name character varying, 
    _is_approver boolean)
returns record_timeline
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _shard int4 := ud_get_record_etalon_shard_number(_etalon_id);
    _result record_timeline;
begin
    with 
    rk (keys) as (
        select case when _fetch_keys = true then ud_fetch_record_keys(_etalon_id) else null end
    ),
    oi (lud) as (
        select case when _operation_id is not null then (
            select max(v.create_date) as lud 
            from 
                record_vistory v, record_origins o
            where 
                o.etalon_id = _etalon_id
            and o.shard = _shard
            and v.origin_id = o.id
            and v.shard = _shard
            and v.operation_id = _operation_id
        ) else null end
    ),
    rv (records) as (
        select
            array_agg((
            v.id,
            v.origin_id,
            v.shard,
            v.revision,
            v.valid_from,
            v.valid_to,
            v.create_date,
            v.created_by,
            v.status,
            v.approval,
            v.shift,
            v.operation_type,
            v.operation_id,
            v.data_a,
            v.data_b,
            v.major,
            v.minor)::record_vistory_data)
        from
            record_vistory v, (
                select
                    t.origin_id,
                    max(t.revision) as revision
                from
                    record_origins o, record_vistory t, oi
                where
                    o.etalon_id = _etalon_id
                and o.shard = _shard
                and t.origin_id = o.id
                and t.shard = _shard
                and t.create_date <= (select case when _operation_id is null then coalesce(_lud, 'infinity') else oi.lud end)
                and (coalesce(_point, now()) between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity'))
                and (t.approval <> 'DECLINED'::approval_state and (t.approval <> 'PENDING'::approval_state or (_is_approver = true or t.created_by = _user_name)))
                group by t.origin_id
            ) as s
        where
            v.origin_id = s.origin_id
        and v.revision = s.revision
        and v.shard = _shard
    )
    select keys, records into _result.keys, _result.vistory_data from rk, rv;
    return _result;
end;
$BODY$;

create or replace function ud_fetch_record_interval(
    _shard int4, 
    _lsn int8,
    _fetch_keys boolean,
    _point timestamptz,
    _lud timestamptz,
    _operation_id character varying,
    _user_name character varying, 
    _is_approver boolean)
returns record_timeline
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result record_timeline;
begin
    with 
    rk (keys) as (
        select case when _fetch_keys = true then ud_fetch_record_keys(_shard, _lsn) else null end
    ),
    oi (lud) as (
        select case when _operation_id is not null then (
            select max(v.create_date) as lud 
            from 
                record_vistory v, record_origins o, record_etalons e
            where 
                e.lsn = _lsn
            and e.shard = _shard
            and o.etalon_id = e.id
            and o.shard = _shard
            and v.origin_id = o.id
            and v.shard = _shard
            and v.operation_id = _operation_id
        ) else null end
    ),
    rv (records) as (
        select
            array_agg((
            v.id,
            v.origin_id,
            v.shard,
            v.revision,
            v.valid_from,
            v.valid_to,
            v.create_date,
            v.created_by,
            v.status,
            v.approval,
            v.shift,
            v.operation_type,
            v.operation_id,
            v.data_a,
            v.data_b,
            v.major,
            v.minor)::record_vistory_data)
        from
            record_vistory v, (
                select
                    t.origin_id,
                    max(t.revision) as revision
                from
                    record_etalons e, record_origins o, record_vistory t, oi
                where
                    e.shard = _shard
                and e.lsn = _lsn
                and o.etalon_id = e.id
                and o.shard = _shard
                and t.origin_id = o.id
                and t.shard = _shard
                and t.create_date <= (select case when _operation_id is null then coalesce(_lud, 'infinity') else oi.lud end)
                and (coalesce(_point, now()) between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity'))
                and (t.approval <> 'DECLINED'::approval_state and (t.approval <> 'PENDING'::approval_state or (_is_approver = true or t.created_by = _user_name)))
                group by t.origin_id
            ) as s
        where
            v.origin_id = s.origin_id
        and v.revision = s.revision
        and v.shard = _shard
    )
    select keys, records into _result.keys, _result.vistory_data from rk, rv;

    return _result;
end;
$BODY$;

---------------------------------------------- Relations ---------------------------------------------

create type relation_vistory_data as (
    id uuid,
    origin_id uuid,
    shard int4,
    revision int4,
    valid_from timestamptz,
    valid_to timestamptz,
    create_date timestamptz,
    created_by varchar,
    status record_status,
    approval approval_state,
    shift data_shift,
    operation_type operation_type,
    operation_id text,
    data_a text,
    data_b bytea,
    major int4,
    minor int4);

create type relation_timeline as (
    keys relation_key,
    vistory_data relation_vistory_data[]
);

-- FN
create or replace function ud_fetch_relation_timeline(
    _etalon_id uuid,
    _fetch_keys boolean,
    _fetch_data boolean,
    _user_name character varying, 
    _is_approver boolean)
returns relation_timeline
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _shard int4 := ud_get_relation_etalon_shard_number(_etalon_id);
    _result relation_timeline;
begin

    --------------------- Recursive timeline
    with recursive t (id, origin_id, valid_from, valid_to, revision, status, approval, last_update) as (

        select v.id, v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.create_date
        from relation_vistory v,
            ( select  i.origin_id, max(i.revision) as revision
              from relation_etalons e, relation_origins o, relation_vistory i
              where 
                  e.id = _etalon_id
              and e.shard = _shard
              and e.reltype != 'CONTAINS'::relation_type -- Cut off containment relations, since the cte is not used for them
              and o.shard = _shard
              and o.etalon_id = e.id
              and i.shard = _shard
              and i.origin_id = o.id
              and i.status <> 'MERGED'
              and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
              group by i.origin_id ) as s
        where 
            v.origin_id = s.origin_id
        and v.revision = s.revision
        and v.shard = _shard
        --------------------- Recursive sub select without duplicates
        union
        select v.id, v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.create_date
        from relation_vistory v, t tt
        where 
            v.origin_id = tt.origin_id
        and v.shard = _shard
        and v.revision = ( 
            select max(i.revision) as revision from relation_vistory i
            where
                i.origin_id = tt.origin_id
            and i.shard = _shard
            and (coalesce(i.valid_from, '-infinity') < coalesce(tt.valid_from, '-infinity')
              or coalesce(i.valid_to, 'infinity') > coalesce(tt.valid_to, 'infinity'))
            and i.revision < tt.revision
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
        )
    )
    select
        case when _fetch_keys then ud_fetch_relation_keys(_etalon_id) else null end,
        case 
        when e.reltype = 'CONTAINS'::relation_type then (
            select array_agg((
                v.id,
                v.origin_id,
                v.shard,
                v.revision,
                v.valid_from,
                v.valid_to,
                v.create_date,
                v.created_by,
                v.status,
                v.approval,
                v.shift,
                v.operation_type,
                v.operation_id,
                case when _fetch_data then v.data_a else null end,
                case when _fetch_data then v.data_b else null end,
                v.major,
                v.minor)::relation_vistory_data)
            from unnest((ud_fetch_record_timeline(e.etalon_id_to, false, _fetch_data, _user_name, _is_approver)).vistory_data) v
        )
        else (   
            select array_agg((
                v.id,
                v.origin_id,
                v.shard,
                v.revision,
                v.valid_from,
                v.valid_to,
                v.create_date,
                v.created_by,
                v.status,
                v.approval,
                v.shift,
                v.operation_type,
                v.operation_id,
                case when _fetch_data then v.data_a else null end,
                case when _fetch_data then v.data_b else null end,
                v.major,
                v.minor
            )::relation_vistory_data)
            from t, relation_vistory v
            where 
                v.id = t.id
            and v.shard = _shard
            and not exists (  
                select true from t tt
                where t.origin_id = tt.origin_id
                and t.revision < tt.revision
                and (  
                    coalesce(t.valid_from, '-infinity') between coalesce(tt.valid_from, '-infinity') and coalesce(tt.valid_to, 'infinity')
                    and coalesce(t.valid_to, 'infinity') between coalesce(tt.valid_from, '-infinity') and coalesce(tt.valid_to, 'infinity')
                ) 
            )
        )
        end as vistory_data
    into
        _result.keys,
        _result.vistory_data
    from relation_etalons e
    where 
        e.shard = _shard
    and e.id = _etalon_id;

    return _result;
end;
$BODY$;

create or replace function ud_fetch_relation_timeline(
    _shard integer,
    _lsn bigint,
    _fetch_keys boolean,
    _fetch_data boolean,
    _user_name character varying, 
    _is_approver boolean)
returns relation_timeline
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _shard int4 := ud_get_relation_etalon_shard_number(_etalon_id);
    _result relation_timeline;
begin

    --------------------- Recursive timeline
    with recursive t (id, origin_id, valid_from, valid_to, revision, status, approval, last_update) as (

        select v.id, v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.create_date
        from relation_vistory v,
            ( select  i.origin_id, max(i.revision) as revision
              from relation_etalons e, relation_origins o, relation_vistory i
              where 
                  e.shard = _shard
              and e.lsn = _lsn
              and e.reltype != 'CONTAINS'::relation_type -- Cut off containment relations, since the cte is not used for them
              and o.shard = _shard
              and o.etalon_id = e.id
              and i.shard = _shard
              and i.origin_id = o.id
              and i.status <> 'MERGED'
              and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
              group by i.origin_id ) as s
        where 
            v.origin_id = s.origin_id
        and v.revision = s.revision
        and v.shard = _shard
        --------------------- Recursive sub select without duplicates
        union
        select v.id, v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.create_date
        from relation_vistory v, t tt
        where 
            v.origin_id = tt.origin_id
        and v.shard = _shard
        and v.revision = ( 
            select max(i.revision) as revision from relation_vistory i
            where
                i.origin_id = tt.origin_id
            and i.shard = _shard
            and (coalesce(i.valid_from, '-infinity') < coalesce(tt.valid_from, '-infinity')
              or coalesce(i.valid_to, 'infinity') > coalesce(tt.valid_to, 'infinity'))
            and i.revision < tt.revision
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
        )
    )
    select
        case when _fetch_keys then ud_fetch_relation_keys(_etalon_id) else null end,
        case 
        when e.reltype = 'CONTAINS'::relation_type then (
            select array_agg((
                v.id,
                v.origin_id,
                v.shard,
                v.revision,
                v.valid_from,
                v.valid_to,
                v.create_date,
                v.created_by,
                v.status,
                v.approval,
                v.shift,
                v.operation_type,
                v.operation_id,
                case when _fetch_data then v.data_a else null end,
                case when _fetch_data then v.data_b else null end,
                v.major,
                v.minor)::relation_vistory_data)
            from unnest((ud_fetch_record_timeline(e.etalon_id_to, false, _fetch_data, _user_name, _is_approver)).vistory_data) v
        )
        else (   
            select array_agg((
                v.id,
                v.origin_id,
                v.shard,
                v.revision,
                v.valid_from,
                v.valid_to,
                v.create_date,
                v.created_by,
                v.status,
                v.approval,
                v.shift,
                v.operation_type,
                v.operation_id,
                case when _fetch_data then v.data_a else null end,
                case when _fetch_data then v.data_b else null end,
                v.major,
                v.minor
            )::relation_vistory_data)
            from t, relation_vistory v
            where 
                v.id = t.id
            and v.shard = _shard
            and not exists (  
                select true from t tt
                where t.origin_id = tt.origin_id
                and t.revision < tt.revision
                and (  
                    coalesce(t.valid_from, '-infinity') between coalesce(tt.valid_from, '-infinity') and coalesce(tt.valid_to, 'infinity')
                    and coalesce(t.valid_to, 'infinity') between coalesce(tt.valid_from, '-infinity') and coalesce(tt.valid_to, 'infinity')
                ) 
            )
        )
        end as vistory_data
    into
        _result.keys,
        _result.vistory_data
    from 
        relation_etalons e
    where 
        e.shard = _shard
    and e.lsn = _lsn;

    return _result;
end;
$BODY$;

-- Classifiers
create type classifier_vistory_data as (
    id uuid,
    origin_id uuid,
    shard int4,
    revision int4,
    valid_from timestamptz,
    valid_to timestamptz,
    create_date timestamptz,
    created_by varchar(256),
    status record_status,
    approval approval_state,
    shift data_shift,
    operation_type operation_type,
    operation_id text,
    data_a text,
    data_b bytea,
    major int4,
    minor int4);

create type classifier_timeline as (
    keys classifier_key,
    vistory_data classifier_vistory_data[]
);