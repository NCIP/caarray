-- file storage changes
alter table datacolumn add column data_handle varchar(255) not null;
create index idx_handle on datacolumn (data_handle);
-- TODO: migrate old blobs
drop table datacolumn_blob_parts;

alter table caarrayfile add column data_handle varchar(255) not null;
create index idx_handle on caarrayfile (data_handle);
-- TODO: migrate old blobs
drop table caarrayfile_blob_parts;

create table multipart_blob (id bigint not null auto_increment, uncompressed_size bigint not null, compressed_size integer not null, creation_timestamp datetime not null, primary key (id)) type=InnoDB;
create table multipart_blob_blob_parts (multipart_blob bigint not null, blob_parts bigint not null, contents_index integer not null, primary key (multipart_blob, contents_index), unique (blob_parts)) type=InnoDB;
alter table multipart_blob_blob_parts add index FK426B680D3BB658DD (multipart_blob), add constraint FK426B680D3BB658DD foreign key (multipart_blob) references multipart_blob (id);
alter table multipart_blob_blob_parts add index FK426B680DE313AEE3 (blob_parts), add constraint FK426B680DE313AEE3 foreign key (blob_parts) references blob_holder (id);
