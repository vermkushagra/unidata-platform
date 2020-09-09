DELETE FROM job_trigger USING job_trigger jt2
WHERE job_trigger.name = jt2.name AND job_trigger.id > jt2.id;

ALTER TABLE job_trigger ADD CONSTRAINT name_trigger_unique UNIQUE (name);