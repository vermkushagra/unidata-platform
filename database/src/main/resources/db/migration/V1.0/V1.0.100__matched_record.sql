ALTER TABLE rule_group_linker
  ADD COLUMN order_number INT NOT NULL DEFAULT 1;


CREATE TABLE IF NOT EXISTS matched_records (
  id               BIGSERIAL                NOT NULL,
  source_etalon_id VARCHAR(36)              NOT NULL,
  target_etalon_id VARCHAR(36)              NOT NULL,
  cluster_id       INT                      NULL,
  entity_name      VARCHAR(255)             NOT NULL,
  matching_date    TIMESTAMP WITH TIME ZONE NOT NULL,
  group_id         INT                      NULL,
  rule_id          INT                      NULL,
  storage_fkey     VARCHAR(255)             NOT NULL DEFAULT 'default',
  CONSTRAINT matched_records_pkey PRIMARY KEY (id),
  CONSTRAINT matching_group_fkey FOREIGN KEY (group_id) REFERENCES matching_groups (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT matching_rules_fkey FOREIGN KEY (rule_id) REFERENCES matching_rules (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT meta_model_fkey FOREIGN KEY (entity_name, storage_fkey) REFERENCES meta_model (id, storage_fkey) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT CK_one_is_null CHECK (     (group_id IS NOT NULL AND rule_id IS NULL) OR
                                        (rule_id IS NOT NULL AND group_id IS NULL))
);

CREATE INDEX partial_cluster_index ON matched_records (cluster_id)
  WHERE cluster_id IS NOT NULL;

CREATE INDEX entity_rule_group_index ON matched_records (entity_name, storage_fkey, rule_id, group_id);

ALTER TABLE matching_rules ALTER data DROP NOT NULL;
ALTER TABLE matching_rules RENAME COLUMN data TO settings;
ALTER TABLE matching_rules ADD COLUMN description TEXT;
ALTER TABLE matching_rules ADD COLUMN storage_fkey VARCHAR(255) NOT NULL DEFAULT 'default';
ALTER TABLE matching_groups ADD COLUMN storage_fkey VARCHAR(255) NOT NULL DEFAULT 'default';
ALTER TABLE matching_rules ADD CONSTRAINT meta_model_fkey FOREIGN KEY (entity_name, storage_fkey) REFERENCES meta_model (id, storage_fkey) MATCH FULL ON DELETE CASCADE;
ALTER TABLE matching_groups ADD CONSTRAINT meta_model_fkey FOREIGN KEY (entity_name, storage_fkey) REFERENCES meta_model (id, storage_fkey) MATCH FULL ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS matching_algorithms (
  id           SERIAL NOT NULL,
  algorithm_id INT    NOT NULL,
  rule_id      INT    NOT NULL,
  data         TEXT   NOT NULL,
  CONSTRAINT matching_algorithms_pkey PRIMARY KEY (id),
  CONSTRAINT matching_rules_fkey FOREIGN KEY (rule_id) REFERENCES matching_rules (id) MATCH FULL ON DELETE CASCADE
);

CREATE INDEX rule_index ON matching_algorithms (rule_id);