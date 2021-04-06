alter table job add column state text;

update job set state =
    case when enabled = true then 'ENABLED'
    ELSE 'DISABLED' END;


alter table job ALTER COLUMN state SET NOT NULL;
alter table job DROP COLUMN enabled;
