-- Table: s_label

-- DROP TABLE s_label;

CREATE TABLE s_label
(
  id serial NOT NULL,
  name character varying(255),
  display_name character varying(255),
  description text,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone,
  created_by character varying(255),
  updated_by character varying(255),
  CONSTRAINT pk_s_label PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

  -- Table: s_label_attribute

-- DROP TABLE s_label_attribute;

CREATE TABLE s_label_attribute
(
  id serial NOT NULL,
  name character varying(255),
  s_label_id integer NOT NULL,
  value character varying(1500),
  description text,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone,
  created_by character varying(255),
  updated_by character varying(255),
  path character varying(2044) COLLATE pg_catalog."POSIX",
  CONSTRAINT pk_s_label_attribute PRIMARY KEY (id),
  CONSTRAINT fk_s_label_attribute FOREIGN KEY (s_label_id)
      REFERENCES s_label (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_s_role_s_label0 FOREIGN KEY (s_label_id)
      REFERENCES s_label (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
-- Table: s_label_attribute_value

-- DROP TABLE s_label_attribute_value;

CREATE TABLE s_label_attribute_value
(
  id serial NOT NULL,
  s_user_id integer,
  s_label_attribute_id integer,
  value character varying(1500),
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone,
  created_by character varying(255),
  updated_by character varying(255),
  CONSTRAINT pk_s_label_attribute_value PRIMARY KEY (id),
  CONSTRAINT fk_s_label_attribute_value0 FOREIGN KEY (s_user_id)
      REFERENCES s_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_s_label_attribute_value1 FOREIGN KEY (s_label_attribute_id)
      REFERENCES s_label_attribute (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
-- Table: s_role_s_label

-- DROP TABLE s_role_s_label;

CREATE TABLE s_role_s_label
(
  id serial NOT NULL,
  s_role_id integer,
  s_label_id integer,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone,
  created_by character varying(255),
  updated_by character varying(255),
  CONSTRAINT pk_s_role_s_label PRIMARY KEY (id),
  CONSTRAINT fk_s_role_s_label0 FOREIGN KEY (s_label_id)
      REFERENCES s_label (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_s_role_s_label1 FOREIGN KEY (s_role_id)
      REFERENCES s_role (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
  
alter table public.s_user add column admin boolean default false;
update s_user set admin=true where login='admin';