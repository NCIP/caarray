-- GF 21413, remove Project.hostProfile
alter table project drop key project_hostaccess_fk;
alter table project drop foreign key project_hostaccess_fk;

alter table project drop key PROJECT_HOSTACCESS_FK;
alter table project drop foreign key PROJECT_HOSTACCESS_FK;
