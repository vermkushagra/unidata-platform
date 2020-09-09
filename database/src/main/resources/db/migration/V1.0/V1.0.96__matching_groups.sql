ALTER TABLE matching_rules  ADD CONSTRAINT unique_name_and_entity UNIQUE (name, entity_name);

CREATE TABLE IF NOT EXISTS matching_groups (
  id          SERIAL       NOT NULL,
  name        VARCHAR(256) NOT NULL,
  entity_name VARCHAR(256) NOT NULL,
  data        TEXT         NOT NULL,
  active      BOOLEAN      NOT NULL  DEFAULT FALSE,
  CONSTRAINT matching_group_pkey PRIMARY KEY (id),
  CONSTRAINT unique_name_and_entity_name UNIQUE (name, entity_name)
)

