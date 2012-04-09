alter table caarrayfile add column partial_size integer not null;
alter table caarrayfile modify column uncompressed_size bigint not null;
alter table caarrayfile modify column compressed_size bigint not null;