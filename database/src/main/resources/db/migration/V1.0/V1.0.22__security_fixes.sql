truncate s_user cascade;
truncate s_role cascade;
truncate s_right cascade;
truncate user_logins cascade;
insert into user_logins(user_name,password) values('admin','$2a$10$iM8g/RVRpYQq60GTvqjN8.8Ayo0FEJ0PbBb60uRYrAcQ55NGmlGkK');
INSERT INTO s_resource( id, name, r_type, created_at, updated_at, created_by, updated_by, display_name ) VALUES ( 4999, 'ADMIN_SYSTEM_MANAGEMENT', 'SYSTEM', '2015-08-26 14:50:43.181+03', null, null, null, 'Администратор системы' ); 
INSERT INTO s_resource( id, name, r_type, created_at, updated_at, created_by, updated_by, display_name ) VALUES ( 5999, 'ADMIN_DATA_MANAGEMENT', 'SYSTEM', '2015-08-26 14:50:43.191+03', null, null, null, 'Администратор данных' ); 

INSERT INTO s_right( id, name, description, created_at, updated_at, created_by, updated_by ) VALUES ( 5999, 'CREATE', null, '2015-08-26 14:50:43.201+03', null, null, null ); 
INSERT INTO s_right( id, name, description, created_at, updated_at, created_by, updated_by ) VALUES ( 6999, 'UPDATE', null, '2015-08-26 14:50:43.201+03', null, null, null ); 
INSERT INTO s_right( id, name, description, created_at, updated_at, created_by, updated_by ) VALUES ( 7999, 'DELETE', null, '2015-08-26 14:50:43.201+03', null, null, null ); 
INSERT INTO s_right( id, name, description, created_at, updated_at, created_by, updated_by ) VALUES ( 8999, 'READ', null, '2015-08-26 14:50:43.201+03', null, null, null ); 

INSERT INTO s_role( id, name, r_type, display_name, description, created_at, updated_at, created_by, updated_by ) VALUES ( 117999, 'ADMIN', 'USER_DEFINED', 'Администратор', null, null, null, null, null ); 

INSERT INTO s_user( id, login, email, first_name, last_name, notes, auth_type, created_at, updated_at, created_by, updated_by, active ) VALUES ( 48999, 'admin', 'mail@example.com', '', '', null, null, '2015-08-27 10:47:44.734+03', null, null, null, true ); 

INSERT INTO s_user_s_role( id, s_users_id, s_roles_id, created_at, updated_at, created_by, updated_by ) VALUES ( 59999, 48999, 117999, '2015-08-27 11:23:27.944+03', null, null, null ); 

INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) VALUES ( 346999, 4999, 7999, null, null, null, null, 117999 ); 
INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) VALUES ( 348999, 4999, 8999, null, null, null, null, 117999 ); 
INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) VALUES ( 345999, 4999, 5999, null, null, null, null, 117999 ); 
INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) VALUES ( 351999, 5999, 6999, null, null, null, null, 117999 ); 
INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) VALUES ( 347999, 4999, 6999, null, null, null, null, 117999 ); 
INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) VALUES ( 349999, 5999, 5999, null, null, null, null, 117999 ); 
INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) VALUES ( 352999, 5999, 8999, null, null, null, null, 117999 ); 
INSERT INTO s_right_s_resource( id, s_resource_id, s_right_id, created_at, updated_at, created_by, updated_by, s_role_id ) VALUES ( 350999, 5999, 7999, null, null, null, null, 117999 ); 