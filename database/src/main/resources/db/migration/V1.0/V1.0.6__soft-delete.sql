-- Add status
ALTER TABLE origins ADD COLUMN status smallint NOT NULL default 0;
-- Add status
ALTER TABLE etalons ADD COLUMN status smallint NOT NULL default 0;