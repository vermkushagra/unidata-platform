delete from duplicates;
delete from binary_data;
delete from character_data;
delete from origins cascade;
delete from etalons cascade;

-- etalons
alter table etalons alter column name type character varying (256);
alter table etalons 
	alter column create_date type timestamp with time zone,
	alter column create_date set not null,
	alter column create_date set default current_timestamp;
alter table etalons alter column update_date type timestamp with time zone,
	alter column update_date drop not null,
	alter column update_date set default null;
alter table etalons 
	alter column created_by type character varying (256),
	alter column created_by set not null;
alter table etalons alter column updated_by type character varying (256);
drop index if exists ix_etalons_name;
create index ix_etalons_name on etalons (name);
drop index if exists ix_etalons_status;
create index ix_etalons_status on etalons (status);

-- origins update
alter table origins drop column if exists data;
alter table origins drop column if exists valid_from;
alter table origins drop column if exists valid_to;
alter table origins drop column if exists revision;

alter table origins 
	alter column natural_key type character varying(512),
	alter column natural_key set not null;
alter table origins rename column natural_key to external_id;

alter table origins 
	alter column origin_name type character varying(256),
	alter column origin_name set not null;
alter table origins rename column origin_name to source_system;

alter table origins 
	alter column name type character varying(256),
	alter column name set not null;

alter table origins rename column golden_id to etalon_id;

alter table origins 
	alter column create_date type timestamp with time zone,
	alter column create_date set not null,
	alter column create_date set default current_timestamp;
alter table origins 
	alter column update_date type timestamp with time zone,
	alter column update_date drop not null,
	alter column update_date set default null;
alter table origins 
	alter column created_by type character varying (256),
	alter column created_by set not null;
alter table origins 
	alter column updated_by type character varying (256),
	alter column updated_by drop default;
-- FK	
alter table origins add constraint fk_origins_etalon_id foreign key (etalon_id) references etalons (id) match full;
-- UQ
alter table origins drop constraint if exists uq_origins_external_id_source_system;
alter table origins add constraint uq_origins_external_id_source_system unique (external_id, source_system, name);
-- IDX
drop index if exists ix_origins_status;
create index ix_origins_status on origins (status);

-- origins_vistory create
drop table if exists origins_vistory;
create table origins_vistory (
	id char(36),
	origin_id char(36) not null,
	revision integer not null,
	valid_from timestamp with time zone,
	valid_to timestamp with time zone,
	data text,
	create_date timestamp with time zone not null default current_timestamp,
	created_by character varying(256) not null,
	status character varying(256) not null default 'ACTIVE',
	constraint pk_origins_vistory primary key (id),	
	constraint fk_origins_origin_id foreign key (origin_id) references origins (id) match full,
	constraint uq_origins_vistory unique (origin_id, revision)
);

drop index if exists ix_origins_vistory_valid_from_valid_to;
create index ix_origins_vistory_valid_from_valid_to on origins_vistory (valid_from, valid_to);