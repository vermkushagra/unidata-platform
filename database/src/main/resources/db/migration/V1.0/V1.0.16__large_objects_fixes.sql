-- BLOBs
alter table binary_data 
    add column size bigint,
    add column status character varying(256) not null default 'PENDING';
alter table binary_data 
    alter column id type char(36),
    alter column id drop default;           
drop sequence binary_data_id_seq;       
alter table binary_data 
    alter column create_date type timestamp with time zone,
    alter column create_date set not null,
    alter column create_date set default current_timestamp;
alter table binary_data 
    alter column update_date type timestamp with time zone,
    alter column update_date drop not null,
    alter column update_date set default null;

-- CLOBs
alter table character_data 
    add column size bigint,
    add column status character varying(256) not null default 'PENDING';
alter table character_data 
    alter column id type char(36),
    alter column id drop default;               
drop sequence character_data_id_seq;            
alter table character_data  
    alter column create_date type timestamp with time zone,
    alter column create_date set not null,
    alter column create_date set default current_timestamp;
alter table character_data  
    alter column update_date type timestamp with time zone,
    alter column update_date drop not null,
    alter column update_date set default null;
