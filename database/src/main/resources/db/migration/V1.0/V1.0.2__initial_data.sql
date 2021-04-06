INSERT INTO meta_storage
  (id, name, created_by)
VALUES
  (1, 'Default storage', 'migrate');

INSERT INTO meta_model
  (id, storage_fkey, name, version, data, created_by)
VALUES
  (1, 1, 'Default model', 1, '', 'migrate');

select setval('sq_meta_storage', 2);
select setval('sq_meta_model', 2);