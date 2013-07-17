alter table experiment add column version bigint;
update experiment set version=1;