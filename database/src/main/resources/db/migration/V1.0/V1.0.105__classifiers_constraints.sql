DROP TABLE IF EXISTS clsf CASCADE;
DROP TABLE IF EXISTS classifiers CASCADE;
DROP TABLE IF EXISTS classifier_nodes CASCADE;
DROP TABLE IF EXISTS clsf_node CASCADE;
DROP TABLE IF EXISTS clsf_node_attr CASCADE;
-- Table: clsf
CREATE TABLE clsf
(
  id serial NOT NULL,
  name character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  display_name character varying(2044) COLLATE pg_catalog."POSIX",
  code_pattern character varying(2044) COLLATE pg_catalog."POSIX",
  description text,
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone,
  created_by character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  updated_by character varying(2044) COLLATE pg_catalog."POSIX",
  CONSTRAINT clsf_unique_id PRIMARY KEY (id),
  CONSTRAINT clsf_unique_name UNIQUE (name)
)
WITH (
  OIDS=FALSE
);

-- Index: clsf_index_id

-- DROP INDEX clsf_index_id;

CREATE INDEX clsf_index_id
  ON clsf
  USING btree
  (id);

-- Index: clsf_index_name

-- DROP INDEX clsf_index_name;

CREATE INDEX clsf_index_name
  ON clsf
  USING btree
  (name COLLATE pg_catalog."POSIX");

-- Table: clsf_node

-- DROP TABLE clsf_node;

CREATE TABLE clsf_node
(
  id serial NOT NULL,
  clsf_id integer NOT NULL,
  code character varying(2044) COLLATE pg_catalog."POSIX",
  name character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  description character varying(2044) COLLATE pg_catalog."POSIX",
  node_id character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  parent_node_id character varying(2044) COLLATE pg_catalog."POSIX",
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone,
  created_by character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  updated_by character varying(2044) COLLATE pg_catalog."POSIX",
  CONSTRAINT clsf_node_unique_id PRIMARY KEY (id),
  CONSTRAINT fk_clsf_node_clsf FOREIGN KEY (clsf_id)
      REFERENCES clsf (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_clsf_node_clsf_node FOREIGN KEY (parent_node_id)
      REFERENCES clsf_node (node_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT clsf_node_unique_node_id UNIQUE (node_id)
)
WITH (
  OIDS=FALSE
);

-- Index: clsf_index_node_id

-- DROP INDEX clsf_index_node_id;

CREATE INDEX clsf_index_node_id
  ON clsf_node
  USING btree
  (node_id COLLATE pg_catalog."POSIX");

-- Index: clsf_index_parent_id

-- DROP INDEX clsf_index_parent_id;

CREATE INDEX clsf_index_parent_id
  ON clsf_node
  USING btree
  (parent_node_id COLLATE pg_catalog."POSIX");

-- Index: clsf_node_index_clsf_id

-- DROP INDEX clsf_node_index_clsf_id;

CREATE INDEX clsf_node_index_clsf_id
  ON clsf_node
  USING btree
  (clsf_id);

-- Index: clsf_node_index_id

-- DROP INDEX clsf_node_index_id;

CREATE INDEX clsf_node_index_id
  ON clsf_node
  USING btree
  (id);

-- Table: clsf_node_attr

-- DROP TABLE clsf_node_attr;

CREATE TABLE clsf_node_attr
(
  id serial NOT NULL,
  clsf_node_id integer NOT NULL,
  attr_name character varying(256) COLLATE pg_catalog."POSIX" NOT NULL,
  display_name character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  description character varying(10485) COLLATE pg_catalog."POSIX",
  data_type character varying(256) COLLATE pg_catalog."POSIX" NOT NULL,
  is_read_only boolean DEFAULT false,
  is_hidden boolean DEFAULT false,
  is_nullable boolean DEFAULT false,
  is_unique boolean DEFAULT false,
  is_searchable boolean DEFAULT true,
  default_value character varying(2044) COLLATE pg_catalog."POSIX",
  created_at timestamp with time zone NOT NULL,
  updated_at timestamp with time zone,
  created_by character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  updated_by character varying(2044) COLLATE pg_catalog."POSIX",
  CONSTRAINT clsf_node_attr_unique_id PRIMARY KEY (id),
  CONSTRAINT fk_clsf_node_attr_clsf_node FOREIGN KEY (clsf_node_id)
      REFERENCES clsf_node (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT clsf_node_attr_unique_node_id_name UNIQUE (clsf_node_id, attr_name)
)
WITH (
  OIDS=FALSE
);

-- Index: clsf_node_attr_index_attr_name

-- DROP INDEX clsf_node_attr_index_attr_name;

CREATE INDEX clsf_node_attr_index_attr_name
  ON clsf_node_attr
  USING btree
  (attr_name COLLATE pg_catalog."POSIX");

-- Index: clsf_node_attr_index_node_id

-- DROP INDEX clsf_node_attr_index_node_id;

CREATE INDEX clsf_node_attr_index_node_id
  ON clsf_node_attr
  USING btree
  (clsf_node_id);

-- Index: node_attr_index_id

-- DROP INDEX node_attr_index_id;

CREATE INDEX node_attr_index_id
  ON clsf_node_attr
  USING btree
  (id);

