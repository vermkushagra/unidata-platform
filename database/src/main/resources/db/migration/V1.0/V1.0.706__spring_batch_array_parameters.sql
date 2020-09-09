set session schema 'unidata_batch_job';

ALTER TABLE batch_job_execution_params ADD COLUMN string_val_arr text[];
ALTER TABLE batch_job_execution_params ADD COLUMN date_val_arr timestamp without time zone[];
ALTER TABLE batch_job_execution_params ADD COLUMN long_val_arr bigint[];
ALTER TABLE batch_job_execution_params ADD COLUMN double_val_arr double precision[];

set session schema 'public';