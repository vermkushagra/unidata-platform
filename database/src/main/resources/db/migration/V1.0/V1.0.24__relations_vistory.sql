-- etalons_relations
drop table if exists etalons_relations cascade;
create table etalons_relations
(
    id character(36) not null,
    name character varying(256) not null,
    etalon_id_from character(36) not null, -- FK
    etalon_id_to character(36) not null, -- FK
    version integer not null,
    create_date timestamp with time zone not null default now(),
    update_date timestamp with time zone,
    created_by character varying(256) not null,
    updated_by character varying(256),
    status character varying(256) not null default 'ACTIVE'::character varying,
    constraint pk_etalons_relations_pkey primary key (id),
    constraint fk_etalons_relations_etalon_id_from foreign key (etalon_id_from)
        references etalons (id) match full
        on update no action on delete no action,
    constraint fk_etalons_relations_etalon_id_to foreign key (etalon_id_to)
        references etalons (id) match full
        on update no action on delete no action
);


-- Index: ix_etalons_relations_name
drop index if exists ix_etalons_relations_name;
create index ix_etalons_relations_name on etalons_relations (name);

-- Index: ix_etalons_relations_name_name_from_name_to
drop index if exists uq_etalons_relations_name_etalon_id_from_etalon_id_to;
create index uq_etalons_relations_name_etalon_id_from_etalon_id_to on etalons_relations (name, etalon_id_from, etalon_id_to);

-- Index: ix_etalons_status
drop index if exists ix_etalons_relations_status;
create index ix_etalons_relations_status on etalons_relations (status);

-- origins_relations
drop table if exists origins_relations cascade;
create table origins_relations
(
    id character(36) not null,
    etalon_id character(36) not null, -- FK
    name character varying(256) not null,
    origin_id_from character(36) not null, -- FK
    origin_id_to character(36) not null, -- FK
    version integer not null,
    source_system character varying(256) not null,
    create_date timestamp with time zone not null default now(),
    update_date timestamp with time zone,
    created_by character varying(256) not null,
    updated_by character varying(256),
    status character varying(256) not null default 'ACTIVE'::character varying,
    constraint pk_origins_relations_pkey primary key (id),
    constraint fk_origins_relations_etalon_id foreign key (etalon_id) 
        references etalons_relations (id) match full 
        on update no action on delete no action,
    constraint fk_origins_relations_origin_id_from foreign key (origin_id_from) 
        references origins (id) match full 
        on update no action on delete no action,
    constraint fk_origins_relations_origin_id_to foreign key (origin_id_to) 
        references origins (id) match full 
        on update no action on delete no action
);

-- Index: ix_origins_relations_name
drop index if exists ix_origins_relations_name;
create index ix_origins_relations_name on etalons_relations (name);

-- UQ
alter table origins_relations drop constraint if exists uq_origins_relations_name_origin_id_from_origin_id_to;
alter table origins_relations add constraint uq_origins_relations_name_origin_id_from_origin_id_to unique (name, origin_id_from, origin_id_to);

-- IDX
drop index if exists ix_origins_relations_relations_status;
create index ix_origins_relations_relations_status on origins_relations (status);

-- origins_relations_vistory
drop table if exists origins_relations_vistory cascade;
create table origins_relations_vistory (
	id char(36),
	origin_id char(36) not null,
	revision integer not null,
	valid_from timestamp with time zone,
	valid_to timestamp with time zone,
	data text,
	create_date timestamp with time zone not null default current_timestamp,
	created_by character varying(256) not null,
	status character varying(256) not null default 'ACTIVE',
	constraint pk_origins_relations_vistory primary key (id),	
	constraint fk_origins_relations_origin_id foreign key (origin_id) references origins_relations (id) match full,
	constraint uq_origins_relations_vistory unique (origin_id, revision)
);

-- Index: ix_origins_relations_vistory_valid_from_valid_to
drop index if exists ix_origins_relations_vistory_valid_from_valid_to;
create index ix_origins_relations_vistory_valid_from_valid_to on origins_vistory (valid_from, valid_to);

-- drop reference
drop function if exists fetch_relations_timeline_intervals(_etalon_id character(36), _name character varying(256));

-- fn
create or replace function fetch_relations_timeline_intervals(_etalon_id_from character(36), _relation_name character varying(256)) 
returns table(
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[]) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status
        from origins_relations_vistory v,
        (   select  i.origin_id, 			
            max(i.revision) as revision
            from origins_relations o, origins_relations_vistory i, etalons_relations e
            where 
                e.etalon_id_from = _etalon_id_from
            and e.name = _relation_name
            and o.etalon_id = e.id
            and i.origin_id = o.id
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status
        from origins_relations_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (  select max(i.revision) from origins_relations_vistory i
                            where i.origin_id = t.origin_id
                            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity') 
                            or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                            and i.revision < t.revision  )
        )

    --------------------- Join sorted
    select case when a.date_point = '-infinity' then null else a.date_point end as valid_from, 
           case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
           ( select array_agg(row(t.origin_id, t.revision, (select source_system from origins_relations where id = t.origin_id), t.status)::text) from t,
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
