-- drop reference
drop function if exists fetch_timeline_intervals(_etalon_id character(36));
drop function if exists fetch_timeline_intervals(_etalon_id character(36), _user_name character varying(256), _is_approver boolean);
drop function if exists fetch_records_timeline_intervals(_etalon_id character(36), _user_name character varying(256), _is_approver boolean);

-- fn
create or replace function fetch_records_timeline_intervals(_etalon_id character(36), _user_name character varying(256), _is_approver boolean) 
returns table(
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[]) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval, owner, last_update) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_vistory v,
        (   select  i.origin_id,            
            max(i.revision) as revision
            from origins o, origins_vistory i 
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  
            select max(i.revision) from origins_vistory i
            where i.origin_id = t.origin_id
            and i.status <> 'MERGED'
            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
              or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
            and i.revision < t.revision
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))) 
        ))
    --------------------- Join sorted
    select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
           case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
           ( select array_agg(
                row( t.origin_id, 
                     t.revision, 
                     (select source_system from origins where id = t.origin_id), 
                     t.status, 
                     t.approval, 
                     t.owner, 
                     to_char(t.last_update, 'YYYY-MM-DD HH24:MI:SS.MS'))::text ) from t,
              ( select i.origin_id,             
                max(i.revision) as revision
                from t i
                where coalesce(i.valid_from, '-infinity') <= a.date_point 
                and coalesce(i.valid_to, 'infinity') >= b.date_point
                group by i.origin_id ) k
              where k.origin_id = t.origin_id
              and k.revision = t.revision ) as contributors 
    from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from 
            ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1 
              where not exists 
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t 
                where t1.origin_id = origin_id
                and t1.revision < revision
                and coalesce(t1.valid_from, '-infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists 
              ( select true from origins o1, origins o2, t
                where 
                    t1.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t1.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t1.last_update -- theoretically impossible to have it null
                and (coalesce(t1.valid_from, '-infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) ) 
              union 
              select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null 
              and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to)
              and not exists 
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t 
                where t2.origin_id = origin_id
                and t2.revision < revision
                and t2.valid_to between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists 
              ( select true from origins o1, origins o2, t
                where 
                    t2.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t2.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t2.last_update -- theoretically impossible to have it null
                and (t2.valid_to between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) ) 
              ) _a
              order by date_point asc ) as a,
         ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
            ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1 
              -- cut off garbage revisions from the same oid which were selected by one active end
              where not exists 
              ( select true from t 
                where t1.origin_id = origin_id
                and t1.revision < revision
                and coalesce(t1.valid_to, 'infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists 
              ( select true from origins o1, origins o2, t
                where 
                    t1.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t1.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t1.last_update -- theoretically impossible to have it null
                and (coalesce(t1.valid_to, 'infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) ) 
              union 
              select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
              and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from)
              and not exists 
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t 
                where t2.origin_id = origin_id
                and t2.revision < revision
                and t2.valid_from between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') ) 
              -- cut off older revisions from the same source system and different oid
              and not exists 
              ( select true from origins o1, origins o2, t
                where 
                    t2.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t2.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t2.last_update -- theoretically impossible to have it null
                and (t2.valid_from between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
              ) _b
              order by date_point asc ) as b
    where a.block_id = b.block_id;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;

-- drop reference
drop function if exists fetch_etalon_boundary(_etalon_id character(36), _ts timestamp with time zone);
drop function if exists fetch_etalon_boundary(_etalon_id character(36), _ts timestamp with time zone, _user_name character varying(256), _is_approver boolean);
drop function if exists fetch_records_etalon_boundary(_etalon_id character(36), _ts timestamp with time zone, _user_name character varying(256), _is_approver boolean);

-- fn
create or replace function fetch_records_etalon_boundary(_etalon_id character(36), _ts timestamp with time zone, _user_name character varying(256), _is_approver boolean) 
returns table(
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[],
    created_by character varying(256),
    updated_by character varying(256),
    create_date timestamp with time zone,
    update_date timestamp with time zone, 
    name character varying(256),
    status character varying(256)) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval, owner, last_update) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_vistory v,
        (   select  i.origin_id,            
            max(i.revision) as revision
            from origins o, origins_vistory i 
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  
            select max(i.revision) from origins_vistory i
            where i.origin_id = t.origin_id
            and i.status <> 'MERGED'
            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
              or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
            and i.revision < t.revision
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))  
        ))
    --------------------- Join sorted
    select q.valid_from, q.valid_to, q.contributors, e.created_by,
        (select v.created_by from origins_vistory v, origins o 
         where o.etalon_id = e.id and v.origin_id = o.id 
         order by v.create_date desc fetch first 1 rows only ) as updated_by,
        (select min(v.create_date) from origins_vistory v, origins o 
         where o.etalon_id = e.id and v.origin_id = o.id) as create_date,
        (select max(v.create_date) from origins_vistory v, origins o 
         where o.etalon_id = e.id and v.origin_id = o.id) as update_date,
        e.name,
        e.status
    from (    
        select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
               case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
               ( select array_agg(
                 row( t.origin_id, 
                      t.revision, 
                      (select source_system from origins where id = t.origin_id), 
                      t.status, 
                      t.approval, 
                      t.owner, 
                      to_char(t.last_update, 'YYYY-MM-DD HH24:MI:SS.MS') )::text) from t,
                  ( select i.origin_id,
                    max(i.revision) as revision
                    from t i
                    where coalesce(i.valid_from, '-infinity') <= a.date_point 
                    and coalesce(i.valid_to, 'infinity') >= b.date_point
                    group by i.origin_id ) k
                  where k.origin_id = t.origin_id
                  and k.revision = t.revision ) as contributors,
              _etalon_id as etalon_id
        from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from 
                ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1 
                  where not exists
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t 
                    where t1.origin_id = origin_id
                    and t1.revision < revision
                    and coalesce(t1.valid_from, '-infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists (
                    select true from origins o1, origins o2, t
                    where 
                        t1.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t1.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t1.last_update -- theoretically impossible to have it null
                    and (coalesce(t1.valid_from, '-infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity'))
                  ) 
                  union 
                  select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null 
                  and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to)
                  and not exists                  
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t 
                    where t2.origin_id = origin_id
                    and t2.revision < revision
                    and t2.valid_to between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists 
                  ( select true from origins o1, origins o2, t
                    where 
                        t2.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t2.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t2.last_update -- theoretically impossible to have it null
                    and (t2.valid_to between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity'))) 
                  ) _a
                  order by date_point asc ) as a,
             ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
                ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1 
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  where not exists 
                  ( select true from t 
                    where t1.origin_id = origin_id
                    and t1.revision < revision
                    and coalesce(t1.valid_to, 'infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists 
                  ( select true from origins o1, origins o2, t
                    where 
                        t1.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t1.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t1.last_update -- theoretically impossible to have it null
                    and (coalesce(t1.valid_to, 'infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) ) 
                  union 
                  select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
                  and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from)
                  and not exists 
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t 
                    where t2.origin_id = origin_id
                    and t2.revision < revision
                    and t2.valid_from between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') ) 
                  -- cut off older revisions from the same source system and different oid
                  and not exists 
                  ( select true from origins o1, origins o2, t
                    where 
                        t2.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t2.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t2.last_update -- theoretically impossible to have it null
                    and (t2.valid_from between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')))
                   ) _b
                  order by date_point asc ) as b      
        where a.block_id = b.block_id
        and (a.date_point <= _ts and b.date_point >= _ts)
    ) q, etalons e
    where e.id = q.etalon_id;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;
--------------------------------------------------
-- drop fn
drop function if exists fetch_relations_etalon_boundary(_etalon_id character(36), _ts timestamp with time zone);
drop function if exists fetch_relations_etalon_boundary(_etalon_id character(36), _ts timestamp with time zone, _user_name character varying(256), _is_approver boolean);
-- fn
create or replace function fetch_relations_etalon_boundary(
    _etalon_id character(36), 
    _ts timestamp with time zone,
    _user_name character varying(256), 
    _is_approver boolean)
returns table(
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[],
    created_by character varying(256),
    updated_by character varying(256),
    create_date timestamp with time zone, 
    update_date timestamp with time zone, 
    name character varying(256),
    status character varying(256)) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval, owner, last_update) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_relations_vistory v,
        (   select  i.origin_id,            
            max(i.revision) as revision
            from origins_relations o, origins_relations_vistory i
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
            from origins_relations_vistory v, t
            where v.origin_id = t.origin_id
            and v.revision = (  
                select max(i.revision) from origins_relations_vistory i
                where i.origin_id = t.origin_id
                and i.status <> 'MERGED'
                and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity')
                or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                and i.revision < t.revision
                and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))  
            )
        )
   
    --------------------- Join sorted
    select q.valid_from, q.valid_to, q.contributors, e.created_by,
        (select v.created_by from origins_relations_vistory v, origins_relations o
         where v.origin_id = o.id and o.etalon_id = e.id
         order by v.create_date desc fetch first 1 rows only) as updated_by,
        (select v.create_date from origins_relations_vistory v, origins_relations o
         where v.origin_id = o.id and o.etalon_id = e.id 
         order by v.create_date asc fetch first 1 rows only) as create_date,
        (select v.create_date from origins_relations_vistory v, origins_relations o
         where v.origin_id = o.id and o.etalon_id = e.id 
         order by v.create_date desc fetch first 1 rows only) as update_date,
        e.name,
        e.status
    from ( 
        select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
               case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
               ( select array_agg(
                  row( t.origin_id, 
                       t.revision, 
                       (select source_system from origins_relations where id = t.origin_id), 
                       t.status, 
                       t.approval, 
                       t.owner, 
                       to_char(t.last_update, 'YYYY-MM-DD HH24:MI:SS.MS'))::text) from t,
                  ( select i.origin_id,
                    max(i.revision) as revision
                    from t i
                    where coalesce(i.valid_from, '-infinity') <= a.date_point 
                    and coalesce(i.valid_to, 'infinity') >= b.date_point
                    group by i.origin_id ) k
                  where k.origin_id = t.origin_id
                  and k.revision = t.revision ) as contributors,
               _etalon_id as etalon_id
        from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from 
                ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1 
                  where not exists
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t 
                    where t1.origin_id = origin_id
                    and t1.revision < revision
                    and coalesce(t1.valid_from, '-infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists (
                    select true from origins o1, origins o2, t
                    where 
                        t1.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t1.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t1.last_update -- theoretically impossible to have it null
                    and (coalesce(t1.valid_from, '-infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) ) 
                  union 
                  select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null 
                  and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to)
                  and not exists                  
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t 
                    where t2.origin_id = origin_id
                    and t2.revision < revision
                    and t2.valid_to between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists 
                  ( select true from origins o1, origins o2, t
                    where 
                        t2.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t2.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t2.last_update -- theoretically impossible to have it null
                    and (t2.valid_to between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity'))) 
                  ) _a
                  order by date_point asc ) as a,
             ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
                ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1 
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  where not exists 
                  ( select true from t 
                    where t1.origin_id = origin_id
                    and t1.revision < revision
                    and coalesce(t1.valid_to, 'infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists 
                  ( select true from origins o1, origins o2, t
                    where 
                        t1.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t1.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t1.last_update -- theoretically impossible to have it null
                    and (coalesce(t1.valid_to, 'infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) ) 
                  union 
                  select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
                  and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from)
                  and not exists 
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t 
                    where t2.origin_id = origin_id
                    and t2.revision < revision
                    and t2.valid_from between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') ) 
                  -- cut off older revisions from the same source system and different oid
                  and not exists 
                  ( select true from origins o1, origins o2, t
                    where 
                        t2.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t2.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t2.last_update -- theoretically impossible to have it null
                    and (t2.valid_from between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
                   ) _b
                  order by date_point asc ) as b
        where a.block_id = b.block_id
        and (a.date_point <= _ts and b.date_point >= _ts)
    ) q, etalons_relations e
    where e.id = q.etalon_id;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;
-----------------------------------------------------------------------------------
-- drop reference
drop function if exists fetch_relations_timeline_intervals(_etalon_id character(36), _name character varying(256));
drop function if exists fetch_relations_timeline_intervals2(_etalon_id character(36), _name character varying(256));
drop function if exists fetch_relations_timeline_intervals2(_etalon_id character(36));
drop function if exists fetch_relations_timeline_intervals(_etalon_id character(36));
drop function if exists fetch_relations_timeline_intervals(_etalon_id character(36), _user_name character varying(256), _is_approver boolean);

-- fn
create or replace function fetch_relations_timeline_intervals(
    _etalon_id character(36), 
    _user_name character varying(256), 
    _is_approver boolean) 
returns table(
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[]) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval, owner, last_update) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_relations_vistory v,
        (   select  i.origin_id,            
            max(i.revision) as revision
            from origins_relations o, origins_relations_vistory i
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_relations_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  
            select max(i.revision) from origins_relations_vistory i
            where i.origin_id = t.origin_id
            and i.status <> 'MERGED'
            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
            or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
            and i.revision < t.revision
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))  
            )
        )

    --------------------- Join sorted
    select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
           case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
           ( select array_agg(
              row( t.origin_id, 
                   t.revision, 
                   (select source_system from origins_relations where id = t.origin_id), 
                   t.status, 
                   t.approval, 
                   t.owner, 
                   to_char(t.last_update, 'YYYY-MM-DD HH24:MI:SS.MS'))::text ) from t,
              ( select i.origin_id,             
                max(i.revision) as revision
                from t i
                where coalesce(i.valid_from, '-infinity') <= a.date_point 
                and coalesce(i.valid_to, 'infinity') >= b.date_point
                group by i.origin_id ) k
              where k.origin_id = t.origin_id
              and k.revision = t.revision ) as contributors 
    from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from 
            ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1 
              where not exists
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t 
                where t1.origin_id = origin_id
                and t1.revision < revision
                and coalesce(t1.valid_from, '-infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists (
                select true from origins o1, origins o2, t
                where 
                    t1.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t1.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t1.last_update -- theoretically impossible to have it null
                and (coalesce(t1.valid_from, '-infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) ) 
              union 
              select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null 
              and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to)
              and not exists                  
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t 
                where t2.origin_id = origin_id
                and t2.revision < revision
                and t2.valid_to between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists 
              ( select true from origins o1, origins o2, t
                where 
                    t2.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t2.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t2.last_update -- theoretically impossible to have it null
                and (t2.valid_to between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity'))) 
              ) _a
              order by date_point asc ) as a,
         ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
            ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1 
              -- cut off garbage revisions from the same oid which were selected by one active end
              where not exists 
              ( select true from t 
                where t1.origin_id = origin_id
                and t1.revision < revision
                and coalesce(t1.valid_to, 'infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists 
              ( select true from origins o1, origins o2, t
                where 
                    t1.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t1.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t1.last_update -- theoretically impossible to have it null
                and (coalesce(t1.valid_to, 'infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) ) 
              union 
              select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
              and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from)
              and not exists 
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t 
                where t2.origin_id = origin_id
                and t2.revision < revision
                and t2.valid_from between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') ) 
              -- cut off older revisions from the same source system and different oid
              and not exists 
              ( select true from origins o1, origins o2, t
                where 
                    t2.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t2.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t2.last_update -- theoretically impossible to have it null
                and (t2.valid_from between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')))
               ) _b
              order by date_point asc ) as b
    where a.block_id = b.block_id;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;

