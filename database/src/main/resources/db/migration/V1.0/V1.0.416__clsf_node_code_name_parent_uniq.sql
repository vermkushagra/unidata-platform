ALTER TABLE clsf_node DROP CONSTRAINT clsf_node_unique_name_parent_id;
ALTER TABLE clsf_node ADD CONSTRAINT clsf_node_unique_code_name_parent_id UNIQUE(code, name, parent_node_id, clsf_id);
