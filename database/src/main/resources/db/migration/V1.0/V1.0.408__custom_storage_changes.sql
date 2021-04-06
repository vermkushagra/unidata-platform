alter table s_custom_settings rename to s_custom_storage;

create unique index key_user_name_uindex ON s_custom_storage (key, user_name);
create index ix_key on s_custom_storage using btree (key);
create index ix_user_name on s_custom_storage using btree (user_name);