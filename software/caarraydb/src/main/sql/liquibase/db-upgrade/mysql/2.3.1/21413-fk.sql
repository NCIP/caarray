-- GF 21413, remove Project.hostProfile - keys
alter table project drop foreign key project_hostaccess_fk;
alter table project drop key project_hostaccess_fk;
