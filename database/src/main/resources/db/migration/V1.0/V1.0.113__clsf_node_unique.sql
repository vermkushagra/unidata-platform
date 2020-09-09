ALTER TABLE clsf_node DROP CONSTRAINT clsf_node_unique_node_id CASCADE;
ALTER TABLE clsf_node ADD CONSTRAINT  clsf_node_unique_node_id UNIQUE(node_id, clsf_id);
ALTER TABLE clsf_node
   ADD CONSTRAINT FK_node_id_parent_id
   FOREIGN KEY(parent_node_id, clsf_id)
   REFERENCES clsf_node(node_id, clsf_id)