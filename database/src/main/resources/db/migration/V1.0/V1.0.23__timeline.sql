-- drop reference
drop function if exists fetch_timeline(_etalon_id character(36));

-- contributor
drop type if exists contributor cascade; 
create type contributor as (
    origin_id character(36),
    revision integer,
    source_system character varying(256),
    status character varying(256)
);

-- fn
create or replace function fetch_timeline(_etalon_id character(36)) 
returns table(
    point_in_time timestamp with time zone, 
    contributors contributor[]) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status
        from origins_vistory v,
        (   select  i.origin_id, 			
            max(i.revision) as revision
            from origins o, origins_vistory i 
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id		
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status
        from origins_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  select max(i.revision) from origins_vistory i
                            where i.origin_id = t.origin_id
                            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
                              or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                            and i.revision < t.revision  )
        )
    --------------------- Join sorted
    select date_point, 
        (   select array_agg(row(t.origin_id, t.revision, (select source_system from origins where id = t.origin_id), t.status)::contributor) from t,
            (   select i.origin_id, 			
                max(i.revision) as revision
                from t i
                where coalesce(i.valid_from, '-infinity') <= s.date_point 
                and coalesce(i.valid_to, 'infinity') >= s.date_point
                group by i.origin_id
            ) as k
            where k.origin_id = t.origin_id
            and k.revision = t.revision ) as contributors 
    from (  select coalesce(t.valid_from, '-infinity') as date_point from t
      union select coalesce(t.valid_to, 'infinity') as date_point from t  ) s order by date_point asc;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;

-- drop reference
drop function if exists fetch_timeline_intervals(_etalon_id character(36));

-- fn
create or replace function fetch_timeline_intervals(_etalon_id character(36)) 
returns table(
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[]) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status
        from origins_vistory v,
        (   select  i.origin_id, 			
            max(i.revision) as revision
            from origins o, origins_vistory i 
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id		
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status
        from origins_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  select max(i.revision) from origins_vistory i
                            where i.origin_id = t.origin_id
                            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
                              or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                            and i.revision < t.revision  )
        )
    --------------------- Join sorted
    select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
           case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
           ( select array_agg(row(t.origin_id, t.revision, (select source_system from origins where id = t.origin_id), t.status)::text) from t,
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

-- fn
create or replace function fetch_etalon_boundary(_etalon_id character(36), _ts timestamp with time zone) 
returns table(
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors contributor[]) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status
        from origins_vistory v,
        (   select  i.origin_id, 			
            max(i.revision) as revision
            from origins o, origins_vistory i 
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id		
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status
        from origins_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  select max(i.revision) from origins_vistory i
                            where i.origin_id = t.origin_id
                            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
                              or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                            and i.revision < t.revision  )
        )
    --------------------- Join sorted
    select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
           case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
           ( select array_agg(row(t.origin_id, t.revision, (select source_system from origins where id = t.origin_id), t.status)::contributor) from t,
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
    where a.block_id = b.block_id
    and (a.date_point <= _ts and b.date_point >= _ts);
    --------------------- End of recursive timeline
end;
$$ language plpgsql;