set session schema 'unidata_work_flow';

update ACT_GE_PROPERTY set VALUE_ = '5.21.0.0' where NAME_ = 'schema.version';

set session schema 'public';
