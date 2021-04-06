ALTER TABLE clsf_node
   DROP CONSTRAINT  FK_node_id_parent_id;
ALTER TABLE clsf_node
   ADD CONSTRAINT FK_node_id_parent_id
   FOREIGN KEY(parent_node_id, clsf_id)
   REFERENCES clsf_node(node_id, clsf_id) ON DELETE CASCADE;