-- Clean etalons relations without one of etalon
delete from etalons_relations where
  etalon_id_from not in (select id from etalons) or
  etalon_id_to not in (select id from etalons);

-- Add constraints to etalons relations
alter table public.etalons_relations
  add constraint fk_etalons_relations_etalon_id_from foreign key (etalon_id_from) references public.etalons (id) on update cascade on delete cascade;

alter table public.etalons_relations
  add constraint fk_etalons_relations_etalon_id_to foreign key (etalon_id_to) references public.etalons (id) on update cascade on delete cascade;


-- Clean origins relations without one of origin
delete from origins_relations where
  origin_id_from not in (select id from origins) or
  origin_id_to not in (select id from origins);

-- Add constraints to origins relations
alter table public.origins_relations
  add constraint fk_origin_relations_origin_id_from foreign key (origin_id_from) references public.origins (id) on update cascade on delete cascade;

alter table public.origins_relations
  add constraint k_origin_relations_origin_id_to foreign key (origin_id_to) references public.origins (id) on update cascade on delete cascade;