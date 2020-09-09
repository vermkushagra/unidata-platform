-- 1. Rename column
do $$
declare
    needs_update boolean := false;
begin
    needs_update := coalesce((SELECT true FROM information_schema.columns WHERE table_name = 'etalons' and column_name = 'lock_id' 
        and not exists (SELECT true FROM information_schema.columns WHERE table_name = 'etalons' and column_name = 'gsn')), false);
    if (needs_update = true) then
        raise notice 'Table [etalons] has [lock_id] column and will be updated';
        alter table etalons rename column lock_id to gsn;
        create unique index ix_etalons_gsn on etalons using btree (gsn);
    else 
        raise notice 'Table [etalons] does not have [lock_id] column and will not be updated';
    end if;

    needs_update := coalesce((SELECT true FROM information_schema.columns WHERE table_name = 'origins' and column_name = 'lock_id'
        and not exists (SELECT true FROM information_schema.columns WHERE table_name = 'origins' and column_name = 'gsn')), false);
    if (needs_update = true) then
        raise notice 'Table [origins] has [lock_id] column and will be updated';
        alter table origins rename column lock_id to gsn;
        create unique index ix_origins_gsn on origins using btree (gsn);
    else 
        raise notice 'Table [origins] does not have [lock_id] column and will not be updated';
    end if;

    needs_update := coalesce((SELECT true FROM information_schema.columns WHERE table_name = 'etalons_relations' and column_name = 'lock_id'
        and not exists (SELECT true FROM information_schema.columns WHERE table_name = 'etalons_relations' and column_name = 'gsn')), false);
    if (needs_update = true) then
        raise notice 'Table [etalons_relations] has [lock_id] column and will be updated';
        alter table etalons_relations rename column lock_id to gsn;
        create unique index ix_etalons_relations_gsn on etalons_relations using btree (gsn);
    else 
        raise notice 'Table [etalons_relations] does not have [lock_id] column and will not be updated';
    end if;

    needs_update := coalesce((SELECT true FROM information_schema.columns WHERE table_name = 'origins_relations' and column_name = 'lock_id'
        and not exists (SELECT true FROM information_schema.columns WHERE table_name = 'origins_relations' and column_name = 'gsn')), false);
    if (needs_update = true) then
        raise notice 'Table [origins_relations] has [lock_id] column and will be updated';
        alter table origins_relations rename column lock_id to gsn;
        create unique index ix_origins_relations_gsn on origins_relations using btree (gsn);
    else 
        raise notice 'Table [origins_relations] does not have [lock_id] column and will not be updated';
    end if;

    needs_update := coalesce((SELECT true FROM information_schema.columns WHERE table_name = 'etalons_classifiers' and column_name = 'lock_id'
        and not exists (SELECT true FROM information_schema.columns WHERE table_name = 'etalons_classifiers' and column_name = 'gsn')), false);
    if (needs_update = true) then
        raise notice 'Table [etalons_classifiers] has [lock_id] column and will be updated';
        alter table etalons_classifiers rename column lock_id to gsn;
        create unique index ix_etalons_classifiers_gsn on etalons_classifiers using btree (gsn);
    else 
        raise notice 'Table [etalons_classifiers] does not have [lock_id] column and will not be updated';
    end if;

    needs_update := coalesce((SELECT true FROM information_schema.columns WHERE table_name = 'origins_classifiers' and column_name = 'lock_id'
        and not exists (SELECT true FROM information_schema.columns WHERE table_name = 'origins_classifiers' and column_name = 'gsn')), false);
    if (needs_update = true) then
        raise notice 'Table [origins_classifiers] has [lock_id] column and will be updated';
        alter table origins_classifiers rename column lock_id to gsn;
        create unique index ix_origins_classifiers_gsn on origins_classifiers using btree (gsn);
    else 
        raise notice 'Table [origins_classifiers] does not have [lock_id] column and will not be updated';
    end if;
end$$ language plpgsql;

alter table etalons alter column gsn set not null; 
alter table etalons alter column gsn set default nextval('global_lock_id_seq');

alter table origins alter column gsn set not null;
alter table origins alter column gsn set default nextval('global_lock_id_seq');

alter table etalons_relations alter column gsn set not null;
alter table etalons_relations alter column gsn set default nextval('global_lock_id_seq');

alter table origins_relations alter column gsn set not null;
alter table origins_relations alter column gsn set default nextval('global_lock_id_seq');

alter table etalons_classifiers alter column gsn set not null;
alter table etalons_classifiers alter column gsn set default nextval('global_lock_id_seq');

alter table origins_classifiers alter column gsn set not null;
alter table origins_classifiers alter column gsn set default nextval('global_lock_id_seq');

alter table etalons drop column if exists lock_id;
alter table origins drop column if exists lock_id;
alter table etalons_relations drop column if exists lock_id;
alter table origins_relations drop column if exists lock_id;
alter table etalons_classifiers drop column if exists lock_id;
alter table origins_classifiers drop column if exists lock_id;

-- 2. Modify functions
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
    perform pg_advisory_xact_lock(gsn) from origins where id = _origin_id;
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
    perform pg_advisory_xact_lock(gsn) from origins_relations where id = _origin_id;
    insert into origins_relations_vistory (id, origin_id, operation_id, revision, valid_from, valid_to, data, created_by, status, approval)
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
    perform pg_advisory_xact_lock(gsn) from origins_classifiers where id = _origin_id;
    insert into origins_classifiers_vistory (id, origin_id, operation_id, revision, valid_from, valid_to, data, created_by, status, approval)
    values (_id, _origin_id, _operation_id,
        coalesce((select max(prev.revision) + 1 from origins_classifiers_vistory prev where prev.origin_id = _origin_id), 1), 
        _valid_from, _valid_to, _data, _created_by, _status, _approval);
end
$$ language plpgsql;

-- 3. Etalons batch blocks
create or replace function ud_calc_etalon_batch_blocks(_start bigint, _block int, _name varchar(256))
returns table (block_num int, start_id character(36), start_gsn bigint, end_id character(36), end_gsn bigint)
as $$
declare
    cur_gsn bigint := coalesce(_start, -9223372036854775808);
    cur_block int := 0;
begin

    create temporary table __result (block_num int not null primary key, start_id character(36), start_gsn bigint, end_id character(36), end_gsn bigint) on commit drop;

    while true loop

        if (_name is null) then
            with _block1 as (select id, gsn from etalons where gsn >= cur_gsn order by gsn limit _block),
                 _block_start1 as (select id, gsn from _block1 order by gsn limit 1),
                 _block_end1 as (select id, gsn from _block1 order by gsn desc limit 1)
            
            insert into __result (block_num, start_id, start_gsn, end_id, end_gsn) 
            select cur_block, _block_start1.id, _block_start1.gsn, _block_end1.id, _block_end1.gsn from _block_start1, _block_end1;

            cur_gsn := (select __result.end_gsn from __result where __result.block_num = cur_block);
            if cur_gsn is null then
                exit;
            end if;

            cur_gsn := cur_gsn + 1;
            cur_block := cur_block + 1;
            
        else
            with _block2 as (select id, gsn from etalons where name = _name and gsn >= cur_gsn order by gsn limit _block),
                 _block_start2 as (select id, gsn from _block2 order by gsn limit 1),
                 _block_end2 as (select id, gsn from _block2 order by gsn desc limit 1)

            insert into __result (block_num, start_id, start_gsn, end_id, end_gsn) 
            select cur_block, _block_start2.id, _block_start2.gsn, _block_end2.id, _block_end2.gsn from _block_start2, _block_end2;

            cur_gsn := (select __result.end_gsn from __result where __result.block_num = cur_block);
            if cur_gsn is null then
                exit;
            end if;

            cur_gsn := cur_gsn + 1;
            cur_block := cur_block + 1;
            
        end if;
        
    end loop;
    return query select __result.block_num, __result.start_id, __result.start_gsn, __result.end_id, __result.end_gsn from __result;
    
end$$ language plpgsql;