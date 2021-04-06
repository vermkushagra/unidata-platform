delete from s_user_property_value where user_id not in (select id from s_user);
delete from s_user_property_value where property_id not in (select id from s_user_property);

-- Add FK constraints for UserPropertyValue
ALTER TABLE s_user_property_value ADD CONSTRAINT fk_s_user_property_value_user_id FOREIGN KEY (user_id)
  REFERENCES s_user (id) MATCH FULL ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE s_user_property_value ADD CONSTRAINT fk_s_user_property_value_property_id FOREIGN KEY (property_id)
  REFERENCES s_user_property (id) MATCH FULL ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Create Role Property Value
CREATE TABLE s_role_property (
  id            serial UNIQUE NOT NULL,
  name          character varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
  display_name  character varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
  created_at    timestamp with time zone NOT NULL,
  updated_at    timestamp with time zone,
  created_by    character varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
  updated_by    character varying( 2044 ) COLLATE "pg_catalog"."POSIX",
  PRIMARY KEY ( "id" ),
  CONSTRAINT ix_s_role_property_unique_name UNIQUE( "name" )
);

CREATE INDEX ix_s_role_property_name ON s_role_property USING btree( "name" ASC NULLS LAST );
CREATE INDEX ix_s_role_property_id ON s_role_property USING btree( "id" ASC NULLS LAST );

CREATE TABLE s_role_property_value (
  id            serial NOT NULL,
  role_id       integer NOT NULL,
  property_id   integer NOT NULL,
  value         character varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
  created_at    timestamp with time zone NOT NULL,
  created_by    character varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
  updated_at    time with time zone,
  updated_by    character varying( 2044 ) COLLATE "pg_catalog"."POSIX",
  PRIMARY KEY ( "id" ),
  CONSTRAINT fk_s_role_property_value_role_id FOREIGN KEY (role_id) REFERENCES s_role (id),
  CONSTRAINT fk_s_role_property_value_property_id FOREIGN KEY (property_id) REFERENCES s_role_property (id)

);

CREATE INDEX ix_s_role_property_value_user_id ON s_role_property_value USING btree( role_id ASC NULLS LAST );
CREATE INDEX ix_s_role_property_value_property_id ON s_role_property_value USING btree( property_id ASC NULLS LAST );
