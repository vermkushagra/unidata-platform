ALTER TABLE measurement_units
  DROP CONSTRAINT measurement_units_pkey;
ALTER TABLE measurement_units
  ADD PRIMARY KEY (value_id, id);
ALTER TABLE measurement_units
  ADD COLUMN unit_order INTEGER NOT NULL DEFAULT 0;