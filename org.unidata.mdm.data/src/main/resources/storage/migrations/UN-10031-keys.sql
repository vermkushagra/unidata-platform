-- Records
-- drop type record_origin_key cascade;
create type record_origin_key as (
    id uuid,
    initial_owner uuid,
    status record_status,
    enrichment boolean,
    revision integer,
    source_system varchar(256),
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    external_id varchar(512)
);

-- drop type record_key cascade;
create type record_key as (
    shard integer,
    lsn bigint,
    id uuid,
    name varchar(256),
    status record_status,
    state approval_state,
    approved boolean,
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    origin_keys record_origin_key[]
);

-- Relations
-- drop type relation_origin_key cascade;
create type relation_origin_key as (
    id uuid,
    initial_owner uuid,
    status record_status,
    enrichment boolean,
    revision integer,
    source_system varchar(256),
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    from_key uuid,
    to_key uuid
);

-- drop type relation_key cascade;
create type relation_key as (
    shard integer,
    lsn bigint,
    id uuid,
    name varchar(256),
    status record_status,
    state approval_state,
    approved boolean,
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    type relation_type,
    from_key uuid,
    to_key uuid,
    origin_keys relation_origin_key[]
);

-- Classifiers
-- drop type classifier_origin_key cascade;
create type classifier_origin_key as (
    id uuid,
    initial_owner uuid,
    status record_status,
    enrichment boolean,
    revision integer,
    source_system varchar(256),
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    node_id uuid,
    record_key uuid
);

-- drop type record_key cascade;
create type classifier_key as (
    shard integer,
    lsn bigint,
    id uuid,
    name varchar(256),
    status record_status,
    state approval_state,
    approved boolean,
    create_date timestamptz,
    created_by varchar(256),
    update_date timestamptz,
    updated_by varchar(256),
    record_key uuid,
    origin_keys classifier_origin_key[]
);

