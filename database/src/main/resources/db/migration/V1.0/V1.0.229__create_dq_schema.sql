--create schema
DROP SCHEMA IF EXISTS unidata_dq CASCADE;
CREATE SCHEMA unidata_dq;
SET SCHEMA 'unidata_dq';
--create data types
CREATE TYPE DQ_STATUS AS ENUM ('NEW', 'RESOLVED');
CREATE TYPE DQ_SEVERITY AS ENUM ('CRITICAL','HIGH','NORMAL','LOW');
--create tables
CREATE TABLE dq_results
(
  id bigserial NOT NULL,
  request_id character varying(256) COLLATE pg_catalog."POSIX" NOT NULL,
  record_id character varying(256) COLLATE pg_catalog."POSIX" NOT NULL,
  entity_name character varying(256) COLLATE pg_catalog."POSIX" NOT NULL,
  rule_name character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  severity DQ_SEVERITY NOT NULL,
  status DQ_STATUS NOT NULL,
  category character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  message character varying(2044) COLLATE pg_catalog."POSIX",
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  created_by character varying(256) COLLATE pg_catalog."POSIX" NOT NULL,
  updated_at timestamp with time zone DEFAULT now(),
  updated_by character varying(256) COLLATE pg_catalog."POSIX",
  CONSTRAINT dq_results_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
--create hash function
create OR REPLACE function h_int(text) returns int as $$
 select ('x'||substr(md5($1),1,8))::bit(32)::int;
$$ language sql;
--create partitioning function
CREATE OR REPLACE FUNCTION create_partition_and_insert() RETURNS trigger AS
  $BODY$
    DECLARE
      partition_n int;
      partition TEXT;
    BEGIN
      partition_n := abs(h_int(NEW.record_id)%100)::int;
      partition := TG_RELNAME || '_' || partition_n;
      IF NOT EXISTS(SELECT relname FROM pg_class WHERE relname=partition) THEN
        RAISE NOTICE 'A partition has been created %',partition;
        EXECUTE 'CREATE TABLE ' || partition || ' (check (abs(h_int(record_id)%100)::int = ' || partition_n || ')) INHERITS (' || TG_RELNAME || ');';
        EXECUTE 'CREATE INDEX idx_dq_results_request_id' || partition_n || ' ON ' || partition || ' USING HASH  (request_id COLLATE pg_catalog."POSIX");';
	EXECUTE 'CREATE INDEX idx_dq_results_record_id' || partition_n || ' ON ' || partition || ' USING HASH  (record_id COLLATE pg_catalog."POSIX");';

      END IF;
      EXECUTE 'INSERT INTO ' || partition || ' SELECT(' || TG_RELNAME || ' ' || quote_literal(NEW) || ').* RETURNING record_id;';
      RETURN NULL;
    END;
  $BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
--create trigger
CREATE TRIGGER dq_results_insert_trigger
BEFORE INSERT ON dq_results
FOR EACH ROW EXECUTE PROCEDURE create_partition_and_insert();
--create stat view(for testing and maintenance)
CREATE OR REPLACE VIEW show_dq_partitions AS
SELECT nmsp_parent.nspname AS parent_schema,
       parent.relname AS parent,
       nmsp_child.nspname AS child_schema,
       child.relname AS child,
       ps.n_live_tup as records
FROM pg_inherits 
JOIN pg_class parent ON pg_inherits.inhparent = parent.oid
JOIN pg_class child ON pg_inherits.inhrelid = child.oid
JOIN pg_namespace nmsp_parent ON nmsp_parent.oid = parent.relnamespace
JOIN pg_namespace nmsp_child ON nmsp_child.oid = child.relnamespace
JOIN pg_stat_user_tables ps ON child.relname = ps.relname
WHERE parent.relname='dq_results'
ORDER BY child;