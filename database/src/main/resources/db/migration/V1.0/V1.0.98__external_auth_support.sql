alter table s_token drop constraint if exists fk_s_token;
alter table s_token add constraint fk_s_token foreign key (s_user_id) references s_user (id) match simple on update cascade on delete cascade;

delete from s_user where external = true;

alter table s_user drop column if exists auth_type;
alter table s_user drop constraint if exists unique_login_source;
alter table s_user add constraint unique_login_source unique(login);
