-- Functions
-----------------------------------------------------------------------------------
-- drop function
drop function if exists ud_move_from_import_tables( _t_name_in text, _t_name_out text, _key_field text, _copy_fields text, _t_start integer, _t_limit integer, _schema_field_in text, _schema_field_out text);
-----------------------------------------------------------------------------------
-- fn
create or replace function ud_move_from_import_tables (
    _t_name_in text,
    _t_name_out text,
    _key_field text,
    _copy_fields text,
    _t_start integer default 0,
    _t_limit integer default 100000,
    _schema_field_in text default 'public'::text,
    _schema_field_out text default 'public'::text)
  returns integer as
$$
declare
    cur_id bigint;
    exe text;
    t_in text := quote_ident(_schema_field_in) || '.' || quote_ident(_t_name_in);
    t_out text := quote_ident(_schema_field_out) || '.' || quote_ident(_t_name_out);
begin

    --while true loop

      --lock t12 in EXCLUSIVE mode;
      exe :=
      'with
      t1 as (delete from ' || t_in || ' where ' || quote_ident(_key_field) || ' in (select ' ||
      quote_ident(_key_field) || ' from ' || t_in || ' where ' || quote_ident(_key_field) || ' >= $1 and ' || quote_ident(_key_field) || ' < ($1 + $2) ' || ' order by ' || quote_ident(_key_field) || ') returning *),
      t2 as (insert into ' || t_out || ' (' || _copy_fields || ') select ' || _copy_fields || ' from t1 returning *)
      select * from t2 limit 1';
      --RAISE EXCEPTION '%', exe;
      execute exe using _t_start, _t_limit;

      exe := 'select ' || quote_ident(_key_field) || ' from ' || t_in || ' limit 1';
      execute exe into cur_id ;
      if cur_id is null then
      --    exit;
        return 0;
      end if;

    --end loop;
    return 1;

end
$$ language plpgsql;
-----------------------------------------------------------------------------------
