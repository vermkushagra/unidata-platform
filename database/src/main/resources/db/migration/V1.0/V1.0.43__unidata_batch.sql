CREATE TABLE job_template (
  id                     SERIAL UNIQUE NOT NULL,
  job_template_name      VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE job_parameter (
  id                    BIGSERIAL UNIQUE NOT NULL,
  job_template_id       INTEGER not NULL,
  job_parameter_name    VARCHAR(100) NOT NULL,
  val_string            VARCHAR(250),
  val_date              TIMESTAMPTZ,
  val_long              BIGINT,
  val_double            DOUBLE PRECISION
);

ALTER TABLE job_parameter ADD CONSTRAINT fk_job_template_id FOREIGN KEY ( job_template_id ) REFERENCES job_template( id );
