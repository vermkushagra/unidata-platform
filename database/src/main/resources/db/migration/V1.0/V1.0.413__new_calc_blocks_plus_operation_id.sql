do
$$
declare
begin
    -- 1. Restore FKeys
    -- Clean etalons relations without one of etalon
    delete from etalons_relations where etalon_id_from not in (select id from etalons) or etalon_id_to not in (select id from etalons);
    -- Clean origins relations without one of origin
    delete from origins_relations where origin_id_from not in (select id from origins) or origin_id_to not in (select id from origins);

    -- Add constraints to etalons relations
    if not exists (select true from information_schema.constraint_table_usage
                   where table_schema = 'public' and constraint_name = 'fk_etalons_relations_etalon_id_from') then
        raise notice 'Constraint fk_etalons_relations_etalon_id_from does not exist. Creating.';
        alter table public.etalons_relations add constraint fk_etalons_relations_etalon_id_from foreign key (etalon_id_from) references public.etalons (id) on update no action on delete cascade;
    else
        raise notice 'Constraint fk_etalons_relations_etalon_id_from already exists. Skept.';
    end if;

    if not exists (select true from information_schema.constraint_column_usage
                   where table_schema = 'public' and constraint_name = 'fk_etalons_relations_etalon_id_to') then
        raise notice 'Constraint fk_etalons_relations_etalon_id_to does not exist. Creating.';
        alter table public.etalons_relations add constraint fk_etalons_relations_etalon_id_to foreign key (etalon_id_to) references public.etalons (id) on update no action on delete cascade;
    else
        raise notice 'Constraint fk_etalons_relations_etalon_id_to already exists. Skept.';
    end if;

    -- Add constraints to origins relations
    if not exists (select true from information_schema.constraint_column_usage
                   where table_schema = 'public' and constraint_name = 'fk_origin_relations_origin_id_from') then
        raise notice 'Constraint fk_origin_relations_origin_id_from does not exist. Creating.';
        alter table public.origins_relations add constraint fk_origin_relations_origin_id_from foreign key (origin_id_from) references public.origins (id) on update no action on delete cascade;
    else
        raise notice 'Constraint fk_origin_relations_origin_id_from already exists. Skept.';
    end if;

    if not exists (select true from information_schema.constraint_column_usage
                   where table_schema = 'public' and constraint_name = 'fk_origin_relations_origin_id_to') then
        raise notice 'Constraint fk_origin_relations_origin_id_from does not exist. Creating.';
        alter table public.origins_relations add constraint fk_origin_relations_origin_id_to foreign key (origin_id_to) references public.origins (id) on update no action on delete cascade;
    else
        raise notice 'Constraint fk_origin_relations_origin_id_to already exists. Skept.';
    end if;

    /*
    reindex index ix_uq_etalons_relations_name_etalon_id_from_etalon_id_to;
    reindex index ix_uq_origins_relations_name_origin_id_from_origin_id_to;
    */
    -- 2. Add operation_id fields (9.4 code)
    if not exists (select column_name from information_schema.columns
                   where table_schema = 'public' and table_name = 'etalons' and column_name = 'operation_id') then
        raise notice 'Column public.etalons.operation_id does not exist. Creating.';
        alter table etalons add column operation_id text not null default '-1'::text;
    else
        raise notice 'Column public.etalons.operation_id alredy exists. Skept.';
    end if;

    if not exists (select column_name from information_schema.columns
                   where table_schema = 'public' and table_name = 'etalons_relations' and column_name = 'operation_id') then
        raise notice 'Column public.etalons_relations.operation_id does not exist. Creating.';
        alter table etalons_relations add column operation_id text not null default '-1'::text;
    else
        raise notice 'Column public.etalons_relations.operation_id alredy exists. Skept.';
    end if;

    if not exists (select column_name from information_schema.columns
                   where table_schema = 'public' and table_name = 'etalons_classifiers' and column_name = 'operation_id') then
        raise notice 'Column public.etalons_classifiers.operation_id does not exist. Creating.';
        alter table etalons_classifiers add column operation_id text not null default '-1'::text;
    else
        raise notice 'Column public.etalons_classifiers.operation_id alredy exists. Skept.';
    end if;

    alter table origins_vistory alter column operation_id set data type text;
    alter table origins_relations_vistory alter column operation_id set data type text;
    alter table origins_classifiers_vistory alter column operation_id set data type text;

    drop index if exists ix_etalons_operation_id;
    create index ix_etalons_operation_id on etalons using btree (operation_id collate pg_catalog."default");

    drop index if exists ix_etalons_classifiers_operation_id;
    create index ix_etalons_classifiers_operation_id on etalons_classifiers using btree (operation_id collate pg_catalog."default");

    drop index if exists ix_etalons_relations_operation_id;
    create index ix_etalons_relations_operation_id on etalons_relations using btree (operation_id collate pg_catalog."default");

end
$$ language plpgsql;

-- 3. Etalons batch blocks
drop function if exists ud_calc_etalon_batch_blocks(in _start bigint, in _block integer, in _name character varying);

create or replace function ud_calc_records_etalon_batch_blocks(_start bigint, _block int, _names text[], _operation_id text, _upd_mode text)
returns table (block_num int, start_id character(36), start_gsn bigint, end_id character(36), end_gsn bigint, name text)
as $$
declare
    exec_sql text;
    cur_gsn bigint := coalesce(_start, -9223372036854775808);
    cur_block int := 0;
    cur_name text;
    block_sz int := coalesce(_block, 5000);
begin
    -- 1. Result table
    create temporary table __result (block_num int not null primary key, start_id character(36), start_gsn bigint, end_id character(36), end_gsn bigint, name text) on commit drop;
    -- 2. Exec stmt
    exec_sql := 'with _block as (select id, gsn, name from etalons where gsn >= $1';
    if (array_length(_names, 1) > 0) then
        exec_sql := exec_sql || ' and name = $5';
    end if;

    if (_operation_id is not null) then
        if (_upd_mode = 'RELATIONS') then
            exec_sql := exec_sql || ' and (exists (select true from etalons_relations e where e.etalon_id_from = etalons.id and e.operation_id = $2) or exists'
                                 || ' (select true from etalons_relations e, origins_relations o, origins_relations_vistory v where e.etalon_id_from = etalons.id and o.etalon_id = e.id and o.id = v.origin_id and v.operation_id = $2))';
        else
            exec_sql := exec_sql || ' and ((etalons.operation_id = $2 or exists (select true from origins o, origins_vistory v where o.etalon_id = etalons.id and o.id = v.origin_id and v.operation_id = $2))'
                                 || ' or (exists (select true from etalons_classifiers e where e.etalon_id_record = etalons.id and e.operation_id = $2) or exists'
                                 || ' (select true from etalons_classifiers e, origins_classifiers o, origins_classifiers_vistory v where e.etalon_id_record = etalons.id and o.etalon_id = e.id and o.id = v.origin_id and v.operation_id = $2)))';
        end if;
    end if;

    exec_sql := exec_sql || ' order by gsn limit $3),'
                         || ' _block_start as (select id, gsn, name from _block order by gsn asc limit 1), _block_end as (select id, gsn, name from _block order by gsn desc limit 1)'
                         || ' insert into __result (block_num, start_id, start_gsn, end_id, end_gsn, name)'
                         || ' select $4, _block_start.id, _block_start.gsn, _block_end.id, _block_end.gsn,';

    if (array_length(_names, 1) > 0) then
        exec_sql := exec_sql || ' _block_end.name';
    else
        exec_sql := exec_sql || ' null';
    end if;

    exec_sql := exec_sql || ' from _block_start, _block_end';

    -- RAISE NOTICE 'sql [%]', exec_sql;

    if (array_length(_names, 1) > 0) then
        foreach cur_name in array _names loop

            cur_gsn := (select min(gsn) from etalons where etalons.name = cur_name);
            while true loop
                execute exec_sql using  cur_gsn, _operation_id, block_sz, cur_block, cur_name;

                cur_gsn := (select __result.end_gsn from __result where __result.block_num = cur_block);
                if cur_gsn is null then
                    exit;
                end if;

                cur_gsn := cur_gsn + 1;
                cur_block := cur_block + 1;

            end loop;
        end loop;
    else
        while true loop
            execute exec_sql using  cur_gsn, _operation_id, block_sz, cur_block;

            cur_gsn := (select __result.end_gsn from __result where __result.block_num = cur_block);
            if cur_gsn is null then
                exit;
            end if;

            cur_gsn := cur_gsn + 1;
            cur_block := cur_block + 1;

        end loop;
    end if;
    return query select __result.block_num, __result.start_id, __result.start_gsn, __result.end_id, __result.end_gsn, __result.name from __result;

end$$ language plpgsql;