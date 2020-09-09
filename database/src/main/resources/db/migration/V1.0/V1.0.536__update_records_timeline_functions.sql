

DROP FUNCTION public.ud_fetch_records_timeline_intervals(uuid, character varying, boolean);

CREATE OR REPLACE FUNCTION public.ud_fetch_records_timeline_intervals(
  _etalon_id uuid,
  _user_name character varying,
  _is_approver boolean)
  RETURNS TABLE(etalon_id uuid, period_id bigint, vf timestamp with time zone, vt timestamp with time zone, contributors text[])
LANGUAGE 'plpgsql'

COST 100
VOLATILE
ROWS 1000
AS $BODY$

begin
  return query
  --------------------- Recursive timeline
  with recursive t (origin_id, valid_from, valid_to, revision, status, approval, owner, last_update, operation_type) as (
    select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date, v.operation_type
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
    select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date, v.operation_type
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
  select _etalon_id,
         case when b.date_point = 'infinity' then 9223372036825200000 else (extract(epoch from b.date_point at time zone 'UTC') * 1000)::bigint end as period_id,
         case when a.date_point = '-infinity' then null else a.date_point end as valid_from,
         case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
         ( select array_agg(
                        row( t.origin_id,
                        t.revision,
                        (select source_system from origins where id = t.origin_id),
                        t.status,
                        t.approval,
                        t.owner,
                        to_char(t.last_update, 'YYYY-MM-DD HH24:MI:SS.MS'),
                        t.operation_type)::text ) from t,
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
                                                                                                     -- cut off revisions from the same source system and different oid
                                                                                                     -- which are overlapped due to inactivity of the newer period
                                                                                                   and not exists
                                                                                                     ( select true from origins o1, origins o2, t
                                                                                                       where
                                                                                                           t1.origin_id <> t.origin_id
                                                                                                         and o1.id = t.origin_id
                                                                                                         and o2.id = t1.origin_id
                                                                                                         and o1.source_system = o2.source_system
                                                                                                         and t.status = 'INACTIVE'
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
                                                                                                     -- cut off revisions from the same source system and different oid
                                                                                                     -- which are overlapped due to inactivity of the newer period
                                                                                                                                                                         and not exists
                                                                                                     ( select true from origins o1, origins o2, t
                                                                                                       where
                                                                                                           t2.origin_id <> t.origin_id
                                                                                                         and o1.id = t.origin_id
                                                                                                         and o2.id = t2.origin_id
                                                                                                         and o1.source_system = o2.source_system
                                                                                                         and t.status = 'INACTIVE'
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
                                                                                                     -- cut off revisions from the same source system and different oid
                                                                                                     -- which are overlapped due to inactivity of the newer period
                                                                                                   and not exists
                                                                                                     ( select true from origins o1, origins o2, t
                                                                                                       where
                                                                                                           t1.origin_id <> t.origin_id
                                                                                                         and o1.id = t.origin_id
                                                                                                         and o2.id = t1.origin_id
                                                                                                         and o1.source_system = o2.source_system
                                                                                                         and t.status = 'INACTIVE'
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
                                                                                                     -- cut off revisions from the same source system and different oid
                                                                                                     -- which are overlapped due to inactivity of the newer period
                                                                                                                                                                           and not exists
                                                                                                     ( select true from origins o1, origins o2, t
                                                                                                       where
                                                                                                           t2.origin_id <> t.origin_id
                                                                                                         and o1.id = t.origin_id
                                                                                                         and o2.id = t2.origin_id
                                                                                                         and o1.source_system = o2.source_system
                                                                                                         and t.status = 'INACTIVE'
                                                                                                         and t.last_update > t2.last_update -- theoretically impossible to have it null
                                                                                                         and (t2.valid_from between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
                                                                                               ) _b
         order by date_point asc ) as b
  where a.block_id = b.block_id;
  --------------------- End of recursive timeline
end;

$BODY$;

ALTER FUNCTION public.ud_fetch_records_timeline_intervals(uuid, character varying, boolean)
OWNER TO postgres;
