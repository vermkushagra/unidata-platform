truncate user_logins cascade;
truncate s_user cascade;
truncate s_role cascade;
insert into user_logins(user_name,password) values('admin','2a$08$AirepCZx9tq4u/Gn/lNYjODC3d1ytUl0Xx6ISDce34NmZFlobcA/2');
insert into s_role(name, r_type, display_name) values('ADMIN_ROLE', 'SYSTEM', 'Админ');
insert into s_user(login, email, first_name, last_name, active) values('admin', 'mail@example.com', '','', true);
insert into s_user_s_role(s_users_id, s_roles_id) values((select id from s_user where login='admin'),(select id from s_role where name='ADMIN_ROLE'));
insert into s_right_s_resource(s_resource_id, s_right_id, s_role_id) values((select id from s_resource where name='ADMIN_SYSTEM_MANAGEMENT'),(select id from s_right where name='CREATE'),((select id from s_role where name='ADMIN_ROLE')));
insert into s_right_s_resource(s_resource_id, s_right_id, s_role_id) values((select id from s_resource where name='ADMIN_SYSTEM_MANAGEMENT'),(select id from s_right where name='UPDATE'),((select id from s_role where name='ADMIN_ROLE')));
insert into s_right_s_resource(s_resource_id, s_right_id, s_role_id) values((select id from s_resource where name='ADMIN_SYSTEM_MANAGEMENT'),(select id from s_right where name='DELETE'),((select id from s_role where name='ADMIN_ROLE')));
insert into s_right_s_resource(s_resource_id, s_right_id, s_role_id) values((select id from s_resource where name='ADMIN_SYSTEM_MANAGEMENT'),(select id from s_right where name='READ'),((select id from s_role where name='ADMIN_ROLE')));

insert into s_right_s_resource(s_resource_id, s_right_id, s_role_id) values((select id from s_resource where name='ADMIN_DATA_MANAGEMENT'),(select id from s_right where name='CREATE'),((select id from s_role where name='ADMIN_ROLE')));
insert into s_right_s_resource(s_resource_id, s_right_id, s_role_id) values((select id from s_resource where name='ADMIN_DATA_MANAGEMENT'),(select id from s_right where name='UPDATE'),((select id from s_role where name='ADMIN_ROLE')));
insert into s_right_s_resource(s_resource_id, s_right_id, s_role_id) values((select id from s_resource where name='ADMIN_DATA_MANAGEMENT'),(select id from s_right where name='DELETE'),((select id from s_role where name='ADMIN_ROLE')));
insert into s_right_s_resource(s_resource_id, s_right_id, s_role_id) values((select id from s_resource where name='ADMIN_DATA_MANAGEMENT'),(select id from s_right where name='READ'),((select id from s_role where name='ADMIN_ROLE')));
