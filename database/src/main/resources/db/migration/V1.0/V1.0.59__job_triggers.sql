CREATE TABLE job_trigger (
  id            BIGSERIAL UNIQUE         NOT NULL,
  finish_job_id BIGINT                   NOT NULL,
  start_job_id  BIGINT                   NOT NULL,
  success_rule  BOOLEAN                  NOT NULL DEFAULT TRUE,
  name          VARCHAR(100)             NOT NULL,
  description   VARCHAR(255),
  create_date   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  update_date   TIMESTAMP WITH TIME ZONE,
  created_by    CHARACTER VARYING(256)   NOT NULL,
  updated_by    CHARACTER VARYING(256),
  CONSTRAINT FINISH_JOB_ID_FK FOREIGN KEY (finish_job_id) REFERENCES job (id),
  CONSTRAINT START_JOB_ID_FK FOREIGN KEY (start_job_id) REFERENCES job (id)
);
