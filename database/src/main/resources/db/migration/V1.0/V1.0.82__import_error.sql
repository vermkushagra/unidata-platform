CREATE TABLE import_errors (
  id           BIGSERIAL UNIQUE         NOT NULL,
  create_date  TIMESTAMP WITH TIME ZONE DEFAULT now(),
  description  TEXT                     NULL,
  error        TEXT                     NULL,
  sql          TEXT                     NULL,
  operation_id VARCHAR(256)             NOT NULL,
  index        INT                      NOT NULL,
  CONSTRAINT pk_import_errors PRIMARY KEY (id)
);