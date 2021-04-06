ALTER TABLE matching_groups
  RENAME COLUMN data TO description;

CREATE TABLE rule_group_linker (
  group_id INT NOT NULL,
  rule_id  INT NOT NULL,
  CONSTRAINT rule_group_linker_pkey PRIMARY KEY (group_id, rule_id),
  CONSTRAINT matching_rules_fkey FOREIGN KEY (rule_id) REFERENCES matching_rules (id) MATCH FULL ON DELETE CASCADE,
  CONSTRAINT matching_group_fkey FOREIGN KEY (group_id) REFERENCES matching_groups (id) MATCH FULL ON DELETE CASCADE
)