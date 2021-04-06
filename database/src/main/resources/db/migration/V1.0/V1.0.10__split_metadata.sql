-- Alter meta model table
alter table meta_model rename column name to type;
alter table meta_model drop constraint meta_model_pkey cascade;
alter table meta_model alter column id type varchar(255);
alter table meta_model alter column type type varchar(128);
alter table meta_model add constraint meta_model_pkey primary key(id, storage_fkey);
