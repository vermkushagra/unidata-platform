DROP TABLE s_ext_token;
DROP TABLE s_ext_user;

ALTER TABLE s_user ADD COLUMN source VARCHAR(255) DEFAULT 'UNIDATA', ADD COLUMN "external" boolean NOT NULL DEFAULT FALSE;
ALTER TABLE s_user ADD CONSTRAINT unique_login_source UNIQUE ("login", source);
