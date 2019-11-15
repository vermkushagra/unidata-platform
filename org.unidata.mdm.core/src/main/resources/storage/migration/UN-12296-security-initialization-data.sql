-- create parent resource
INSERT INTO s_resource (name, display_name, r_type, parent_id, category)
VALUES ('ADMIN_SYSTEM_MANAGEMENT', 'Администрирование системы', 'SYSTEM', null, 'SYSTEM'),
       ('BULK_OPERATIONS_OPERATOR', 'Отключить лимит на пакетные операции с записями', 'SYSTEM', null, 'SYSTEM'),
       ('ADMIN_DATA_MANAGEMENT', 'Администрирование модели данных', 'SYSTEM', null, 'SYSTEM'),
       ('ADMIN_MATCHING_MANAGEMENT', 'Настройка правил поиска дубликатов', 'SYSTEM', null, 'SYSTEM'),
       ('ADMIN_CLASSIFIER_MANAGEMENT', 'Настройка классификаторов', 'SYSTEM', null, 'SYSTEM');
-- create child resource
insert into s_resource (name, display_name, r_type, parent_id, category)
VALUES ('EXECUTE_DATA_OPERATIONS', 'Запуск операций', 'SYSTEM',
        (select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'), 'SYSTEM'),
       ('SECURITY_LABELS_MANAGEMENT', 'Метки безопасности', 'SYSTEM',
        (select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'), 'SYSTEM'),
       ('AUDIT_ACCESS', 'Журнал', 'SYSTEM', (select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'),
        'SYSTEM'),
       ('PLATFORM_PARAMETERS_MANAGEMENT', 'Параметры платформы', 'SYSTEM',
        (select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'), 'SYSTEM'),
       ('USER_MANAGEMENT', 'Пользователи', 'SYSTEM', (select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'),
        'SYSTEM'),
       ('DATA_OPERATIONS_MANAGEMENT', 'Настройка операций', 'SYSTEM',
        (select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'), 'SYSTEM'),
       ('DATA_HISTORY_EDITOR', 'История записи', 'SYSTEM',
        (select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'), 'SYSTEM')
    ;
-- create rights
insert into s_right (name, created_at)
VALUES ('CREATE', NOW()),
    ('UPDATE', NOW()),
    ('DELETE', NOW()),
    ('READ', NOW());

-- APIs
insert into s_apis(name, display_name, description, created_at, updated_at, created_by, updated_by)
values ('REST', 'WEB интерфейс', 'WEB интерфейс', NOW(), NOW(), 'SYSTEM', 'SYSTEM');
insert into s_apis(name, display_name, description, created_at, updated_at, created_by, updated_by)
values ('SOAP', 'SOAP интерфейс', 'SOAP интерфейс', NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- create admin role
insert into s_role(name, r_type, description)
VALUES ('ADMIN', 'USER_DEFINED', 'Админ');

INSERT INTO s_right_s_resource (s_resource_id, s_role_id, s_right_id)
VALUES ((select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'READ')),
       ((select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'CREATE')),
       ((select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'UPDATE')),
       ((select id from s_resource where name = 'ADMIN_SYSTEM_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'DELETE')),
       ((select id from s_resource where name = 'ADMIN_MATCHING_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'READ')),
       ((select id from s_resource where name = 'ADMIN_MATCHING_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'CREATE')),
       ((select id from s_resource where name = 'ADMIN_MATCHING_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'UPDATE')),
       ((select id from s_resource where name = 'ADMIN_MATCHING_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'DELETE')),
       ((select id from s_resource where name = 'ADMIN_DATA_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'READ')),
       ((select id from s_resource where name = 'ADMIN_DATA_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'CREATE')),
       ((select id from s_resource where name = 'ADMIN_DATA_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'UPDATE')),
       ((select id from s_resource where name = 'ADMIN_DATA_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'DELETE')),
       ((select id from s_resource where name = 'ADMIN_CLASSIFIER_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'READ')),
       ((select id from s_resource where name = 'ADMIN_CLASSIFIER_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'CREATE')),
       ((select id from s_resource where name = 'ADMIN_CLASSIFIER_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'UPDATE')),
       ((select id from s_resource where name = 'ADMIN_CLASSIFIER_MANAGEMENT'),
        (select id from s_role where name = 'ADMIN'),
        (select id from s_right where name = 'DELETE'));
-- create admin user
insert into s_user(login, email, first_name, last_name, active, admin)
VALUES ('admin', 'mail@example.com', '', '', true, true);
-- set admin password to 'admin'
insert into s_password (s_user_id, password_text, active)
VALUES ((select id from s_user where login = 'admin'),
        '$2a$10$ugm0ifSpkd4zW6SuwLiRSuGrMHoqf8BtqagrL5oIS44Fnp8EpLzJW', true);
-- set 'admin' role as ADMIN
insert into s_user_s_role(s_users_id, s_roles_id)
VALUES ((select id from s_user where login = 'admin'), (select id from s_role where name = 'ADMIN'));
-- set SOAP and REST endpoints for 'admin' user
insert into s_user_s_apis(s_user_id, s_api_id, created_by)
values (
    (select id from s_user where login = 'admin'),
    (select id from s_apis where name = 'REST'),
    'SYSTEM'
);
insert into s_user_s_apis(s_user_id, s_api_id, created_by)
values (
    (select id from s_user where login = 'admin'),
    (select id from s_apis where name = 'SOAP'),
    'SYSTEM'
);
