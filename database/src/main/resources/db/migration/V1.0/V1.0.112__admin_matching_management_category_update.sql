create or replace function fix_admin_matching_management_category() returns setof void as
$$
declare
    record_exists boolean := false;
begin
    record_exists := coalesce((select distinct true from s_resource where trim(name) = 'ADMIN_MATCHING_MANAGEMENT'), false);
    
    if record_exists = false then

        raise notice 'ADMIN_MATCHING_MANAGEMENT does not exist and will be created';
        
        INSERT INTO s_resource( id, name, r_type, category, created_at, updated_at, created_by, updated_by, display_name ) 
        VALUES (7999, 'ADMIN_MATCHING_MANAGEMENT', 'SYSTEM', 'SYSTEM', current_timestamp, null, null, null, 'Администратор правил сопоставления записей');

        INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) 
        VALUES ( 363999, 7999, 6999, null, null, null, null, 117999 );
        INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) 
        VALUES ( 364999, 7999, 5999, null, null, null, null, 117999 );
        INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) 
        VALUES ( 365999, 7999, 8999, null, null, null, null, 117999 );
        INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) 
        VALUES ( 366999, 7999, 7999, null, null, null, null, 117999 );
    else
        raise notice 'ADMIN_MATCHING_MANAGEMENT does already exist and will be updated';
        
        update s_resource set category = 'SYSTEM' where trim(name) = 'ADMIN_MATCHING_MANAGEMENT';
    end if;
end;
$$ language plpgsql;

select fix_admin_matching_management_category();

drop function if exists add_admin_matching_management_if_not_exists();
drop function if exists fix_admin_matching_management_category();
