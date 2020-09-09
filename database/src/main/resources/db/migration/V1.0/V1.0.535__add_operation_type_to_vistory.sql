CREATE TYPE vistory_operation_type AS ENUM
('DIRECT','CASCADED');

ALTER TYPE vistory_operation_type
  OWNER TO postgres;

ALTER TABLE origins_vistory ADD COLUMN operation_type vistory_operation_type;
ALTER TABLE origins_relations_vistory ADD COLUMN operation_type vistory_operation_type;