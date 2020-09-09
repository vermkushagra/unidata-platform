-- Origins
ALTER TABLE origins 
	ALTER COLUMN status TYPE VARCHAR(256),
	ALTER COLUMN status SET NOT NULL,
	ALTER COLUMN status SET DEFAULT 'ACTIVE';

UPDATE origins SET status = 'ACTIVE' WHERE status = '0';
UPDATE origins SET status = 'INACTIVE' WHERE status = '1';

-- Etalons
ALTER TABLE etalons 
	ALTER COLUMN status TYPE VARCHAR(256),
	ALTER COLUMN status SET NOT NULL,
	ALTER COLUMN status SET DEFAULT 'ACTIVE';

UPDATE etalons SET status = 'ACTIVE' WHERE status = '0';
UPDATE etalons SET status = 'INACTIVE' WHERE status = '1';