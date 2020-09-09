-- Table: statistic_counters

-- DROP TABLE statistic_counters;

CREATE TABLE statistic_counters
(
  type character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  at_date timestamp with time zone NOT NULL,
  count integer NOT NULL,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone NOT NULL,
  created_by character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  updated_by character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  id serial NOT NULL,
  CONSTRAINT statistic_counters_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

-- Index: index_at_date

-- DROP INDEX index_at_date;

CREATE INDEX index_at_date
  ON statistic_counters
  USING btree
  (at_date);

-- Index: index_type

-- DROP INDEX index_type;

CREATE INDEX index_type
  ON statistic_counters
  USING btree
  (type COLLATE pg_catalog."POSIX");

