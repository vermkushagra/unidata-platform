CREATE TABLE "public"."s_user_property" ( 
    "id" serial  NOT NULL,
    "name" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
    "display_name" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
    "created_at" Timestamp With Time Zone NOT NULL,
    "updated_at" Timestamp With Time Zone,
    "created_by" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
    "updated_by" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX",
    PRIMARY KEY ( "id" ),
    CONSTRAINT "unique_name" UNIQUE( "name" ),
    CONSTRAINT "unique_id" UNIQUE( "id" ) );
 
CREATE INDEX "index_name" ON "public"."s_user_property" USING btree( "name" ASC NULLS LAST );


CREATE INDEX "index_id" ON "public"."s_user_property" USING btree( "id" ASC NULLS LAST );


CREATE TABLE "public"."s_user_property_value" ( 
    "id" serial NOT NULL,
    "user_id" Integer NOT NULL,
    "property_id" Integer NOT NULL,
    "value" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
    "created_at" Timestamp With Time Zone NOT NULL,
    "created_by" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
    "updated_at" Time With Time Zone,
    "updated_by" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX",
    PRIMARY KEY ( "id" ) );
 
CREATE INDEX "s_user_property_value_user_id_idx" ON "public"."s_user_property_value" USING btree( "user_id" ASC NULLS LAST );


CREATE INDEX "s_user_property_value_property_id_idx" ON "public"."s_user_property_value" USING btree( "property_id" ASC NULLS LAST );
CREATE TABLE "public"."auth_audit" ( 
    "id" serial NOT NULL,
    "action" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
    "client_ip" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX",
    "server_ip" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX",
    "created_at" Timestamp With Time Zone NOT NULL,
    "created_by" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX" NOT NULL,
    "updated_at" Timestamp With Time Zone,
    "updated_by" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX",
    "description" Character Varying( 2044 ) COLLATE "pg_catalog"."POSIX",
    PRIMARY KEY ( "id" ) );
 insert into s_user_property(name, display_name, created_at, created_by) values('department', 'отдел', current_timestamp, 'admin');
 insert into s_user_property(name, display_name, created_at, created_by) values('city', 'город', current_timestamp, 'admin');
 insert into s_user_property(name, display_name, created_at, created_by) values('phone', 'телефон', current_timestamp, 'admin');
