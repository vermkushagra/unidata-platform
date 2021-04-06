
CREATE TABLE JOB_BATCH_JOB_INSTANCE (
    JOB_ID BIGINT NOT NULL,
    JOB_INSTANCE_ID BIGINT NOT NULL,
    create_date           timestamp with time zone not null default now(),
    update_date           timestamp with time zone,
    created_by            character varying(256) not null,
    updated_by            character varying(256),
    constraint FK_JOB_ID foreign key (JOB_ID) references JOB(ID),
    constraint FK_BATCH_JOB_INSTANCE_ID foreign key (JOB_INSTANCE_ID)
        references unidata_batch_job.BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
);

