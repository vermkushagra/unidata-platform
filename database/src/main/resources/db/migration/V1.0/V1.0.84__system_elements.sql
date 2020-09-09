CREATE TABLE system_elements
(
  id serial NOT NULL,
  element_type character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  element_name character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  element_folder character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  element_description character varying(2044) COLLATE pg_catalog."POSIX",
  element_content bytea NOT NULL,
  created_at timestamp with time zone NOT NULL,
  created_by character varying(2044) COLLATE pg_catalog."POSIX" NOT NULL,
  updated_at timestamp with time zone,
  updated_by character varying(2044) COLLATE pg_catalog."POSIX",
  CONSTRAINT system_data_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);