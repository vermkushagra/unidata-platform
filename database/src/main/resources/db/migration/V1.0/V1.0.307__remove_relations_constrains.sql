-- Drop constraints to etalons relations
alter table public.etalons_relations
  drop constraint fk_etalons_relations_etalon_id_from;

alter table public.etalons_relations
  drop constraint fk_etalons_relations_etalon_id_to;

-- Drop constraints to etalons relations
alter table public.origins_relations
  drop constraint fk_origin_relations_origin_id_from;

alter table public.origins_relations
  drop constraint k_origin_relations_origin_id_to;
