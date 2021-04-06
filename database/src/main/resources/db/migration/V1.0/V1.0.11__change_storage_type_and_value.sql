-- Update storage to match with model.
alter table meta_model drop constraint fk_storage;
alter table meta_storage alter column id type varchar(255);
update meta_storage set id = 'default' where id = '1';
alter table meta_model alter column storage_fkey type varchar(255);
update meta_model set storage_fkey = 'default' where storage_fkey = '1';
alter table meta_model add constraint fk_storage foreign key (storage_fkey) references meta_storage (id) match full
      on update no action on delete no action;
