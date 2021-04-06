CREATE TABLE DQ_ERRORS
(
  id SERIAL NOT NULL,
  severity character varying NOT NULL,
  category character varying NOT NULL,
  message character varying NOT NULL,
  rule_name character varying NOT NULL,
  record_id character varying NOT NULL,
  create_date TIMESTAMP NOT NULL DEFAULT NOW(),
  update_date TIMESTAMP NOT NULL DEFAULT NOW(),
  created_by character varying NOT NULL,
  updated_by character varying NOT NULL,  
  CONSTRAINT dq_errors_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);