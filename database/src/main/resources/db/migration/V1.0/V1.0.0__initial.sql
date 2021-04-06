CREATE TABLE meta_storage (
  id INTEGER NOT NULL,
  name TEXT NOT NULL,
  create_date TIMESTAMP NOT NULL DEFAULT now(),
	update_date TIMESTAMP NULL DEFAULT now(),
	created_by TEXT NOT NULL,
	updated_by TEXT NULL,
  CONSTRAINT meta_storage_pkey PRIMARY KEY (id)
);

CREATE TABLE meta_model (
  id INTEGER NOT NULL,
  storage_fkey INTEGER NOT NULL,
  name TEXT NOT NULL,
  version INTEGER NOT NULL,
  data text NOT NULL,
  create_date TIMESTAMP NOT NULL DEFAULT now(),
  update_date TIMESTAMP NULL DEFAULT now(),
  created_by TEXT NOT NULL,
  updated_by TEXT NULL,
  CONSTRAINT meta_model_pkey PRIMARY KEY (id)
);

CREATE TABLE meta_ui (
  id INTEGER NOT NULL,
  model_fkey INTEGER NOT NULL,
  name TEXT NOT NULL,
  version INTEGER NOT NULL,
  data text NOT NULL,
  create_date TIMESTAMP NOT NULL DEFAULT now(),
  update_date TIMESTAMP NULL DEFAULT now(),
  created_by TEXT NOT NULL,
  updated_by TEXT NULL,
  CONSTRAINT meta_ui_pkey PRIMARY KEY (id)
);


ALTER TABLE meta_model ADD CONSTRAINT fk_storage FOREIGN KEY (storage_fkey)
  REFERENCES meta_storage (id) MATCH FULL
  ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE meta_ui ADD CONSTRAINT fk_model FOREIGN KEY (model_fkey)
  REFERENCES meta_model (id) MATCH FULL
  ON DELETE NO ACTION ON UPDATE NO ACTION;

CREATE SEQUENCE sq_meta_storage
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 2147483647
  START 1
  CACHE 1;

CREATE SEQUENCE sq_meta_model
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 2147483647
  START 1
  CACHE 1;

CREATE SEQUENCE sq_meta_ui
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 2147483647
  START 1
  CACHE 1;

