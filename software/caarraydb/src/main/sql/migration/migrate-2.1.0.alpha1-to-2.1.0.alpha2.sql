-- dev team change request 13129
create table datacolumn_blob_parts (datacolumn bigint not null, blob_parts bigint not null, contents_index integer not null, primary key (datacolumn, contents_index), unique (blob_parts)) type=InnoDB;

create temporary table dc_blob_stage (id bigint primary key auto_increment, dc_id bigint, contents longblob);

-- marker row
insert into dc_blob_stage (id, dc_id, contents) select max(id)+1, null, null from blob_holder;
-- copy from datacolumn
insert into dc_blob_stage (contents, dc_id) select serialized_values, id from datacolumn where serialized_values is not null;
-- create linkages
insert into datacolumn_blob_parts (datacolumn, blob_parts, contents_index) select dc_id, id, 0 from dc_blob_stage where dc_id is not null;
-- punt from data columns
alter table datacolumn drop column serialized_values;
-- copy to blob_holder
insert into blob_holder(id, contents) select id, contents from dc_blob_stage where dc_id is not null;
-- add the constraints
alter table datacolumn_blob_parts add index FKA09E0DDD964C4BEA (datacolumn), add constraint FKA09E0DDD964C4BEA foreign key (datacolumn) references datacolumn (id);
alter table datacolumn_blob_parts add index FKA09E0DDDE313AEE3 (blob_parts), add constraint FKA09E0DDDE313AEE3 foreign key (blob_parts) references blob_holder (id);
-- clean up
drop table dc_blob_stage;


-- defect 12976
drop table experimentarray;
