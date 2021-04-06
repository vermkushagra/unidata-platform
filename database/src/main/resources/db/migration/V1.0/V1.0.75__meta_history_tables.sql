do $$
declare
  v_tab record;
  v_tgr record;
  v_col record;
  v_sql text;
  v_cols text;
  v_cols_i text;
  v_cols_u text;
  v_cols_d text;
begin
  for v_tab in select t.* from information_schema.tables t where table_schema in ('public') and table_type = 'BASE TABLE' and table_name not like '%_hist' and table_name like 'meta_%' and table_name not like 'schema_version' loop
    for v_tgr in (select * from information_schema.triggers t where t.event_object_schema = v_tab.table_schema and t.event_object_table = v_tab.table_name and (t.trigger_name like 'tgr_%_i' or t.trigger_name like 'tgr_%_u' or t.trigger_name like 'tgr_%_d')) loop
      v_sql = 'drop trigger ' || v_tgr.trigger_name || ' on ' || v_tgr.event_object_schema || '."' || v_tgr.event_object_table || '"';
      execute v_sql;
    end loop;
    if not exists (select null from information_schema.sequences where sequence_schema = v_tab.table_schema and sequence_name = v_tab.table_name || '_revision_seq') then
      v_sql := 'create sequence ' || v_tab.table_schema || '."' || v_tab.table_name || '_revision_seq" INCREMENT 1 START 1 CACHE 1';
      execute v_sql;
    end if;

    if not exists (select null from information_schema.tables where table_schema = v_tab.table_schema and table_name = v_tab.table_name || '_hist') then
      v_sql := 'create table ' || v_tab.table_schema || '."' || v_tab.table_name || '_hist"(x_op text, x_timestamp timestamp with time zone, x_revision int';
      for v_col in select * from information_schema.columns where table_schema = v_tab.table_schema and table_name = v_tab.table_name order by ordinal_position loop
        v_sql := v_sql || ', "' || v_col.column_name || '_old" ' || v_col.data_type;
        v_sql := v_sql || ', "' || v_col.column_name || '_new" ' || v_col.data_type;
      end loop;
      v_sql := v_sql || ')';
      --raise notice '%', v_sql;
      execute v_sql;
    end if;
    v_cols := '';
    v_cols_i := '';
    v_cols_u := '';
    v_cols_d := '';
    for v_col in select * from information_schema.columns where table_schema = v_tab.table_schema and table_name = v_tab.table_name order by ordinal_position loop
      if not exists (select * from information_schema.columns where table_schema = v_tab.table_schema and table_name = v_tab.table_name || '_hist' and column_name = v_col.column_name || '_old') then
        v_sql = 'alter table ' || v_tab.table_schema || '."' || v_tab.table_name || '_hist" add "' || v_col.column_name || '_old" ' || v_col.data_type;
        raise notice '%', v_sql;
        execute v_sql;
      end if;
      if not exists (select * from information_schema.columns where table_schema = v_tab.table_schema and table_name = v_tab.table_name || '_hist' and column_name = v_col.column_name || '_new') then
        v_sql = 'alter table ' || v_tab.table_schema || '."' || v_tab.table_name || '_hist" add "' || v_col.column_name || '_new" ' || v_col.data_type;
        raise notice '%', v_sql;
        execute v_sql;
      end if;
      v_cols := v_cols || ', "' || v_col.column_name || '_old", "' || v_col.column_name || '_new"';
      v_cols_i := v_cols_i || ', null, new."' || v_col.column_name || '"';
      v_cols_u := v_cols_u || ', old."' || v_col.column_name || '", new."' || v_col.column_name || '"';
      v_cols_d := v_cols_d || ', old."' || v_col.column_name || '", null';
    end loop;
    v_sql := '
create or replace function ' || v_tab.table_schema || '.f_tgr_' || v_tab.table_name || '_hist_i() returns trigger as $tgr$
begin
  insert into ' || v_tab.table_schema || '."' || v_tab.table_name || '_hist"(x_op, x_timestamp, x_revision' || v_cols || ') values(''I'', now(), nextval(''' || v_tab.table_schema || '."' || v_tab.table_name || '_revision_seq"'')' || v_cols_i || ');
  return null;
end
$tgr$ language plpgsql';
    --raise notice '%', v_sql;
    execute v_sql;
    --v_sql = 'drop trigger if exists tgr_' || v_tab.table_name || '_hist_i on ' || v_tab.table_schema || '."' || v_tab.table_name || '"';
    --execute v_sql;
    v_sql = 'create trigger tgr_' || v_tab.table_name || '_hist_i after insert on ' || v_tab.table_schema || '."' || v_tab.table_name || '" for each row execute procedure ' || v_tab.table_schema || '.f_tgr_' || v_tab.table_name || '_hist_i()';
    execute v_sql;
    v_sql := '
create or replace function ' || v_tab.table_schema || '.f_tgr_' || v_tab.table_name || '_hist_u() returns trigger as $tgr$
begin
  insert into ' || v_tab.table_schema || '."' || v_tab.table_name || '_hist"(x_op, x_timestamp, x_revision' || v_cols || ') values(''U'', now(), nextval(''' || v_tab.table_schema || '."' || v_tab.table_name || '_revision_seq"'')' || v_cols_u || ');
  return null;
end
$tgr$ language plpgsql';
    --raise notice '%', v_sql;
    execute v_sql;
    --v_sql = 'drop trigger if exists tgr_' || v_tab.table_name || '_hist_u on ' || v_tab.table_schema || '."' || v_tab.table_name || '"';
    --execute v_sql;
    v_sql = 'create trigger tgr_' || v_tab.table_name || '_hist_u after update on ' || v_tab.table_schema || '."' || v_tab.table_name || '" for each row execute procedure ' || v_tab.table_schema || '.f_tgr_' || v_tab.table_name || '_hist_u()';
    execute v_sql;
    v_sql := '
create or replace function ' || v_tab.table_schema || '.f_tgr_' || v_tab.table_name || '_hist_d() returns trigger as $tgr$
begin
  insert into ' || v_tab.table_schema || '."' || v_tab.table_name || '_hist"(x_op, x_timestamp, x_revision' || v_cols || ') values(''D'', now(), nextval(''' || v_tab.table_schema || '."' || v_tab.table_name || '_revision_seq"'')' || v_cols_d || ');
  return null;
end
$tgr$ language plpgsql';
    --raise notice '%', v_sql;
    execute v_sql;
    --v_sql = 'drop trigger if exists tgr_' || v_tab.table_name || '_hist_d on ' || v_tab.table_schema || '."' || v_tab.table_name || '"';
    --execute v_sql;
    v_sql = 'create trigger tgr_' || v_tab.table_name || '_hist_d after delete on ' || v_tab.table_schema || '."' || v_tab.table_name || '" for each row execute procedure ' || v_tab.table_schema || '.f_tgr_' || v_tab.table_name || '_hist_d()';
    execute v_sql;
  end loop;

end $$;
