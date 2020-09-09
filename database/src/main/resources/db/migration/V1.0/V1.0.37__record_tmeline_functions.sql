-- contributor
drop type if exists contributor cascade; 
create type contributor as (
    origin_id character(36),
    revision integer,
    source_system character varying(256),
    status character varying(256),
    approval character varying(256)
);

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
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval
        from origins_vistory v,
        (   select  i.origin_id,            
            max(i.revision) as revision
            from origins o, origins_vistory i 
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval
        from origins_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  select max(i.revision) from origins_vistory i
                            where i.origin_id = t.origin_id
                            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
                              or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                            and i.revision < t.revision
                            and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))  )
        )
    --------------------- Join sorted
    select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
           case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
           ( select array_agg(row(t.origin_id, t.revision, (select source_system from origins where id = t.origin_id), t.status, t.approval)::text) from t,
              ( select i.origin_id,             
                max(i.revision) as revision
                from t i
                where coalesce(i.valid_from, '-infinity') <= a.date_point 
                and coalesce(i.valid_to, 'infinity') >= b.date_point
                group by i.origin_id ) k
              where k.origin_id = t.origin_id
              and k.revision = t.revision ) as contributors 
    from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from 
            ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1 union 
              select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null 
              and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to) ) _a
              order by date_point asc ) as a,
         ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
            ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1 union 
              select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
              and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from) ) _b
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
    create_date timestamp with time zone, 
    created_by character varying(256), 
    update_date timestamp with time zone, 
    updated_by character varying(256),
    name character varying(256),
    status character varying(256)) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval
        from origins_vistory v,
        (   select  i.origin_id,            
            max(i.revision) as revision
            from origins o, origins_vistory i 
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval
        from origins_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  select max(i.revision) from origins_vistory i
                            where i.origin_id = t.origin_id
                            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
                              or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                            and i.revision < t.revision
                            and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))  )
        )
    --------------------- Join sorted
    select q.valid_from, q.valid_to, q.contributors, e.create_date, e.created_by,
        (select max(v.create_date) from origins_vistory v, origins o 
         where o.etalon_id = e.id and v.origin_id = o.id) as update_date,
        (select v.created_by from origins_vistory v, origins o 
         where o.etalon_id = e.id and v.origin_id = o.id 
         order by v.create_date desc fetch first 1 rows only ) as updated_by,
        e.name,
        e.status
    from (    
        select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
               case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
               ( select array_agg(row(t.origin_id, t.revision, (select source_system from origins where id = t.origin_id), t.status, t.approval)::text) from t,
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
                ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1 union 
                  select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null 
                  and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to) ) _a
                  order by date_point asc ) as a,
             ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
                ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1 union 
                  select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
                  and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from) ) _b
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
    create_date timestamp with time zone, 
    created_by character varying(256),    
    update_date timestamp with time zone, 
    updated_by character varying(256),
    name character varying(256),
    status character varying(256)) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval
        from origins_relations_vistory v,
        (   select  i.origin_id,            
            max(i.revision) as revision
            from origins_relations o, origins_relations_vistory i
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval
            from origins_relations_vistory v, t
            where v.origin_id = t.origin_id
            and v.revision = (  select max(i.revision) from origins_relations_vistory i
                                where i.origin_id = t.origin_id
                                and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity')
                                or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                                and i.revision < t.revision
                                and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))  )
        )
   
    --------------------- Join sorted
    select q.valid_from, q.valid_to, q.contributors, e.create_date, e.created_by,
        (select v.create_date from origins_relations_vistory v, origins_relations o
         where v.origin_id = o.id and o.etalon_id = e.id 
         order by v.create_date desc fetch first 1 rows only) as update_date,
        (select v.created_by from origins_relations_vistory v, origins_relations o
         where v.origin_id = o.id and o.etalon_id = e.id
         order by v.create_date desc fetch first 1 rows only) as updated_by,
        e.name,
        e.status
    from ( 
        select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
               case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
               ( select array_agg(row(t.origin_id, t.revision, (select source_system from origins_relations where id = t.origin_id), t.status, t.approval)::text) from t,
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
                ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1 union 
                  select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null 
                  and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to) ) _a
                  order by date_point asc ) as a,
             ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
                ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1 union 
                  select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
                  and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from) ) _b
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
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval
        from origins_relations_vistory v,
        (   select  i.origin_id,            
            max(i.revision) as revision
            from origins_relations o, origins_relations_vistory i
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval
        from origins_relations_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  select max(i.revision) from origins_relations_vistory i
                            where i.origin_id = t.origin_id
                            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
                            or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                            and i.revision < t.revision
                            and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name))  )
        )

    --------------------- Join sorted
    select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
           case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
           ( select array_agg(row(t.origin_id, t.revision, (select source_system from origins_relations where id = t.origin_id), t.status, t.approval)::text) from t,
              ( select i.origin_id,             
                max(i.revision) as revision
                from t i
                where coalesce(i.valid_from, '-infinity') <= a.date_point 
                and coalesce(i.valid_to, 'infinity') >= b.date_point
                group by i.origin_id ) k
              where k.origin_id = t.origin_id
              and k.revision = t.revision ) as contributors 
    from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from 
            ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1 union 
              select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null 
              and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to) ) _a
              order by date_point asc ) as a,
         ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
            ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1 union 
              select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
              and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from) ) _b
              order by date_point asc ) as b      
    where a.block_id = b.block_id;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;

