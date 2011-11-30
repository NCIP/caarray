-- file storage changes

-- new unified multipart blob columns
create table multipart_blob (id bigint not null auto_increment, uncompressed_size bigint not null, compressed_size integer not null, creation_timestamp datetime not null, primary key (id)) type=InnoDB;
create table multipart_blob_blob_parts (multipart_blob bigint not null, blob_parts bigint not null, contents_index integer not null, primary key (multipart_blob, contents_index), unique (blob_parts)) type=InnoDB;
alter table multipart_blob_blob_parts add index FK426B680D3BB658DD (multipart_blob), add constraint FK426B680D3BB658DD foreign key (multipart_blob) references multipart_blob (id);
alter table multipart_blob_blob_parts add index FK426B680DE313AEE3 (blob_parts), add constraint FK426B680DE313AEE3 foreign key (blob_parts) references blob_holder (id);

-- temporary column to link back to origin of multipart blob
alter table multipart_blob add column source bigint; 

-- migrate datacolumn blobs
alter table datacolumn add column data_handle varchar(255);
create index idx_handle on datacolumn (data_handle);
insert into multipart_blob(uncompressed_size, compressed_size, creation_timestamp, source) select 0, 0, now(), id from datacolumn; 
update datacolumn,multipart_blob set data_handle = concat('db-multipart:', multipart_blob.id) where datacolumn.id = multipart_blob.source; 
insert into multipart_blob_blob_parts(multipart_blob, blob_parts, contents_index) 
  select mpb.id, dcbp.blob_parts, dcbp.contents_index 
    from multipart_blob mpb join datacolumn_blob_parts dcbp on mpb.source = dcbp.datacolumn;  
drop table datacolumn_blob_parts;

-- remove link for datacolumns so it doesn't confuse the file migration
update multipart_blob set source = null;

-- migrate file blobs
alter table caarrayfile add column data_handle varchar(255);
create index idx_handle on caarrayfile (data_handle);
insert into multipart_blob(uncompressed_size, compressed_size, creation_timestamp, source) select compressed_size, uncompressed_size, now(), id from caarrayfile; 
update caarrayfile,multipart_blob set data_handle = concat('db-multipart:', multipart_blob.id) where caarrayfile.id = multipart_blob.source; 
alter table caarrayfile modify data_handle varchar(255) not null;
insert into multipart_blob_blob_parts(multipart_blob, blob_parts, contents_index) 
  select mpb.id, fbp.blob_parts, fbp.contents_index 
    from multipart_blob mpb join caarrayfile_blob_parts fbp on mpb.source = fbp.caarrayfile;  
drop table caarrayfile_blob_parts;

alter table multipart_blob drop column source; 
