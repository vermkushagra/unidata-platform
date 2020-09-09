DELETE FROM clusters;

ALTER TABLE clusters
  ALTER COLUMN group_id SET NOT NULL;

ALTER TABLE matched_records
  ADD CONSTRAINT etalon_record_fkey FOREIGN KEY (etalon_id) REFERENCES etalons (id) MATCH FULL ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS blocked_matched_records (
  id                    SERIAL       NOT NULL,
  cluster_identifier    VARCHAR(255) NOT NULL,
  rule_id               INT          NOT NULL,
  group_id              INT          NULL,
  entity_name           VARCHAR(255) NOT NULL,
  storage_fkey          VARCHAR(255) NOT NULL,
  blocked_etalon_id     CHAR(36)     NOT NULL,
  blocked_for_etalon_id CHAR(36)     NOT NULL,
  CONSTRAINT blocked_matched_records_pkey PRIMARY KEY (id),
  CONSTRAINT group_fkey FOREIGN KEY (group_id) REFERENCES matching_groups (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT rule_fkey FOREIGN KEY (rule_id) REFERENCES matching_rules (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT etalon_record_fkey_1 FOREIGN KEY (blocked_etalon_id) REFERENCES etalons (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT etalon_record_fkey_2 FOREIGN KEY (blocked_for_etalon_id) REFERENCES etalons (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT meta_model_fkey FOREIGN KEY (entity_name, storage_fkey) REFERENCES meta_model (id, storage_fkey) MATCH FULL ON DELETE CASCADE
);

CREATE INDEX blocked_records_search_index ON blocked_matched_records (entity_name, storage_fkey, group_id, rule_id, cluster_identifier);

CREATE INDEX blocked_etalons_index ON blocked_matched_records (blocked_etalon_id);
CREATE INDEX blocked_etalons_for_index ON blocked_matched_records (blocked_for_etalon_id);