ALTER TABLE clsf_node ADD CONSTRAINT  clsf_node_unique_name_parent_id UNIQUE(name, parent_node_id, clsf_id);
