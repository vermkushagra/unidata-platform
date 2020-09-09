CREATE TABLE audit_events (
  id           BIGSERIAL UNIQUE         NOT NULL,
  create_date  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  created_by   CHARACTER VARYING(256)   NOT NULL,
  details      TEXT                     NULL,
  operation_id VARCHAR(256)             NOT NULL,
  type         INT                      NOT NULL,
  CONSTRAINT pk_audit PRIMARY KEY (id)
);

CREATE TABLE audit_details (
  id            BIGSERIAL UNIQUE         NOT NULL,
  audit_id      BIGINT                   NOT NULL,
  is_success    BOOLEAN                  NOT NULL DEFAULT TRUE,
  details       TEXT                     NULL,
  etalon_id     CHAR(36)                 NULL,
  origin_id     CHAR(36)                 NULL,
  source_system VARCHAR(256)             NULL,
  entity_name   VARCHAR(256)             NULL,
  external_id   VARCHAR(512)             NULL,
  CONSTRAINT pk_audit_details PRIMARY KEY (id),
  CONSTRAINT fk_audit FOREIGN KEY (audit_id) REFERENCES audit_events (id) MATCH FULL
  ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE INDEX ix_operation_id ON audit_events (operation_id);
CREATE INDEX ix_audit_id ON audit_details (audit_id);

