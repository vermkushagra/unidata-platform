-- drop fn
drop function if exists upsert_relation_vistory(
    _id character,
    _origin_id character,
    _operation_id character,
    _valid_from timestamp with time zone,
    _valid_to timestamp with time zone,
    _data text,
    _created_by character varying,
    _status character varying,
    _approval character varying,
    _major integer,
    _minor integer);

drop function if exists upsert_record_vistory(
    _id character,
    _origin_id character,
    _operation_id character,
    _valid_from timestamp with time zone,
    _valid_to timestamp with time zone,
    _data text,
    _created_by character varying,
    _create_date timestamp with time zone,
    _status character varying,
    _approval character varying,
    _shift character varying,
    _major integer,
    _minor integer);

drop function if exists upsert_classifier_vistory(
    character,
    character,
    character,
    timestamp with time zone,
    timestamp with time zone,
    text,
    character varying,
    character varying,
    character varying,
    integer,
    integer);
