-- Duplicates table
create table if not exists duplicates (
	master_id char(36),
	duplicate_id char(36),
	constraint duplicates_pkey primary key (master_id, duplicate_id)
)
with (oids = false);

