-- s_resource
alter table s_resource drop column if exists parent_id;
alter table s_resource add column parent_id integer default null;
alter table s_resource add constraint fk_s_resource_parent_id 
    foreign key (parent_id) references s_resource (id) match full
    on update cascade on delete cascade;
drop index if exists ix_s_resource_parent_id;
create index ix_s_resource_parent_id on s_resource using btree (parent_id);

alter table s_resource drop constraint if exists uq_s_resource_name;
alter table s_resource add constraint uq_s_resource_name unique (name);

-- s_right_s_resource
alter table s_right_s_resource drop constraint if exists fk_s_right_s_resource_0;
alter table s_right_s_resource add constraint fk_s_right_s_resource_0 
    foreign key (s_resource_id) references s_resource (id) match full
    on update cascade on delete cascade;

