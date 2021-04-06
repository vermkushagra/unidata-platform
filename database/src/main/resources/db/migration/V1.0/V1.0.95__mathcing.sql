CREATE TABLE IF NOT EXISTS matching_rules (
  id          SERIAL       NOT NULL,
  name        VARCHAR(256) NOT NULL,
  data        TEXT         NOT NULL,
  entity_name VARCHAR(256) NOT NULL,
  active      BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT matching_rules_pkey PRIMARY KEY (id)
);

CREATE INDEX entity_name_index ON matching_rules USING HASH (entity_name);