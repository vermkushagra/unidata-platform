alter table blocked_matched_records
  drop constraint meta_model_fkey;

alter table clusters
  drop constraint meta_model_fkey;

drop index if exists search_index;
create unique index ix_clusters_search ON clusters using btree (entity_name, group_id, rule_id, cluster_identifier);
create index ix_entity_name_clusters ON clusters using btree (entity_name);

drop index if exists blocked_records_search_index;
create index ix_blocked_records_search ON blocked_matched_records using btree (entity_name, group_id, rule_id, cluster_identifier);
create index ix_entity_name_blocked ON blocked_matched_records using btree (entity_name);

alter table matching_rules
  add column preprocessing boolean default true;

alter table clusters
  alter column storage_fkey  DROP NOT NULL;

alter table blocked_matched_records
  alter column storage_fkey  DROP NOT NULL;