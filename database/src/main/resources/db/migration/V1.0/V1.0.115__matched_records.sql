ALTER TABLE clusters ADD COLUMN version INTEGER NOT NULL DEFAULT 1;

DROP TABLE IF EXISTS matched_records;

CREATE TABLE IF NOT EXISTS matched_records (
  cluster_id    INT                      NOT NULL,
  etalon_id     VARCHAR(36)              NOT NULL,
  etalon_date   TIMESTAMP WITH TIME ZONE NOT NULL,
  matching_rate SMALLINT                 NOT NULL DEFAULT 100,
  CONSTRAINT matched_records_pkey PRIMARY KEY (cluster_id,etalon_id),
  CONSTRAINT cluster_fkey FOREIGN KEY (cluster_id) REFERENCES clusters (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT etalon_fkey FOREIGN KEY (etalon_id) REFERENCES etalons (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT check_rate CHECK (matching_rate >= 0 AND matching_rate <= 100)
);

CREATE INDEX etalons_fkey_index ON matched_records (etalon_id);

