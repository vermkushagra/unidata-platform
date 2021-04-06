CREATE TABLE IF NOT EXISTS configuration
(
  name VARCHAR(255) PRIMARY KEY NOT NULL,
  value BYTEA
);
CREATE UNIQUE INDEX configuration_name_uindex ON configuration (name);