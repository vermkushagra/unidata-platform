-- origins
alter table origins add column valid_from timestamp without time zone default null;
alter table origins add column valid_to timestamp without time zone default null;
alter table origins add column revision smallint default 1;
