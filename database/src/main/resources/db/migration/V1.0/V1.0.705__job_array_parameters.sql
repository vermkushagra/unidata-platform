ALTER TABLE job_parameter ADD COLUMN val_arr_string text[];
ALTER TABLE job_parameter ADD COLUMN val_arr_date timestamp with time zone[];
ALTER TABLE job_parameter ADD COLUMN val_arr_long bigint[];
ALTER TABLE job_parameter ADD COLUMN val_arr_double double precision[];
ALTER TABLE job_parameter ADD COLUMN val_arr_boolean boolean[];
