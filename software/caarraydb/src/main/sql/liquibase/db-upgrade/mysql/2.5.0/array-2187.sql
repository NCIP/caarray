-- add new column "parent_id" to the caarrayfile table.
alter table caarrayfile add column parent_id bigint;
alter table caarrayfile add index caarrayfile_parent_fk (parent_id), add constraint caarrayfile_parent_fk foreign key (parent_id) references caarrayfile (id);
