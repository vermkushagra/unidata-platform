CREATE SCHEMA IF NOT EXISTS unidata_audit;

set session schema 'unidata_audit';

CREATE TABLE IF NOT EXISTS AUDIT_EVENTS (
  id           BIGSERIAL UNIQUE         NOT NULL,
  create_date  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  created_by   CHARACTER VARYING(256)   NOT NULL,
  operation_id VARCHAR(256),
  details      TEXT                     NULL,
  action       VARCHAR(256),
  CONSTRAINT pk_audit PRIMARY KEY (id)
);

set session schema 'public';
