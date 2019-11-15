create type update_mark as (update_date timestamptz, updated_by varchar);

-- Records
create or replace function ud_fetch_record_update_mark(_etalon_id uuid, _shard int4)
returns update_mark
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result update_mark;
begin
    
    if _shard is null then
        _shard := ud_get_record_etalon_shard_number(_etalon_id); 
    end if;
    
    select ts, usr into _result.update_date, _result.updated_by from 
    (
              select update_date as ts, updated_by as usr from record_etalons where id = _etalon_id and shard = _shard
    union all select update_date as ts, updated_by as usr from record_origins where etalon_id = _etalon_id and shard = _shard
    union all select create_date as ts, created_by as usr from (
              select v.create_date, v.created_by
              from record_origins o, record_vistory v 
              where 
                  o.etalon_id = _etalon_id
              and o.shard = _shard
              and v.origin_id = o.id 
              and v.shard = _shard
              and (v.approval <> 'DECLINED' and v.approval <> 'PENDING')
              order by v.create_date desc fetch first 1 rows only ) f
    ) t order by ts desc nulls last fetch first 1 rows only;
    
    return _result;
end;
$BODY$;

-- Relations
create or replace function ud_fetch_relation_update_mark(_etalon_id uuid, _shard int4)
returns update_mark
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result update_mark;
begin
    
    if _shard is null then
        _shard := ud_get_relation_etalon_shard_number(_etalon_id); 
    end if;
    
    select ts, usr into _result.update_date, _result.updated_by from 
    (
              select update_date as ts, updated_by as usr from relation_etalons where id = _etalon_id and shard = _shard and reltype != 'CONTAINS'::relation_type
    union all select o.update_date as ts, o.updated_by as usr 
              from relation_origins o, relation_etalons e 
              where 
                  e.id = _etalon_id 
              and e.shard = _shard
              and e.reltype != 'CONTAINS'::relation_type 
              and o.etalon_id = e.id 
              and o.shard = _shard
    union all select f.create_date as ts, f.created_by as usr from (
              select v.create_date, v.created_by 
              from relation_etalons e, relation_origins o, relation_vistory v 
              where 
                  e.id = _etalon_id
              and e.shard = _shard
              and e.reltype != 'CONTAINS'::relation_type
              and o.etalon_id = e.id
              and o.shard = _shard
              and v.origin_id = o.id 
              and v.shard = _shard
              and (v.approval <> 'DECLINED' and v.approval <> 'PENDING')
              order by v.create_date desc fetch first 1 rows only ) f
    union all select m.update_date as ts, m.updated_by from relation_etalons, lateral ud_fetch_record_update_mark(relation_etalons.etalon_id_to, null) m
        where 
            id = _etalon_id 
        and shard = _shard
        and reltype = 'CONTAINS'::relation_type
    ) t order by ts desc nulls last fetch first 1 rows only;
    
    return _result;
end;
$BODY$;

-- Classifiers
create or replace function ud_fetch_classifier_update_mark(_etalon_id uuid, _shard int4)
returns update_mark
language plpgsql
stable leakproof parallel safe
as $BODY$
declare
    _result update_mark;
begin
    
    if _shard is null then
        _shard := ud_get_classifier_etalon_shard_number(_etalon_id); 
    end if;
    
    select ts, usr into _result.update_date, _result.updated_by from 
    (
              select update_date as ts, updated_by as usr from classifier_etalons where id = _etalon_id and shard = _shard
        union select update_date as ts, updated_by as usr from classifier_origins where etalon_id = _etalon_id and shard = _shard
        union select create_date as ts, created_by as usr from (
            select v.create_date, v.created_by 
            from classifier_origins o, classifier_vistory v 
            where 
                o.etalon_id = _etalon_id
            and o.shard = _shard
            and v.origin_id = o.id 
            and v.shard = _shard
            and (v.approval <> 'DECLINED' and v.approval <> 'PENDING')
            order by v.create_date desc fetch first 1 rows only ) f
    ) t order by ts desc nulls last fetch first 1 rows only;

    return _result;
end;
$BODY$;
