ALTER TABLE audit_record_operation_details
  ADD COLUMN is_success BOOLEAN NOT NULL DEFAULT TRUE;