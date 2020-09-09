ALTER TABLE clsf_node ADD COLUMN clsf_name VARCHAR(2044);

UPDATE clsf_node SET clsf_name = c.name FROM clsf c WHERE clsf_node.clsf_id = c.id;

ALTER TABLE clsf_node ALTER COLUMN clsf_name SET NOT NULL;
ALTER TABLE clsf_node DROP CONSTRAINT fk_clsf_node_clsf;
ALTER TABLE clsf_node ADD CONSTRAINT fk_clsf_node_clsf FOREIGN KEY (clsf_name) REFERENCES clsf(name) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE clsf_node DROP COLUMN clsf_id;
ALTER TABLE clsf DROP COLUMN id;