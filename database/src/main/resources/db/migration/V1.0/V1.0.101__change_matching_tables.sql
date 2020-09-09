ALTER TABLE matched_records DROP CONSTRAINT ck_one_is_null;
ALTER TABLE matched_records ALTER COLUMN rule_id SET NOT NULL;