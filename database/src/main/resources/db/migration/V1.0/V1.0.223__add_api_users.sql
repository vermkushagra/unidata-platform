CREATE TABLE s_apis
(
  id serial NOT NULL,
  name character varying(255),
  display_name character varying(255),
  description text,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone,
  created_by character varying(255),
  updated_by character varying(255),
  CONSTRAINT pk_s_apis PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
); 

CREATE TABLE s_user_s_apis
(
  id serial NOT NULL,
  s_user_id integer,
  s_api_id integer,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone,
  created_by character varying(255),
  updated_by character varying(255),
  CONSTRAINT pk_s_user_s_apis PRIMARY KEY (id),
  CONSTRAINT fk_s_user_s_apis FOREIGN KEY (s_api_id)
      REFERENCES s_apis(id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_s_user_s_apis_0 FOREIGN KEY (s_user_id)
      REFERENCES s_user (id) MATCH FULL
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT idx_s_user_s_apis UNIQUE (s_api_id, s_user_id)
)
WITH (
  OIDS=FALSE
);
CREATE INDEX idx_s_user_s_apis_0
  ON s_user_s_apis
  USING btree
  (s_api_id);

CREATE INDEX idx_s_user_s_apis_1
  ON s_user_s_apis
  USING btree
  (s_user_id);
INSERT INTO s_apis(
             name, display_name, description, created_at, updated_at, 
            created_by, updated_by)
    VALUES ('REST', 'WEB интерфейс', 'WEB интерфейс', current_date, current_date, 'SYSTEM', 
            'SYSTEM');
INSERT INTO s_apis(
             name, display_name, description, created_at, updated_at, 
            created_by, updated_by)
    VALUES ('SOAP', 'SOAP интерфейс', 'SOAP интерфейс', current_date, current_date, 'SYSTEM', 
            'SYSTEM');
            
INSERT INTO s_user_s_apis(
             s_user_id, s_api_id, created_at, updated_at, created_by, 
            updated_by)
    SELECT su.id, sa.id, current_date, current_date, 'SYSTEM', 
            'SYSTEM'
    FROM (SELECT id FROM s_user) su, (SELECT id FROM s_apis) sa;