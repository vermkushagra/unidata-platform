-- BLOBs
drop table if exists binary_data;
create table binary_data (
	id serial,
	etalon_id character(36),
	origin_id character(36),
	data bytea,
	field character varying(1024),
	filename character varying(2048),
	mime_type character varying(512),
	create_date timestamp without time zone NOT NULL DEFAULT now(),
	update_date timestamp without time zone,
	created_by character varying(512) NOT NULL,
	updated_by character varying(512),
	constraint pk_binary_data primary key (id),
	constraint fk_etalons_binary_data foreign key (etalon_id) references etalons (id) match full,
	constraint fk_origins_binary_data foreign key (origin_id) references origins (id) match full,
	constraint uq_binary_data unique (etalon_id, origin_id, field)
);
-- CLOBs
drop table if exists character_data;
create table character_data (
	id serial,
	etalon_id character(36),
	origin_id character(36),
	field character varying(1024),
	data text,
	filename character varying(2048),
	mime_type character varying(512),
	create_date timestamp without time zone NOT NULL DEFAULT now(),
	update_date timestamp without time zone,
	created_by character varying(512) NOT NULL,
	updated_by character varying(512),
	constraint pk_character_data primary key (id),
	constraint fk_etalons_character_data foreign key (etalon_id) references etalons (id) match full,
	constraint fk_origins_character_data foreign key (origin_id) references origins (id) match full,
	constraint uq_character_data unique (etalon_id, origin_id, field)
);