DROP TABLE IF EXISTS measurement_units;
DROP TABLE IF EXISTS measurement_values;

CREATE TABLE measurement_values (
  id         VARCHAR(63) NOT NULL,
  name       VARCHAR(63) NOT NULL,
  short_name VARCHAR(31) NOT NULL,
  CONSTRAINT measurement_values_pkey PRIMARY KEY (id)
);

CREATE TABLE measurement_units (
  id         VARCHAR(63)  NOT NULL,
  name       VARCHAR(63)  NOT NULL,
  short_name VARCHAR(31)  NOT NULL,
  function   VARCHAR(255) NOT NULL,
  value_id   VARCHAR(63)  NOT NULL,
  base       BOOLEAN      NOT NULL,
  CONSTRAINT measurement_units_pkey PRIMARY KEY (id),
  CONSTRAINT measurement_values_fkey FOREIGN KEY (value_id) REFERENCES measurement_values (id) MATCH FULL ON DELETE CASCADE
);