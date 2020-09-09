ALTER TABLE classifiers
  DROP COLUMN numeric;

ALTER TABLE classifiers
  ADD COLUMN code_pattern VARCHAR(256) NULL;