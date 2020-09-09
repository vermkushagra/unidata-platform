ALTER TABLE audit_details RENAME TO audit_import_job_details;

CREATE TABLE audit_record_operation_details (
  id            BIGSERIAL UNIQUE         NOT NULL,
  audit_id      BIGINT                   NOT NULL,
  etalon_id     CHAR(36)                 NULL,
  origin_id     CHAR(36)                 NULL,
  registry      VARCHAR(256)             NULL,
  CONSTRAINT pk_audit_record_operation_details PRIMARY KEY (id),
  CONSTRAINT fk_audit FOREIGN KEY (audit_id) REFERENCES audit_events (id) MATCH FULL
  ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE INDEX ix_audit_record_operation_details_audit_id ON audit_record_operation_details (audit_id);

