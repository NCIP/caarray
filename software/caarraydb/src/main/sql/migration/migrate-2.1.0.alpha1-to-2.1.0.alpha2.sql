-- dev team change request 13129
create table datacolumn_blob_parts (datacolumn bigint not null, blob_parts bigint not null, contents_index integer not null, primary key (datacolumn, contents_index), unique (blob_parts)) type=InnoDB;
alter table blob_holder add column tmp_dc_id bigint;
insert into blob_holder (contents, tmp_dc_id) select serialized_values, id from datacolumn where serialized_values is not null;
insert into datacolumn_blob_parts (datacolumn, blob_parts, contents_index) select tmp_dc_id, id, 0 from blob_holder where tmp_dc_id is not null;
alter table datacolumn_blob_parts add index FKA09E0DDD964C4BEA (datacolumn), add constraint FKA09E0DDD964C4BEA foreign key (datacolumn) references datacolumn (id);
alter table datacolumn_blob_parts add index FKA09E0DDDE313AEE3 (blob_parts), add constraint FKA09E0DDDE313AEE3 foreign key (blob_parts) references blob_holder (id);
alter table datacolumn drop column serialized_values;
alter table blob_holder drop column tmp_dc_id;
