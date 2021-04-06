-- Drop old entities table
DROP TABLE entities;

-- Create new tables
CREATE TABLE origins (
  id CHAR(36) NOT NULL,
  name TEXT NOT NULL,
  data text NOT NULL,
  version INTEGER NOT NULL,
  origin_name text NOT NULL,
  natural_key text,
  golden_id CHAR(36) NOT NULL,
  create_date TIMESTAMP NOT NULL DEFAULT now(),
  update_date TIMESTAMP NULL DEFAULT now(),
  created_by TEXT NOT NULL,
  updated_by TEXT NULL,
  CONSTRAINT origins_pkey PRIMARY KEY (id)
);

CREATE TABLE etalons (
  id CHAR(36) NOT NULL,
  name TEXT NOT NULL,
  data text NOT NULL,
  version INTEGER NOT NULL,
  create_date TIMESTAMP NOT NULL DEFAULT now(),
  update_date TIMESTAMP NULL DEFAULT now(),
  created_by TEXT NOT NULL,
  updated_by TEXT NULL,
  CONSTRAINT etalons_pkey PRIMARY KEY (id)
);

-- END OF Entities table
