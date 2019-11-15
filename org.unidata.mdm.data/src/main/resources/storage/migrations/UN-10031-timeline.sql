create type record_vistory_data as (
    id uuid,
    origin_id uuid,
    shard int4,
    revision int4,
    valid_from timestamptz,
    valid_to timestamptz,
    create_date timestamptz,
    created_by varchar,
    status record_status,
    approval approval_state,
    shift data_shift,
    operation_type operation_type,
    operation_id text,
    data_a text,
    data_b bytea,
    major int4,
    minor int4
);

create type record_timeline as (
    keys record_key,
    vistory_data record_vistory_data[]
);
---------------------------------------------- Relations ---------------------------------------------
create type relation_vistory_data as (
    id uuid,
    origin_id uuid,
    shard int4,
    revision int4,
    valid_from timestamptz,
    valid_to timestamptz,
    create_date timestamptz,
    created_by varchar,
    status record_status,
    approval approval_state,
    shift data_shift,
    operation_type operation_type,
    operation_id text,
    data_a text,
    data_b bytea,
    major int4,
    minor int4);

create type relation_timeline as (
    keys relation_key,
    vistory_data relation_vistory_data[]
);
-- Classifiers
create type classifier_vistory_data as (
    id uuid,
    origin_id uuid,
    shard int4,
    revision int4,
    valid_from timestamptz,
    valid_to timestamptz,
    create_date timestamptz,
    created_by varchar(256),
    status record_status,
    approval approval_state,
    shift data_shift,
    operation_type operation_type,
    operation_id text,
    data_a text,
    data_b bytea,
    major int4,
    minor int4);

create type classifier_timeline as (
    keys classifier_key,
    vistory_data classifier_vistory_data[]
);