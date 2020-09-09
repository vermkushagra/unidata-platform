-- Entities table
CREATE SEQUENCE sq_entities_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 2147483647
  START 1
  CACHE 1;

CREATE TABLE entities (
  id INTEGER NOT NULL DEFAULT nextval('sq_entities_id'),
  model_fkey INTEGER NOT NULL,
  name TEXT NOT NULL,
  version INTEGER NOT NULL,
  data text NOT NULL,
  create_date TIMESTAMP NOT NULL DEFAULT now(),
  update_date TIMESTAMP NULL DEFAULT now(),
  created_by TEXT NOT NULL,
  updated_by TEXT NULL,
  CONSTRAINT entities_pkey PRIMARY KEY (id)
);

ALTER TABLE entities ADD CONSTRAINT fk_entity_model FOREIGN KEY (model_fkey)
  REFERENCES meta_model (id) MATCH FULL
  ON DELETE NO ACTION ON UPDATE NO ACTION;
  
ALTER SEQUENCE sq_entities_id OWNED BY entities.id;
-- END OF Entities table
