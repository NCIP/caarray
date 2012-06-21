-- GF 21413, remove Project.hostProfile - keys
alter table project drop foreign key PROJECT_HOSTACCESS_FK;
alter table project drop key PROJECT_HOSTACCESS_FK;
