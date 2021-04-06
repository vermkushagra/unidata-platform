DROP TABLE job_parameter;
DROP TABLE job_template;

CREATE TABLE job (
  id                    bigserial UNIQUE NOT NULL,
  name                  VARCHAR(100) UNIQUE NOT NULL,
  enabled               boolean default true NOT NULL,
  cron_expr             text,
  job_name_ref          text NOT NULL,
  descr                 text,
  create_date           timestamp with time zone not null default now(),
  update_date           timestamp with time zone,
  created_by            character varying(256) not null,
  updated_by            character varying(256)
);

CREATE TABLE job_parameter (
  id                    bigserial UNIQUE not null,
  job_id                bigint not null,
  name                  text not null,
  value                 text,
  create_date           timestamp with time zone not null default now(),
  update_date           timestamp with time zone,
  created_by            character varying(256) not null,
  updated_by            character varying(256),
  constraint fk_job_id foreign key (job_id) references job (id)
);
