-- origins relations vistory
alter table if exists origins_relations_vistory drop constraint fk_origins_relations_origin_id;
alter table origins_relations_vistory add constraint fk_origins_relations_origin_id FOREIGN KEY (origin_id)
      references origins_relations (id) match full on update no action on delete cascade;
-- origins relations
alter table if exists origins_relations drop constraint fk_origins_relations_origin_id_from;
alter table origins_relations add constraint fk_origins_relations_origin_id_from foreign key (origin_id_from)
      references origins (id) match full on update no action on delete cascade;
alter table if exists origins_relations drop constraint fk_origins_relations_origin_id_to;
alter table origins_relations add constraint fk_origins_relations_origin_id_to foreign key (origin_id_to)
      references origins (id) match full on update no action on delete cascade;
-- etalons relations
alter table if exists etalons_relations drop constraint fk_etalons_relations_etalon_id_from;
alter table etalons_relations add constraint fk_etalons_relations_etalon_id_from foreign key (etalon_id_from)
      references etalons (id) match full on update no action on delete cascade;
alter table if exists etalons_relations drop constraint fk_etalons_relations_etalon_id_to;
alter table etalons_relations add constraint fk_etalons_relations_etalon_id_to foreign key (etalon_id_to)
      references etalons (id) match full on update no action on delete cascade;

     