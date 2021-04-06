DROP TABLE matched_records;

CREATE TABLE IF NOT EXISTS clusters (
  id                 SERIAL8                  NOT NULL,
  cluster_identifier VARCHAR(255)             NOT NULL,
  rule_id            INT                      NOT NULL,
  group_id           INT                      NULL,
  entity_name        VARCHAR(255)             NOT NULL,
  storage_fkey       VARCHAR(255)             NOT NULL,
  matching_date      TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT clusters_pkey PRIMARY KEY (id),
  CONSTRAINT group_fkey FOREIGN KEY (group_id) REFERENCES matching_groups (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT rule_fkey FOREIGN KEY (rule_id) REFERENCES matching_rules (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT meta_model_fkey FOREIGN KEY (entity_name, storage_fkey) REFERENCES meta_model (id, storage_fkey) MATCH FULL ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS matched_records (
  id            SERIAL8                  NOT NULL,
  cluster_id    INT                      NOT NULL,
  etalon_id     VARCHAR(36)              NOT NULL,
  etalon_date   TIMESTAMP WITH TIME ZONE NOT NULL,
  matching_rate SMALLINT                 NOT NULL DEFAULT 100,
  CONSTRAINT matched_records_pkey PRIMARY KEY (id),
  CONSTRAINT cluster_fkey FOREIGN KEY (cluster_id) REFERENCES clusters (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT etalon_fkey FOREIGN KEY (etalon_id) REFERENCES etalons (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT check_rate CHECK (matching_rate >= 0 AND matching_rate <= 100)
);

CREATE INDEX clusters_fkey_index ON matched_records (cluster_id);
CREATE INDEX etalons_fkey_index ON matched_records (etalon_id);
CREATE UNIQUE INDEX search_index ON clusters (entity_name, storage_fkey, group_id, rule_id, cluster_identifier);