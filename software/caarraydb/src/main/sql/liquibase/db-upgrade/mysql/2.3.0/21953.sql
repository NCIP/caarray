-- GF 21953 - external sample id refactoring
alter table biomaterial change column external_sample_id external_id varchar(254);