-- 1. drop fns
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

-- 2. Change types
create type record_status as enum ('ACTIVE', 'INACTIVE', 'MERGED', '');
create type approval_state as enum ('PENDING', 'APPROVED', 'DECLINED', '');
create type data_shift as enum ('PRISTINE', 'REVISED', '');

-- 3. Drop unused tables
drop table audit_import_job_details;
drop table audit_record_operation_details;
drop table auth_audit;
drop table dq_errors;
drop table import_errors;

-- Etalons
-- Drop
drop index if exists ix_etalons_status;
drop index if exists ix_etalons_approval;
alter table etalons drop constraint etalons_pkey cascade;
alter table etalons alter column status drop default;
alter table etalons alter column approval drop default;
-- Create
alter table etalons alter column id set data type uuid using id::uuid;
alter table etalons alter column status set data type record_status using status::record_status;
alter table etalons alter column status set default 'ACTIVE'::record_status;
alter table etalons alter column approval set data type approval_state using approval::approval_state;
alter table etalons alter column approval set default 'APPROVED'::approval_state;
alter table etalons add constraint pk_etalons_id primary key (id);
-- Indexes
create index ix_etalons_status on etalons using btree (status);
create index ix_etalons_approval on etalons using btree (approval);

--Origins
-- Drop
drop index if exists ix_origins_status;
drop index if exists ix_origins_etalon_id;
alter table origins drop constraint origins_pkey cascade;
alter table origins alter column status drop default;
-- Create
alter table origins alter column id set data type uuid using id::uuid;
alter table origins alter column etalon_id set data type uuid using etalon_id::uuid;
alter table origins alter column status set data type record_status using status::record_status;
alter table origins alter column status set default 'ACTIVE'::record_status;
alter table origins add constraint fk_origins_etalon_id foreign key (etalon_id) references etalons (id) match full on update cascade on delete cascade;
alter table origins add constraint pk_origins_id primary key (id);
-- Indexes
create index ix_origins_status on origins using btree (status);
create index ix_origins_etalon_id on origins using btree (etalon_id);

-- Origins vistory
-- Drop
drop index if exists ix_origins_vistory_approval;
drop index if exists ix_origins_vistory_status;
drop index if exists ix_origins_vistory_origin_id;
alter table origins_vistory drop constraint pk_origins_vistory;
alter table origins_vistory alter column status drop default;
alter table origins_vistory alter column approval drop default;
alter table origins_vistory alter column shift drop default;
-- Create
alter table origins_vistory rename column data to data_a;
alter table origins_vistory add column data_b bytea;
alter table origins_vistory alter column id set data type uuid using id::uuid;
alter table origins_vistory alter column origin_id set data type uuid using origin_id::uuid;
alter table origins_vistory alter column status set data type record_status using status::record_status;
alter table origins_vistory alter column status set default 'ACTIVE'::record_status;
alter table origins_vistory alter column approval set data type approval_state using approval::approval_state;
alter table origins_vistory alter column approval set default 'APPROVED'::approval_state;
alter table origins_vistory alter column shift set data type data_shift using shift::data_shift;
alter table origins_vistory alter column shift set default 'PRISTINE'::data_shift;
alter table origins_vistory add constraint fk_origins_vistory_origin_id foreign key (origin_id) references origins (id) match full on update cascade on delete cascade;
alter table origins_vistory add constraint pk_origins_vistory_id primary key (id);
-- Indexes
create index ix_origins_vistory_approval on origins_vistory using btree (approval);
create index ix_origins_vistory_status on origins_vistory using btree (status);
create index ix_origins_vistory_origin_id on origins_vistory using btree (origin_id);

-- Matched records
drop index if exists blocked_etalons_for_index;
drop index if exists blocked_etalons_index;
alter table blocked_matched_records alter column blocked_etalon_id set data type uuid using blocked_etalon_id::uuid;
alter table blocked_matched_records alter column blocked_for_etalon_id set data type uuid using blocked_for_etalon_id::uuid;
alter table blocked_matched_records add constraint fk_blocked_matched_records_blocked_etalon_id foreign key (blocked_etalon_id) references etalons (id) match full on update no action on delete cascade;
alter table blocked_matched_records add constraint fk_blocked_matched_records_blocked_for_etalon_id foreign key (blocked_for_etalon_id) references etalons (id) match full on update no action on delete cascade;
create index ix_blocked_matched_records_blocked_etalon_id on blocked_matched_records using btree (blocked_etalon_id);
create index ix_blocked_matched_records_blocked_for_etalon_id on blocked_matched_records using btree (blocked_for_etalon_id);

drop index if exists etalons_fkey_index;
alter table matched_records drop constraint matched_records_pkey;
alter table matched_records alter column etalon_id set data type uuid using etalon_id::uuid;
alter table matched_records add constraint pk_matched_records_cluster_id_etalon_id primary key (cluster_id, etalon_id);
create index ix_matched_records_etalon_id on matched_records using btree (etalon_id);

-- User event
-- Drop
alter table user_event drop constraint pk_user_event cascade;
-- Create
alter table user_event alter column id set data type uuid using id::uuid;
alter table user_event add constraint pk_user_event_id primary key (id);

-- Binary data
-- Drop
drop index if exists ix_binary_data_etalon_id;
drop index if exists ix_binary_data_event_id;
drop index if exists ix_binary_data_origin_id;
alter table binary_data drop constraint pk_binary_data;
alter table binary_data alter column status drop default;
-- Create
alter table binary_data alter column id set data type uuid using id::uuid;
alter table binary_data alter column etalon_id set data type uuid using etalon_id::uuid;
alter table binary_data alter column origin_id set data type uuid using origin_id::uuid;
alter table binary_data alter column event_id set data type uuid using event_id::uuid;
alter table binary_data alter column status set data type approval_state using status::approval_state;
alter table binary_data alter column status set default 'PENDING'::approval_state;
alter table binary_data add constraint pk_binary_data_id primary key (id);
alter table binary_data add constraint fk_user_event_binary_data_event_id foreign key (event_id) references user_event (id) match full on update cascade on delete cascade;
alter table binary_data add constraint fk_etalons_binary_data_etalon_id foreign key (etalon_id) references etalons (id) match full on update cascade on delete cascade;
alter table binary_data add constraint fk_origins_binary_data_origin_id foreign key (origin_id) references origins (id) match full on update cascade on delete cascade;

-- Indexes
create index ix_binary_data_etalon_id on binary_data using btree (etalon_id);
create index ix_binary_data_origin_id on binary_data using btree (origin_id);
create index ix_binary_data_event_id on binary_data using btree (event_id);

-- Character data
-- Drop
drop index if exists ix_character_data_etalon_id;
drop index if exists ix_character_data_event_id;
drop index if exists ix_character_data_origin_id;
alter table character_data drop constraint pk_character_data;
alter table character_data alter column status drop default;
-- Create
alter table character_data alter column id set data type uuid using id::uuid;
alter table character_data alter column etalon_id set data type uuid using etalon_id::uuid;
alter table character_data alter column origin_id set data type uuid using origin_id::uuid;
alter table character_data alter column event_id set data type uuid using event_id::uuid;
alter table character_data alter column status set data type approval_state using status::approval_state;
alter table character_data alter column status set default 'PENDING';
alter table character_data add constraint pk_character_data_id primary key (id);
alter table character_data add constraint fk_user_event_character_data_event_id foreign key (event_id) references user_event (id) match full on update cascade on delete cascade;
alter table character_data add constraint fk_etalons_character_data_etalon_id foreign key (etalon_id) references etalons (id) match full on update cascade on delete cascade;
alter table character_data add constraint fk_origins_character_data_originn_id foreign key (origin_id) references origins (id) match full on update cascade on delete cascade;

-- Indexes
create index ix_character_data_etalon_id on character_data using btree (etalon_id);
create index ix_character_data_event_id on character_data using btree (event_id);
create index ix_character_data_origin_id on character_data using btree (origin_id);

-- Etalons classifiers
-- Drop
drop index if exists ix_etalons_classifiers_status;
drop index if exists ix_etalons_classifiers_approval;
drop index if exists uq_etalons_classifiers_name_etalon_id_record;
alter table etalons_classifiers drop constraint pk_etalons_classifiers_pkey cascade;
alter table etalons_classifiers alter column status drop default;
alter table etalons_classifiers alter column approval drop default;
-- Create
alter table etalons_classifiers alter column id set data type uuid using id::uuid;
alter table etalons_classifiers alter column etalon_id_record set data type uuid using etalon_id_record::uuid;
alter table etalons_classifiers alter column status set data type record_status using status::record_status;
alter table etalons_classifiers alter column status set default 'ACTIVE'::record_status;
alter table etalons_classifiers alter column approval set data type approval_state using approval::approval_state;
alter table etalons_classifiers alter column approval set default 'APPROVED'::approval_state;
alter table etalons_classifiers add constraint pk_etalons_classifiers_id primary key (id);
-- Indexes
create index ix_etalons_classifiers_status on etalons_classifiers using btree (status);
create index ix_etalons_classifiers_approval on etalons_classifiers using btree (approval);
create unique index uq_etalons_classifiers_name_etalon_id_record on etalons_classifiers using btree (name collate pg_catalog."default", etalon_id_record) where status = 'ACTIVE';

--Origins classifiers
-- Drop
drop index if exists ix_origins_classifiers_status;
drop index if exists ix_origins_classifiers_etalon_id;
drop index if exists uq_origins_classifiers_name_node_id_origin_id_record;
alter table origins_classifiers drop constraint pk_origins_classifiers_pkey cascade;
alter table origins_classifiers alter column status drop default;
-- Create
alter table origins_classifiers alter column id set data type uuid using id::uuid;
alter table origins_classifiers alter column etalon_id set data type uuid using etalon_id::uuid;
alter table origins_classifiers alter column origin_id_record set data type uuid using origin_id_record::uuid;
alter table origins_classifiers alter column status set data type record_status using status::record_status;
alter table origins_classifiers alter column status set default 'ACTIVE'::record_status;
alter table origins_classifiers add constraint fk_origins_classifiers_etalon_id foreign key (etalon_id) references etalons_classifiers (id) match full on update cascade on delete cascade;
alter table origins_classifiers add constraint pk_origins_classifiers_id primary key (id);
-- Indexes
create index ix_origins_classifiers_status on origins_classifiers using btree (status);
create index ix_origins_classifiers_etalon_id on origins_classifiers using btree (etalon_id);
create unique index uq_origins_classifiers_name_node_id_origin_id_record on origins_classifiers using btree (name collate pg_catalog."default", node_id collate pg_catalog."default", origin_id_record) where status = 'ACTIVE';

-- Origins classifiers vistory
-- Drop
drop index if exists ix_origins_classifiers_vistory_approval;
drop index if exists ix_origins_classifiers_vistory_status;
drop index if exists ix_origins_classifiers_vistory_origin_id;
alter table origins_classifiers_vistory drop constraint pk_origins_classifiers_vistory;
alter table origins_classifiers_vistory alter column status drop default;
alter table origins_classifiers_vistory alter column approval drop default;
-- alter table origins_classifiers_vistory alter column shift drop default;
-- Create
alter table origins_classifiers_vistory rename column data to data_a;
alter table origins_classifiers_vistory add column data_b bytea;
alter table origins_classifiers_vistory alter column id set data type uuid using id::uuid;
alter table origins_classifiers_vistory alter column origin_id set data type uuid using origin_id::uuid;
alter table origins_classifiers_vistory alter column status set data type record_status using status::record_status;
alter table origins_classifiers_vistory alter column status set default 'ACTIVE'::record_status;
alter table origins_classifiers_vistory alter column approval set data type approval_state using approval::approval_state;
alter table origins_classifiers_vistory alter column approval set default 'APPROVED'::approval_state;
-- alter table origins_classifiers_vistory alter column shift set data type data_shift using shift::data_shift;
-- alter table origins_classifiers_vistory alter column shift set default 'PRISTINE'::data_shift;
alter table origins_classifiers_vistory add constraint fk_origins_classifiers_vistory_origin_id foreign key (origin_id) references origins_classifiers (id) match full on update cascade on delete cascade;
alter table origins_classifiers_vistory add constraint pk_origins_classifiers_vistory_id primary key (id);
-- Indexes
create index ix_origins_classifiers_vistory_approval on origins_classifiers_vistory using btree (approval);
create index ix_origins_classifiers_vistory_status on origins_classifiers_vistory using btree (status);
create index ix_origins_classifiers_vistory_origin_id on origins_classifiers_vistory using btree (origin_id);

-- Etalons relations
-- Drop
drop index if exists ix_etalons_relations_status;
drop index if exists ix_etalons_relations_approval;
drop index if exists uq_etalons_relations_name_etalon_id_record;
alter table etalons_relations drop constraint pk_etalons_relations_pkey cascade;
alter table etalons_relations drop constraint uq_etalons_relations_name_etalon_id_from_etalon_id_to;
alter table etalons_relations alter column status drop default;
alter table etalons_relations alter column approval drop default;
-- Create
alter table etalons_relations alter column id set data type uuid using id::uuid;
alter table etalons_relations alter column etalon_id_from set data type uuid using etalon_id_from::uuid;
alter table etalons_relations alter column etalon_id_to set data type uuid using etalon_id_to::uuid;
alter table etalons_relations alter column status set data type record_status using status::record_status;
alter table etalons_relations alter column status set default 'ACTIVE'::record_status;
alter table etalons_relations alter column approval set data type approval_state using approval::approval_state;
alter table etalons_relations alter column approval set default 'APPROVED'::approval_state;
alter table etalons_relations add constraint pk_etalons_relations_id primary key (id);
alter table etalons_relations add constraint uq_etalons_relations_name_etalon_id_from_etalon_id_to unique (name, etalon_id_from, etalon_id_to);
-- Indexes
create index ix_etalons_relations_status on etalons_relations using btree (status);
create index ix_etalons_relations_approval on etalons_relations using btree (approval);

--Origins relations
-- Drop
drop index if exists ix_origins_relations_status;
drop index if exists ix_origins_relations_etalon_id;
alter table origins_relations drop constraint uq_origins_relations_name_origin_id_from_origin_id_to;
alter table origins_relations drop constraint pk_origins_relations_pkey cascade;
alter table origins_relations alter column status drop default;
-- Create
alter table origins_relations alter column id set data type uuid using id::uuid;
alter table origins_relations alter column etalon_id set data type uuid using etalon_id::uuid;
alter table origins_relations alter column origin_id_from set data type uuid using origin_id_from::uuid;
alter table origins_relations alter column origin_id_to set data type uuid using origin_id_to::uuid;
alter table origins_relations alter column status set data type record_status using status::record_status;
alter table origins_relations alter column status set default 'ACTIVE'::record_status;
alter table origins_relations add constraint fk_origins_relations_etalon_id foreign key (etalon_id) references etalons_relations (id) match full on update cascade on delete cascade;
alter table origins_relations add constraint pk_origins_relations_id primary key (id);
alter table origins_relations add constraint uq_origins_relations_name_origin_id_from_origin_id_to unique (name, origin_id_from, origin_id_to);
-- Indexes
create index ix_origins_relations_status on origins_relations using btree (status);
create index ix_origins_relations_etalon_id on origins_relations using btree (etalon_id);

-- Origins relations vistory
-- Drop
drop index if exists ix_origins_relations_vistory_approval;
drop index if exists ix_origins_relations_vistory_status;
drop index if exists ix_origins_relations_vistory_origin_id;
alter table origins_relations_vistory drop constraint pk_origins_relations_vistory;
alter table origins_relations_vistory alter column status drop default;
alter table origins_relations_vistory alter column approval drop default;
-- alter table origins_relations_vistory alter column shift drop default;
-- Create
alter table origins_relations_vistory rename column data to data_a;
alter table origins_relations_vistory add column data_b bytea;
alter table origins_relations_vistory alter column id set data type uuid using id::uuid;
alter table origins_relations_vistory alter column origin_id set data type uuid using origin_id::uuid;
alter table origins_relations_vistory alter column status set data type record_status using status::record_status;
alter table origins_relations_vistory alter column status set default 'ACTIVE'::record_status;
alter table origins_relations_vistory alter column approval set data type approval_state using approval::approval_state;
alter table origins_relations_vistory alter column approval set default 'APPROVED'::approval_state;
-- alter table origins_relations_vistory alter column shift set data type data_shift using shift::data_shift;
-- alter table origins_relations_vistory alter column shift set default 'PRISTINE'::data_shift;
alter table origins_relations_vistory add constraint fk_origins_relations_vistory_origin_id foreign key (origin_id) references origins_relations (id) match full on update cascade on delete cascade;
alter table origins_relations_vistory add constraint pk_origins_relations_vistory_id primary key (id);
-- Indexes
create index ix_origins_relations_vistory_approval on origins_relations_vistory using btree (approval);
create index ix_origins_relations_vistory_status on origins_relations_vistory using btree (status);
create index ix_origins_relations_vistory_origin_id on origins_relations_vistory using btree (origin_id);

-- Etalon drafts
drop index if exists ix_etalons_draft_states_etalon_id;
alter table etalons_draft_states alter column etalon_id set data type uuid using etalon_id::uuid;
alter table etalons_draft_states alter column status set data type record_status using status::record_status;
create index ix_etalons_draft_states_etalon_id on etalons_relations_draft_states using btree (etalon_id);
-- Etalon relation drafts
drop index if exists ix_etalons_relations_draft_states_etalon_id;
alter table etalons_relations_draft_states alter column etalon_id set data type uuid using etalon_id::uuid;
alter table etalons_relations_draft_states alter column status set data type record_status using status::record_status;
create index ix_etalons_relations_draft_states_etalon_id on etalons_relations_draft_states using btree (etalon_id);

-- Transitions
drop index if exists ix_etalons_transitions_etalon_id;
alter table etalons_transitions drop constraint etalons_transitions_pkey cascade;
alter table etalons_transitions alter column id set data type uuid using id::uuid;
alter table etalons_transitions alter column etalon_id set data type uuid using etalon_id::uuid;
alter table etalons_transitions add constraint pk_etalons_transitions_id primary key (id);
-- Indexes
create index ix_etalons_transitions_etalon_id on etalons_transitions using btree (etalon_id);

alter table origins_transitions drop constraint origins_transitions_pkey;
alter table origins_transitions alter column etalon_transition_id set data type uuid using etalon_transition_id::uuid;
alter table origins_transitions alter column origin_id set data type uuid using origin_id::uuid;
alter table origins_transitions add constraint pk_origins_transitions_etalon_transition_id_origin_id primary key (etalon_transition_id, origin_id);
alter table origins_transitions add constraint fk_origins_transitions_etalon_transition_id foreign key (etalon_transition_id) references etalons_transitions (id) match full on update cascade on delete cascade;

-- Duplicates log
alter table duplicates drop constraint duplicates_pkey;
alter table duplicates alter column etalon_transition_id set data type uuid using etalon_transition_id::uuid;
alter table duplicates alter column duplicate_id set data type uuid using duplicate_id::uuid;
alter table duplicates add constraint pk_duplicates_etalon_transition_id_duplicate_id primary key (etalon_transition_id, duplicate_id);

-- Functions
-----------------------------------------------------------------------------------
-- drop function
drop function if exists fetch_records_etalon_boundary(_etalon_id character(36), _ts timestamp with time zone, _user_name character varying(256), _is_approver boolean);
-----------------------------------------------------------------------------------
-- fn
create or replace function ud_fetch_records_etalon_boundary(_etalon_id uuid, _ts timestamp with time zone, _user_name character varying(256), _is_approver boolean)
returns table(
    etalon_id uuid,
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[],
    created_by character varying(256),
    updated_by character varying(256),
    create_date timestamp with time zone,
    update_date timestamp with time zone,
    name character varying(256),
    status record_status,
    approval approval_state,
    etalon_gsn bigint) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval, owner, last_update) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_vistory v,
        (   select  i.origin_id,
            max(i.revision) as revision
            from origins o, origins_vistory i
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (
            select max(i.revision) from origins_vistory i
            where i.origin_id = t.origin_id
            and i.status <> 'MERGED'
            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity')
              or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
            and i.revision < t.revision
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
        ))
    --------------------- Join sorted
    select e.id, q.valid_from, q.valid_to, q.contributors, e.created_by,
        (select v.created_by from origins_vistory v, origins o
         where o.etalon_id = e.id and v.origin_id = o.id and (v.approval <> 'DECLINED' and (v.approval <> 'PENDING' or (_is_approver or v.created_by = _user_name)))
         order by v.create_date desc fetch first 1 rows only ) as updated_by,
        (select min(v.create_date) from origins_vistory v, origins o
         where o.etalon_id = e.id and v.origin_id = o.id) as create_date,
        (select max(v.create_date) from origins_vistory v, origins o
         where o.etalon_id = e.id and v.origin_id = o.id and (v.approval <> 'DECLINED' and (v.approval <> 'PENDING' or (_is_approver or v.created_by = _user_name)))) as update_date,
        e.name,
        e.status,
        e.approval,
        e.gsn as etalon_gsn
    from (
        select case when a.date_point = '-infinity' then null else a.date_point end as valid_from,
               case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
               ( select array_agg(
                 row( t.origin_id,
                      t.revision,
                      (select source_system from origins where id = t.origin_id),
                      t.status,
                      t.approval,
                      t.owner,
                      to_char(t.last_update, 'YYYY-MM-DD HH24:MI:SS.MS') )::text) from t,
                  ( select i.origin_id,
                    max(i.revision) as revision
                    from t i
                    where coalesce(i.valid_from, '-infinity') <= a.date_point
                    and coalesce(i.valid_to, 'infinity') >= b.date_point
                    group by i.origin_id ) k
                  where k.origin_id = t.origin_id
                  and k.revision = t.revision ) as contributors,
              _etalon_id as etalon_id
        from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from
                ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1
                  where not exists
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t
                    where t1.origin_id = origin_id
                    and t1.revision < revision
                    and coalesce(t1.valid_from, '-infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists (
                    select true from origins o1, origins o2, t
                    where
                        t1.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t1.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t1.last_update -- theoretically impossible to have it null
                    and (coalesce(t1.valid_from, '-infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity'))
                  )
                  union
                  select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null
                  and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to)
                  and not exists
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t
                    where t2.origin_id = origin_id
                    and t2.revision < revision
                    and t2.valid_to between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists
                  ( select true from origins o1, origins o2, t
                    where
                        t2.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t2.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t2.last_update -- theoretically impossible to have it null
                    and (t2.valid_to between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')))
                  ) _a
                  order by date_point asc ) as a,
             ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
                ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  where not exists
                  ( select true from t
                    where t1.origin_id = origin_id
                    and t1.revision < revision
                    and coalesce(t1.valid_to, 'infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists
                  ( select true from origins o1, origins o2, t
                    where
                        t1.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t1.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t1.last_update -- theoretically impossible to have it null
                    and (coalesce(t1.valid_to, 'infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
                  union
                  select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
                  and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from)
                  and not exists
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t
                    where t2.origin_id = origin_id
                    and t2.revision < revision
                    and t2.valid_from between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists
                  ( select true from origins o1, origins o2, t
                    where
                        t2.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t2.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t2.last_update -- theoretically impossible to have it null
                    and (t2.valid_from between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')))
                   ) _b
                  order by date_point asc ) as b
        where a.block_id = b.block_id
        and (a.date_point <= _ts and b.date_point >= _ts)
    ) q, etalons e
    where e.id = q.etalon_id;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;
-----------------------------------------------------------------------------------
drop function if exists fetch_records_timeline_intervals(_etalon_id character(36), _user_name character varying(256), _is_approver boolean);
-- fn
create or replace function ud_fetch_records_timeline_intervals(_etalon_id uuid, _user_name character varying(256), _is_approver boolean)
returns table(
    etalon_id uuid,
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[]) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval, owner, last_update) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_vistory v,
        (   select  i.origin_id,
            max(i.revision) as revision
            from origins o, origins_vistory i
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (
            select max(i.revision) from origins_vistory i
            where i.origin_id = t.origin_id
            and i.status <> 'MERGED'
            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity')
              or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
            and i.revision < t.revision
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
        ))
    --------------------- Join sorted
    select _etalon_id,
           case when a.date_point = '-infinity' then null else a.date_point end as valid_from,
           case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
           ( select array_agg(
                row( t.origin_id,
                     t.revision,
                     (select source_system from origins where id = t.origin_id),
                     t.status,
                     t.approval,
                     t.owner,
                     to_char(t.last_update, 'YYYY-MM-DD HH24:MI:SS.MS'))::text ) from t,
              ( select i.origin_id,
                max(i.revision) as revision
                from t i
                where coalesce(i.valid_from, '-infinity') <= a.date_point
                and coalesce(i.valid_to, 'infinity') >= b.date_point
                group by i.origin_id ) k
              where k.origin_id = t.origin_id
              and k.revision = t.revision ) as contributors
    from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from
            ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1
              where not exists
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t
                where t1.origin_id = origin_id
                and t1.revision < revision
                and coalesce(t1.valid_from, '-infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists
              ( select true from origins o1, origins o2, t
                where
                    t1.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t1.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t1.last_update -- theoretically impossible to have it null
                and (coalesce(t1.valid_from, '-infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
              union
              select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null
              and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to)
              and not exists
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t
                where t2.origin_id = origin_id
                and t2.revision < revision
                and t2.valid_to between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists
              ( select true from origins o1, origins o2, t
                where
                    t2.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t2.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t2.last_update -- theoretically impossible to have it null
                and (t2.valid_to between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
              ) _a
              order by date_point asc ) as a,
         ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
            ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1
              -- cut off garbage revisions from the same oid which were selected by one active end
              where not exists
              ( select true from t
                where t1.origin_id = origin_id
                and t1.revision < revision
                and coalesce(t1.valid_to, 'infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists
              ( select true from origins o1, origins o2, t
                where
                    t1.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t1.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t1.last_update -- theoretically impossible to have it null
                and (coalesce(t1.valid_to, 'infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
              union
              select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
              and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from)
              and not exists
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t
                where t2.origin_id = origin_id
                and t2.revision < revision
                and t2.valid_from between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists
              ( select true from origins o1, origins o2, t
                where
                    t2.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t2.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t2.last_update -- theoretically impossible to have it null
                and (t2.valid_from between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
              ) _b
              order by date_point asc ) as b
    where a.block_id = b.block_id;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;
-----------------------------------------------------------------------------------
-- drop fn
drop function if exists fetch_relations_etalon_boundary(_etalon_id character(36), _ts timestamp with time zone, _user_name character varying(256), _is_approver boolean);
-----------------------------------------------------------------------------------
-- create fn
create or replace function ud_fetch_relations_etalon_boundary(
    _etalon_id uuid,
    _ts timestamp with time zone,
    _user_name character varying(256),
    _is_approver boolean)
returns table(
    etalon_id uuid,
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[],
    created_by character varying(256),
    updated_by character varying(256),
    create_date timestamp with time zone,
    update_date timestamp with time zone,
    name character varying(256),
    status record_status,
    approval approval_state,
    etalon_gsn bigint) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval, owner, last_update) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_relations_vistory v,
        (   select  i.origin_id,
            max(i.revision) as revision
            from origins_relations o, origins_relations_vistory i
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
            from origins_relations_vistory v, t
            where v.origin_id = t.origin_id
            and v.revision = (
                select max(i.revision) from origins_relations_vistory i
                where i.origin_id = t.origin_id
                and i.status <> 'MERGED'
                and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity')
                or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
                and i.revision < t.revision
                and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            )
        )

    --------------------- Join sorted
    select e.id, q.valid_from, q.valid_to, q.contributors, e.created_by,
        (select v.created_by from origins_relations_vistory v, origins_relations o
         where v.origin_id = o.id and o.etalon_id = e.id and (v.approval <> 'DECLINED' and (v.approval <> 'PENDING' or (_is_approver or v.created_by = _user_name)))
         order by v.create_date desc fetch first 1 rows only) as updated_by,
        (select v.create_date from origins_relations_vistory v, origins_relations o
         where v.origin_id = o.id and o.etalon_id = e.id
         order by v.create_date asc fetch first 1 rows only) as create_date,
        (select v.create_date from origins_relations_vistory v, origins_relations o
         where v.origin_id = o.id and o.etalon_id = e.id and (v.approval <> 'DECLINED' and (v.approval <> 'PENDING' or (_is_approver or v.created_by = _user_name)))
         order by v.create_date desc fetch first 1 rows only) as update_date,
        e.name,
        e.status,
        e.approval,
        e.gsn as etalon_gsn
    from (
        select case when a.date_point = '-infinity' then null else a.date_point end as valid_from,
               case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
               ( select array_agg(
                  row( t.origin_id,
                       t.revision,
                       (select source_system from origins_relations where id = t.origin_id),
                       t.status,
                       t.approval,
                       t.owner,
                       to_char(t.last_update, 'YYYY-MM-DD HH24:MI:SS.MS'))::text) from t,
                  ( select i.origin_id,
                    max(i.revision) as revision
                    from t i
                    where coalesce(i.valid_from, '-infinity') <= a.date_point
                    and coalesce(i.valid_to, 'infinity') >= b.date_point
                    group by i.origin_id ) k
                  where k.origin_id = t.origin_id
                  and k.revision = t.revision ) as contributors,
               _etalon_id as etalon_id
        from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from
                ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1
                  where not exists
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t
                    where t1.origin_id = origin_id
                    and t1.revision < revision
                    and coalesce(t1.valid_from, '-infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists (
                    select true from origins o1, origins o2, t
                    where
                        t1.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t1.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t1.last_update -- theoretically impossible to have it null
                    and (coalesce(t1.valid_from, '-infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
                  union
                  select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null
                  and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to)
                  and not exists
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t
                    where t2.origin_id = origin_id
                    and t2.revision < revision
                    and t2.valid_to between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists
                  ( select true from origins o1, origins o2, t
                    where
                        t2.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t2.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t2.last_update -- theoretically impossible to have it null
                    and (t2.valid_to between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')))
                  ) _a
                  order by date_point asc ) as a,
             ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
                ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  where not exists
                  ( select true from t
                    where t1.origin_id = origin_id
                    and t1.revision < revision
                    and coalesce(t1.valid_to, 'infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists
                  ( select true from origins o1, origins o2, t
                    where
                        t1.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t1.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t1.last_update -- theoretically impossible to have it null
                    and (coalesce(t1.valid_to, 'infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
                  union
                  select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
                  and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from)
                  and not exists
                  -- cut off garbage revisions from the same oid which were selected by one active end
                  ( select true from t
                    where t2.origin_id = origin_id
                    and t2.revision < revision
                    and t2.valid_from between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
                  -- cut off older revisions from the same source system and different oid
                  and not exists
                  ( select true from origins o1, origins o2, t
                    where
                        t2.origin_id <> t.origin_id
                    and o1.id = t.origin_id
                    and o2.id = t2.origin_id
                    and o1.source_system = o2.source_system
                    and t.last_update > t2.last_update -- theoretically impossible to have it null
                    and (t2.valid_from between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
                   ) _b
                  order by date_point asc ) as b
        where a.block_id = b.block_id
        and (a.date_point <= _ts and b.date_point >= _ts)
    ) q, etalons_relations e
    where e.id = q.etalon_id;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;
-----------------------------------------------------------------------------------
drop function if exists fetch_relations_timeline_intervals(_etalon_id character(36), _user_name character varying(256), _is_approver boolean);
-----------------------------------------------------------------------------------
-- fn
create or replace function ud_fetch_relations_timeline_intervals(
    _etalon_id uuid,
    _user_name character varying(256),
    _is_approver boolean)
returns table(
    etalon_id uuid,
    vf timestamp with time zone,
    vt timestamp with time zone,
    contributors text[]) as $$
begin
    return query
    --------------------- Recursive timeline
    with recursive t (origin_id, valid_from, valid_to, revision, status, approval, owner, last_update) as (
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_relations_vistory v,
        (   select  i.origin_id,
            max(i.revision) as revision
            from origins_relations o, origins_relations_vistory i
            where o.etalon_id = _etalon_id
            and i.origin_id = o.id
            and i.status <> 'MERGED'
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            group by i.origin_id
        ) as s
        where v.origin_id = s.origin_id
        and v.revision = s.revision
        --------------------- Recursive sub select without duplicates
        union
        select v.origin_id, v.valid_from, v.valid_to, v.revision, v.status, v.approval, v.created_by, v.create_date
        from origins_relations_vistory v, t
        where v.origin_id = t.origin_id
        and v.revision = (
            select max(i.revision) from origins_relations_vistory i
            where i.origin_id = t.origin_id
            and i.status <> 'MERGED'
            and (coalesce(i.valid_from, '-infinity') < coalesce(t.valid_from, '-infinity')
            or coalesce(i.valid_to, 'infinity') > coalesce(t.valid_to, 'infinity'))
            and i.revision < t.revision
            and (i.approval <> 'DECLINED' and (i.approval <> 'PENDING' or (_is_approver or i.created_by = _user_name)))
            )
        )

    --------------------- Join sorted
    select _etalon_id,
           case when a.date_point = '-infinity' then null else a.date_point end as valid_from,
           case when b.date_point = 'infinity' then null else b.date_point end as valid_to,
           ( select array_agg(
              row( t.origin_id,
                   t.revision,
                   (select source_system from origins_relations where id = t.origin_id),
                   t.status,
                   t.approval,
                   t.owner,
                   to_char(t.last_update, 'YYYY-MM-DD HH24:MI:SS.MS'))::text ) from t,
              ( select i.origin_id,
                max(i.revision) as revision
                from t i
                where coalesce(i.valid_from, '-infinity') <= a.date_point
                and coalesce(i.valid_to, 'infinity') >= b.date_point
                group by i.origin_id ) k
              where k.origin_id = t.origin_id
              and k.revision = t.revision ) as contributors
    from ( select _a.date_point, row_number() over (order by _a.date_point asc) as block_id from
            ( select coalesce(t1.valid_from, '-infinity') as date_point from t t1
              where not exists
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t
                where t1.origin_id = origin_id
                and t1.revision < revision
                and coalesce(t1.valid_from, '-infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists (
                select true from origins o1, origins o2, t
                where
                    t1.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t1.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t1.last_update -- theoretically impossible to have it null
                and (coalesce(t1.valid_from, '-infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
              union
              select t2.valid_to + interval '0.001 seconds' as date_point from t t2 where t2.valid_to is not null
              and exists (select true from t t3 where coalesce(t3.valid_to, 'infinity') > t2.valid_to)
              and not exists
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t
                where t2.origin_id = origin_id
                and t2.revision < revision
                and t2.valid_to between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists
              ( select true from origins o1, origins o2, t
                where
                    t2.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t2.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t2.last_update -- theoretically impossible to have it null
                and (t2.valid_to between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')))
              ) _a
              order by date_point asc ) as a,
         ( select _b.date_point, row_number() over (order by _b.date_point asc) as block_id from
            ( select coalesce(t1.valid_to, 'infinity') as date_point from t t1
              -- cut off garbage revisions from the same oid which were selected by one active end
              where not exists
              ( select true from t
                where t1.origin_id = origin_id
                and t1.revision < revision
                and coalesce(t1.valid_to, 'infinity') between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists
              ( select true from origins o1, origins o2, t
                where
                    t1.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t1.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t1.last_update -- theoretically impossible to have it null
                and (coalesce(t1.valid_to, 'infinity') between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')) )
              union
              select t2.valid_from - interval '0.001 seconds' as date_point from t t2 where t2.valid_from is not null
              and exists (select true from t t3 where coalesce(t3.valid_from, '-infinity') < t2.valid_from)
              and not exists
              -- cut off garbage revisions from the same oid which were selected by one active end
              ( select true from t
                where t2.origin_id = origin_id
                and t2.revision < revision
                and t2.valid_from between coalesce(valid_from, '-infinity') and coalesce(valid_to, 'infinity') )
              -- cut off older revisions from the same source system and different oid
              and not exists
              ( select true from origins o1, origins o2, t
                where
                    t2.origin_id <> t.origin_id
                and o1.id = t.origin_id
                and o2.id = t2.origin_id
                and o1.source_system = o2.source_system
                and t.last_update > t2.last_update -- theoretically impossible to have it null
                and (t2.valid_from between coalesce(t.valid_from, '-infinity') and coalesce(t.valid_to, 'infinity')))
               ) _b
              order by date_point asc ) as b
    where a.block_id = b.block_id;
    --------------------- End of recursive timeline
end;
$$ language plpgsql;
-----------------------------------------------------------------------------------