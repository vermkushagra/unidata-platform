delete from s_right_s_resource where s_resource_id in (1,2,3,4);
delete from s_resource where id = 1 and name = 'ADMIN_SYSTEM_MANAGEMENT';
delete from s_resource where id = 2 and name = 'ADMIN_DATA_MANAGEMENT';
delete from s_resource where id = 3 and name = 'Licensee';
delete from s_resource where id = 4 and name = 'LicenseProvider';