CREATE TABLE IF NOT EXISTS classifiers (
  name         VARCHAR(256) NOT NULL UNIQUE,
  display_name VARCHAR(256) NOT NULL,
  description  TEXT,
  numeric      BOOLEAN     DEFAULT FALSE,
  CONSTRAINT classifiers_id PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS classifier_nodes (
  id         VARCHAR(256) NOT NULL UNIQUE,
  parent_id  VARCHAR(256) NULL,
  classifier VARCHAR(256) NOT NULL,
  name       VARCHAR(256) NOT NULL,
  code       VARCHAR(256) NULL,
  data       TEXT         NOT NULL,
  CONSTRAINT classifier_nodes_id PRIMARY KEY (id),
  CONSTRAINT fk_classifiers FOREIGN KEY (classifier) REFERENCES classifiers (name) MATCH FULL
  ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_node_parent FOREIGN KEY (parent_id) REFERENCES classifier_nodes (id) MATCH FULL
  ON DELETE CASCADE ON UPDATE CASCADE
);