CREATE TABLE s_resource ( 
	id                   serial  NOT NULL,
	name                 varchar(100)  NOT NULL,
	display_name         varchar(100)  NOT NULL,
	r_type               varchar(15)  ,
	created_at           timestamptz DEFAULT now() ,
	updated_at           timestamptz  ,
	created_by           varchar(255)  ,
	updated_by           varchar(255)  ,
	CONSTRAINT pk_s_secured_resource PRIMARY KEY ( id )
 );

CREATE TABLE s_right ( 
	id                   serial  NOT NULL,
	name                 varchar(255)  ,
	description          text  ,
	created_at           timestamptz DEFAULT now() ,
	updated_at           timestamptz  ,
	created_by           varchar(255)  ,
	updated_by           varchar(255)  ,
	CONSTRAINT pk_s_role_right PRIMARY KEY ( id )
 );

CREATE TABLE s_role ( 
	id                   serial  NOT NULL,
	name                 varchar(255)  NOT NULL,
	r_type               varchar(255)  ,
	display_name         varchar(255)  ,
	description          text  ,
	created_at           timestamptz DEFAULT now() ,
	updated_at           timestamptz  ,
	created_by           varchar(255)  ,
	updated_by           varchar(255)  ,
	CONSTRAINT pk_s_roles PRIMARY KEY ( id ),
	CONSTRAINT idx_s_role UNIQUE ( name ) 
 );

CREATE TABLE s_user ( 
	id                   serial  NOT NULL,
	login                varchar(255)  NOT NULL,
	email                varchar(255)  NOT NULL,
	first_name           varchar(255)  ,
	last_name            varchar(255)  ,
	notes                varchar(600)  ,
	auth_type            varchar(10) DEFAULT 'UNIDATA'::character varying ,
	created_at           timestamptz DEFAULT now() ,
	updated_at           timestamptz  ,
	created_by           varchar(255)  ,
	updated_by           varchar(255)  ,
	active               bool DEFAULT true ,
	CONSTRAINT pk_s_user PRIMARY KEY ( id )
 );

CREATE TABLE s_user_s_role ( 
	id                   serial  NOT NULL,
	s_users_id           integer  NOT NULL,
	s_roles_id           integer  NOT NULL,
	created_at           timestamptz DEFAULT now() ,
	updated_at           timestamptz  ,
	created_by           varchar(255)  ,
	updated_by           varchar(255)  ,
	CONSTRAINT pk_s_users_s_roles PRIMARY KEY ( id ),
	CONSTRAINT idx_s_users_s_roles3 UNIQUE ( s_users_id, s_roles_id ) 
 );

CREATE INDEX idx_s_users_s_roles1 ON s_user_s_role ( s_roles_id );

CREATE INDEX idx_s_users_s_roles2 ON s_user_s_role ( s_users_id );

CREATE TABLE s_password ( 
	id                   serial  NOT NULL,
	created_at           timestamptz DEFAULT now() ,
	updated_at           timestamptz  ,
	created_by           varchar(255)  ,
	updated_by           varchar(255)  ,
	password_text        varchar(255)  NOT NULL,
	s_user_id            integer  NOT NULL,
	CONSTRAINT pk_s_password PRIMARY KEY ( id ),
	CONSTRAINT idx_s_password UNIQUE ( s_user_id ) 
 );

CREATE TABLE s_right_s_resource ( 
	id                   serial  NOT NULL,
	s_resource_id        integer  ,
	s_right_id           integer  ,
	created_at           timestamptz DEFAULT now() ,
	updated_at           timestamptz  ,
	created_by           varchar(255)  ,
	updated_by           varchar(255)  ,
	s_role_id            integer  ,
	CONSTRAINT pk_s_right_s_resource PRIMARY KEY ( id ),
	CONSTRAINT idx_s_right_s_resource UNIQUE ( s_right_id, s_resource_id, s_role_id ) 
 );

CREATE INDEX idx_s_right_s_resource_0 ON s_right_s_resource ( s_right_id );

CREATE INDEX idx_s_right_s_resource_1 ON s_right_s_resource ( s_resource_id );

CREATE INDEX idx_s_right_s_resource_2 ON s_right_s_resource ( s_role_id );

CREATE TABLE s_token ( 
	id                   serial  NOT NULL,
	token                varchar(255)  ,
	created_at           timestamptz DEFAULT now() ,
	updated_at           timestamptz  ,
	created_by           varchar(255)  ,
	updated_by           varchar(255)  ,
	s_user_id            integer  NOT NULL,
	CONSTRAINT pk_s_token PRIMARY KEY ( id )
 );

CREATE INDEX idx_s_token ON s_token ( s_user_id );

ALTER TABLE s_password ADD CONSTRAINT fk_s_password FOREIGN KEY ( s_user_id ) REFERENCES s_user( id );

ALTER TABLE s_right_s_resource ADD CONSTRAINT fk_s_right_s_resource_0 FOREIGN KEY ( s_resource_id ) REFERENCES s_resource( id );

ALTER TABLE s_right_s_resource ADD CONSTRAINT fk_s_right_s_resource FOREIGN KEY ( s_right_id ) REFERENCES s_right( id );

ALTER TABLE s_right_s_resource ADD CONSTRAINT fk_s_right_s_resource_1 FOREIGN KEY ( s_role_id ) REFERENCES s_role( id );

ALTER TABLE s_token ADD CONSTRAINT fk_s_token FOREIGN KEY ( s_user_id ) REFERENCES s_user( id );

ALTER TABLE s_user_s_role ADD CONSTRAINT fk_s_users_s_roles_s_roles FOREIGN KEY ( s_roles_id ) REFERENCES s_role( id );

ALTER TABLE s_user_s_role ADD CONSTRAINT fk_s_users_s_roles_s_user FOREIGN KEY ( s_users_id ) REFERENCES s_user( id );
insert into s_resource(name,display_name, r_type) values ('ADMIN_SYSTEM_MANAGEMENT','АДМИНИСТРАТОР_СИСТЕМЫ','SYSTEM');
insert into s_resource(name,display_name, r_type)  values ('ADMIN_DATA_MANAGEMENT','АДМИНИСТРАТОР_ДАННЫХ','SYSTEM');
insert into s_resource(name,display_name, r_type)  values ('Licensee','Держатель Лицензии','USER_DEFINED');
insert into s_resource(name,display_name, r_type)  values ('LicenseProvider','Лицензирующий Орган','USER_DEFINED');

insert into s_right(name) values('CREATE');
insert into s_right(name) values('UPDATE');
insert into s_right(name) values('DELETE');
insert into s_right(name) values('READ');
