
CREATE TABLE user_logins
(
  id serial not null,
  user_name character varying NOT NULL,
  password character varying NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT users_user_name_key UNIQUE (user_name)
)
WITH (
  OIDS=FALSE
);
-- Table: user_roles

-- DROP TABLE user_roles;

CREATE TABLE user_roles
(
  id serial NOT NULL,
  role_name character varying NOT NULL,
  user_id integer,
  CONSTRAINT user_roles_pkey PRIMARY KEY (id),
  CONSTRAINT user_roles_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES user_logins (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

-- Table: user_token

-- DROP TABLE user_token;
CREATE TABLE user_token
(
  id serial not null,
  token text NOT NULL,
  created_at timestamp without time zone NOT NULL,
  last_used_at timestamp without time zone,
  user_id integer,
  CONSTRAINT security_token_pkey PRIMARY KEY (id),
  CONSTRAINT security_token_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES user_logins (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
INSERT INTO user_logins(id, user_name, password)  VALUES (1,'tomcat','$2a$10$7tSzTWIkEFD.HzMPgseefuY3SriqCHdCLPMV0Sgp7XZadidngWxVy');

INSERT INTO user_roles(role_name, user_id)  VALUES ('ROLE_ADMIN', 1);
INSERT INTO user_roles(role_name, user_id)  VALUES ('ROLE_DATA_STEWARD', 1);
INSERT INTO user_roles(role_name, user_id)  VALUES ('ROLE_ADMIN_VIEWER', 1);