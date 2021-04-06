alter table job_parameter add column val_string varchar(250);
alter table job_parameter add column val_date timestamp with time zone;
alter table job_parameter add column val_long bigint;
alter table job_parameter add column val_double double precision;

update job_parameter set val_string = CAST(value AS varchar(250));

alter table job_parameter drop column value;
