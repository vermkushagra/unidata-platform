alter table job add column enabled boolean;
alter table job add column error boolean default false not null;

update job set enabled =
    case when state = 'ENABLED' then true
    ELSE false END;

update job set error = true where state = 'ERROR';

alter table job ALTER COLUMN enabled SET NOT NULL;
alter table job DROP COLUMN state;
