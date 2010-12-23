alter table designelementlist_designelement drop primary key;
alter table designelementlist_designelement add column id bigint not null auto_increment, add primary key(id);