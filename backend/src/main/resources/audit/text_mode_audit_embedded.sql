
CREATE TEXT TABLE IF NOT EXISTS AUDIT_EVENTS (
  create_date  TIMESTAMP WITH TIME ZONE      NOT NULL,
  created_by   CHARACTER VARYING(256)        NOT NULL,
  operation_id VARCHAR(256),
  details      LONGVARCHAR                   NULL,
  action       VARCHAR(256)
);
SET TABLE AUDIT_EVENTS SOURCE 'audit;fs=|'