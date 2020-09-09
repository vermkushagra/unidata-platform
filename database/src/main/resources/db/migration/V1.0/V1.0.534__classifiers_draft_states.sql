DO $$
begin
  if not exists(select 1 from schema_version where script ilike '%classifiers_draft_states%') then
  
    drop table if exists public.etalons_classifiers_draft_states;
    create table public.etalons_classifiers_draft_states
    (
      id bigserial NOT null,
      etalon_id uuid not null,
      revision integer not null,
      status record_status not null,
      create_date timestamp with time zone not null default now(),
      created_by character varying(512) not null,
      constraint etalons_classifiers_draft_states_pkey primary key (id)
    );

    alter table public.etalons_classifiers_draft_states owner to postgres;

    -- Fix etalons draft states index
    drop index if exists ix_etalons_draft_states_etalon_id;
    create index ix_etalons_draft_states_etalon_id
        on public.etalons_draft_states using btree (etalon_id);
    
    drop index if exists ix_etalons_classifiers_draft_states_etalon_id;
    create index ix_etalons_classifiers_draft_states_etalon_id
        on public.etalons_classifiers_draft_states
        using btree (etalon_id);

  end if;
END$$;
